package org.example.conditionVariables;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringJoiner;

// Example of Producer-Consumer pattern using Condition Variables in Java
// This example demonstrates a producer thread that reads pairs of matrices from a file
// and a consumer thread that multiplies these matrices and writes the results to another file.
// A thread-safe queue is used to facilitate communication between the producer and consumer threads.
// Backpressure is implemented to prevent the queue from growing indefinitely.
// The producer reads matrices from "matrices.txt" and the consumer writes results to "matrices-result.txt".
public class MainApplication {

    private static final String INPUT_FILE = "matrices.txt";
    private static final String OUTPUT_FILE = "matrices-result.txt";
    private static final int N = 10;

    public static void main(String[] args) throws IOException {
        ThreadSafeQueue threadSafeQueue = new ThreadSafeQueue();
        File inputFile = new File(INPUT_FILE);
        File outputFile = new File(OUTPUT_FILE);

        MatricesReaderProducer producer = new MatricesReaderProducer(new FileReader(inputFile), threadSafeQueue);
        MatricesMultiplierConsumer consumer = new MatricesMultiplierConsumer(threadSafeQueue, new FileWriter(outputFile));

        Long startTime = System.currentTimeMillis();
        producer.start();
        consumer.start();
        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
        }
        Long endTime = System.currentTimeMillis();
        System.out.println("Total time: " + (endTime - startTime) + " ms");
    }

    // Consumer class that multiplies matrices and writes results to a file
    private static class MatricesMultiplierConsumer extends Thread {
        private ThreadSafeQueue threadSafeQueue;
        private FileWriter fileWriter;

        public MatricesMultiplierConsumer(ThreadSafeQueue threadSafeQueue, FileWriter fileWriter) {
            this.threadSafeQueue = threadSafeQueue;
            this.fileWriter = fileWriter;
        }

        // Save the resulting matrix to the output file
        private static void saveMatrixToFile(FileWriter fileWriter, float[][] matrix) throws IOException {
            for (int r = 0; r < N; r++) {
                StringJoiner stringJoiner = new StringJoiner(", ");
                for (int c = 0; c < N; c++) {
                    stringJoiner.add(String.format("%.2f", matrix[r][c]));
                }
                fileWriter.write(stringJoiner.toString());
                fileWriter.write('\n');
            }
            fileWriter.write('\n');
        }

        @Override
        public void run() {
            while (true) {
                MatricesPair matricesPair = threadSafeQueue.remove();
                if (matricesPair == null) {
                    System.out.println("No more matrices to process. Terminating consumer.");
                    break;
                }

                float[][] result = multiplyMatrices(matricesPair.matrixA, matricesPair.matrixB);

                try {
                    saveMatrixToFile(fileWriter, result);
                } catch (IOException e) {
                }
            }

            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Multiply two matrices
        private float[][] multiplyMatrices(float[][] matrixA, float[][] matrixB) {
            float[][] result = new float[N][N];
            for (int r = 0; r < N; r++) {
                for (int c = 0; c < N; c++) {
                    for (int k = 0; k < N; k++) {
                        result[r][c] += matrixA[r][k] * matrixB[k][c];
                    }
                }
            }
            return result;
        }
    }

    // Producer class that reads pairs of matrices from a file
    private static class MatricesReaderProducer extends Thread {
        private Scanner scanner;
        private ThreadSafeQueue threadSafeQueue;

        public MatricesReaderProducer(FileReader fileReader, ThreadSafeQueue threadSafeQueue) {
            this.scanner = new Scanner(fileReader);
            this.threadSafeQueue = threadSafeQueue;
        }

        @Override
        public void run() {
            while (true) {
                float[][] matrixA = readMatrix();
                float[][] matrixB = readMatrix();
                if (matrixA == null || matrixB == null) {
                    threadSafeQueue.terminate();
                    System.out.println("No more matrices to read. Terminating producer.");
                    return;
                }

                MatricesPair matricesPair = new MatricesPair();
                matricesPair.matrixA = matrixA;
                matricesPair.matrixB = matrixB;
                threadSafeQueue.add(matricesPair);
            }
        }

        // Read a single matrix from the input file
        private float[][] readMatrix() {
            float[][] matrix = new float[N][N];
            for (int r = 0; r < N; r++) {
                if (!scanner.hasNext()) {
                    return null;
                }
                String[] line = scanner.nextLine().split(",");
                for (int c = 0; c < N; c++) {
                    matrix[r][c] = Float.valueOf(line[c]);
                }
            }
            scanner.nextLine();
            return matrix;
        }
    }

    // Thread-safe queue implementation using synchronized methods and wait/notify
    private static class ThreadSafeQueue {
        private Queue<MatricesPair> queue = new LinkedList<>();
        private boolean isEmpty = true;
        private boolean isTerminate = false;

        //Introducing a max capacity for the queue to prevent unlimited growth
        private static final int MAX_CAPACITY = 5;

        // Add an item to the queue
        public synchronized void add(MatricesPair matricesPair) {
            while (queue.size() == MAX_CAPACITY) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
            queue.add(matricesPair);
            isEmpty = false;
            notify();
        }

        // Remove and return an item from the queue
        public synchronized MatricesPair remove() {
            // Wait while the queue is empty and not terminated
            MatricesPair matricesPair = null;
            while (isEmpty && !isTerminate) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
            if (queue.size() == 1) {
                isEmpty = true;
            }

            if (queue.size() == 0 && isTerminate) {
                return null;
            }
            System.out.println("queue size: " + queue.size());

            // Remove and return the next item from the queue
            // Notify producers if the queue was full
            matricesPair =  queue.remove();
            if (queue.size() == MAX_CAPACITY - 1) {
                notifyAll();
            }
            return matricesPair;
        }

        // Terminate the queue to signal no more items will be added
        public synchronized void terminate() {
            isTerminate = true;
            notifyAll();
        }
    }

    private static class MatricesPair {
        public float[][] matrixA;
        public float[][] matrixB;
    }
}

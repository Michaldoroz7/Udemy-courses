package org.example.coordination;


import java.math.BigInteger;
import java.util.List;

// Demonstrates coordinating threads using join
// Each thread computes the factorial of a number
public class ThreadWithJoin {
    public static void main(String[] args) throws InterruptedException {
        List<Long> inputNumbers = List.of(1000222222L, 20L, 300L, 400L, 50L);

        List<FactorialThread> threads = inputNumbers.stream()
                .map(FactorialThread::new)
                .toList();

        // Start all threads
        for (FactorialThread thread : threads) {
            thread.setDaemon(true);
            thread.start();
        }

        // Wait for each thread to finish with a timeout
        for (Thread thread : threads) {
            thread.join(2000); // Wait for up to 2 seconds
        }

        // Check if threads are finished
        for (FactorialThread thread : threads) {
            if (thread.isFinished()) {
                System.out.println("Thread for number " + thread.number + " is finished: ");
            } else {
                System.out.println("Thread for number " + thread.number + " still running...");
            }
        }

    }


    // A thread that computes the factorial of a number
    private static class FactorialThread extends Thread {
        private long number;
        private BigInteger result = BigInteger.ZERO;
        private boolean isFinished = false;

        // Constructor to initialize the number
        public FactorialThread(long number) {
            this.number = number;
        }

        @Override
        public void run() {
            this.result = computeFactorial(number);
            this.isFinished = true;
        }

        // Method to compute factorial
        private BigInteger computeFactorial(long number) {
            BigInteger result = BigInteger.ONE;
            for (long i = number; i > 0; i--) {
                result = result.multiply(new BigInteger(Long.toString(i)));
            }
            return result;
        }

        private boolean isFinished() {
            return isFinished;
        }

        public BigInteger getResult() {
            return result;
        }
    }
}

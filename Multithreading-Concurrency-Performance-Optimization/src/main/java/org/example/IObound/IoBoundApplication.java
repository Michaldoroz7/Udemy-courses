package org.example.IObound;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Example of Thread per task model where each task is performed by different thread
public class IoBoundApplication {

    private final static int NUMBER_OF_TASKS = 10_000;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Press enter to start");
        scanner.nextLine();

        System.out.println("Running number " + NUMBER_OF_TASKS + " tasks");


        long start = System.currentTimeMillis();
        performTasks();
        long endTime = System.currentTimeMillis();
        System.out.println("Tasks finished in: " + (endTime - start) + " ms");
    }

    private static void performTasks() {
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {

            for (int i = 0; i < NUMBER_OF_TASKS; i++) {
                //In this example we are submitting to run 100 blocking operations per thread which leads to longer time execution than task per thread
                //It's because most of additional time is spent on context switching of thread when its blocked
                //From the other side, when we are using task per thread approach it can cause application crash due to too large number of thread which are expensive in memory
//                 executorService.submit(() -> blockingIoOperation());
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        for (int j = 0; j < 100; j++) {
                            blockingIoOperation();
                        }
                    }
                });
            }
        }
    }


    //Simulates long blocking IO task
    private static void blockingIoOperation() {
        System.out.println("Executing blocking task from thread" + Thread.currentThread());

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }
}

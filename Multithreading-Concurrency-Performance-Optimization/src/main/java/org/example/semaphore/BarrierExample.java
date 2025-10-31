package org.example.semaphore;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Example of Barrier implementation using Semaphore in Java
// A Barrier is a synchronization mechanism that allows multiple threads to wait
// until all threads have reached a certain point of execution before any of them can proceed.
public class BarrierExample {
    public static void main(String[] args) {
        final int numberOfWorkers = 3;
        Barrier barrier = new Barrier(numberOfWorkers);

        for (int i = 0; i < numberOfWorkers; i++) {
            Thread workerThread = new Thread(new CoordinatedWorkRunner(barrier));
            workerThread.start();
        }
    }

    // Barrier class to synchronize multiple threads
    public static class Barrier {
        private final int numberOfWorkers;
        private final Semaphore semaphore = new Semaphore(0);
        private int counter = 0;
        private final Lock lock = new ReentrantLock();

        public Barrier(int numberOfWorkers) {
            this.numberOfWorkers = numberOfWorkers;
        }

        // Method for threads to wait for others at the barrier
        // When the last thread arrives, it releases all waiting threads
        public void waitForOthers() throws InterruptedException {
            lock.lock();
            boolean isLastWorker = false;
            try {
                counter++;

                if (counter == numberOfWorkers) {
                    isLastWorker = true;
                }
            } finally {
                lock.unlock();
            }

            if (isLastWorker) {
                semaphore.release(numberOfWorkers);
            } else {
                semaphore.acquire();
            }
        }
    }

    // Worker class that performs coordinated work using the Barrier
    // Each worker performs part 1 of the work, waits at the barrier,
    // and then performs part 2 of the work
    public static class CoordinatedWorkRunner implements Runnable {
        private final Barrier barrier;

        public CoordinatedWorkRunner(Barrier barrier) {
            this.barrier = barrier;
        }

        @Override
        public void run() {
            try {
                task();
            } catch (InterruptedException e) {
            }
        }

        private void task() throws InterruptedException {
            // Performing Part 1
            System.out.println(Thread.currentThread().getName()
                    + " part 1 of the work is finished");

            barrier.waitForOthers();

            // Performing Part2
            System.out.println(Thread.currentThread().getName()
                    + " part 2 of the work is finished");
        }
    }
}





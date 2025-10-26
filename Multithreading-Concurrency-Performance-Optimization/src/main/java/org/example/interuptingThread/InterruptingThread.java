package org.example.interuptingThread;

import java.math.BigInteger;

// Demonstrates interrupting a blocking thread
public class InterruptingThread {
    public static void main(String[] args) {

        // Create and start a blocking thread
        // then interrupt it immediately
        // blocking thread will be in sleep state
        Thread thread = new Thread(new BlockingTask());
        thread.start();
        thread.interrupt();

        Thread computationThread = new Thread(
                new LongComputationTask(
                        new BigInteger("2"),
                        new BigInteger("10")
                )
        );
        // Set as daemon so it doesn't block JVM exit
        // setting a Daemon thread allows the JVM to exit even if this thread is still running
        computationThread.setDaemon(true);
        computationThread.start();
        computationThread.interrupt();

    }

    // A task that simulates blocking operation
    private static class BlockingTask implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(50000);
            } catch (InterruptedException e) {
                System.out.println("Entering blocking thread");
            }
        }
    }

    // A task that performs a long computation
    private static class LongComputationTask implements Runnable {

        private BigInteger base;
        private BigInteger power;

        public LongComputationTask(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {
            System.out.println(base + "^" + power + " = " + pow(base, power));
        }

        private BigInteger pow(BigInteger base, BigInteger power) {
            BigInteger result = BigInteger.ONE;
            for (BigInteger i = BigInteger.ZERO; i.compareTo(power) != 0; i = i.add(BigInteger.ONE)) {
                // Check if the thread has been interrupted
                // If so, terminate the computation early
//                if (Thread.currentThread().isInterrupted()) {
//                    System.out.println("Computation interrupted");
//                    return BigInteger.ZERO;
//                }
                result = result.multiply(base);
            }

            return result;
        }
    }
}

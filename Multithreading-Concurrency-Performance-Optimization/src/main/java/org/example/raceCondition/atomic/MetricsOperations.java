package org.example.raceCondition.atomic;

import java.util.Random;

//Example of atomic operations to maintain metrics in multithreading environment
public class MetricsOperations {
    public static void main(String[] args) {

        // Metrics object shared across multiple business logic threads
        // Metrics operations are designed to be atomic to ensure consistency
        // even when accessed by multiple threads concurrently
        Metrics metrics = new Metrics();
        MetricsPrinter metricsPrinter = new MetricsPrinter(metrics);
        metricsPrinter.start();

        // Starting multiple business logic threads that record metrics
        for (int i = 0; i < 5; i++) {
            BusinessLogic businessLogic = new BusinessLogic(metrics);
            businessLogic.start();
        }
    }

    public static class MetricsPrinter extends Thread {
        private Metrics metrics;

        public MetricsPrinter(Metrics metrics) {
            this.metrics = metrics;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                System.out.println("Average time: " + metrics.getAverage());
            }
        }
    }

    // Business logic thread that simulates processing and records metrics
    public static class BusinessLogic extends Thread {
        private final Metrics metrics;
        private Random random = new Random();

        public BusinessLogic(Metrics metrics) {
            this.metrics = metrics;
        }

        @Override
        public void run() {
            while (true) {
                long start = System.currentTimeMillis();
                // Simulate business logic processing time
                try {
                    Thread.sleep(random.nextInt(10));
                } catch (InterruptedException e) {
                }
                long end = System.currentTimeMillis();
                metrics.addSample(end - start);
            }
        }

    }

    // Metrics class to maintain count and average of samples
    public static class Metrics {
        private long count = 0;
        //volatile ensures visibility of changes to variables across threads
        private volatile double average = 0.0;

        // Synchronized method to ensure atomicity of addSample operation
        public synchronized void addSample(long sample) {
            double currentSum = average * count;
            count++;
            average = (currentSum + sample) / count;
        }

        // Getter for average, no need to be synchronized due to volatile keyword which ensures visibility across threads
        public double getAverage() {
            return average;
        }
    }
}

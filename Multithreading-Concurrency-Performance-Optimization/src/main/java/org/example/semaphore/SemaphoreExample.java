package org.example.semaphore;


import java.util.concurrent.Semaphore;

// Example of Semaphore usage in Java
// A Semaphore is a synchronization mechanism that controls access to a shared resource
// by multiple threads. It maintains a set of permits, and threads can acquire or release permits
// to access the shared resource. Semaphores are useful in scenarios where you want to limit
// the number of threads that can access a resource concurrently.
public class SemaphoreExample {

    // Semaphores to control access between producer and consumer
    // One permit for producer, zero for consumer initially
    static Semaphore producerSemaphore = new Semaphore(1);
    static Semaphore consumerSemaphore = new Semaphore(0);

    public static void main(String[] args) {

        SharedResource sharedResource = new SharedResource();

        // Create producer and consumer threads
        // Two consumers to demonstrate multiple threads waiting
        Thread producerThread = new Thread(new Producer(sharedResource));
        Thread consumerThread = new Thread(new Consumer(sharedResource));
        Thread consumerThread2 = new Thread(new Consumer(sharedResource));


        producerThread.start();
        consumerThread.start();
        consumerThread2.start();

    }

    // Consumer class that uses the shared resource
    public static class Consumer implements Runnable {
        private final SharedResource sharedResource;

        public Consumer(SharedResource sharedResource) {
            this.sharedResource = sharedResource;
        }

        public void consume() throws InterruptedException {
            consumerSemaphore.acquire();
            sharedResource.useResource();
            producerSemaphore.release();
        }

        @Override
        public void run() {

            while (true) {
                try {
                    consume();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // Producer class that creates items for the shared resource
    public static class Producer implements Runnable {
        private final SharedResource sharedResource;

        public Producer(SharedResource sharedResource) {
            this.sharedResource = sharedResource;
        }

        public void produce() throws InterruptedException {
            producerSemaphore.acquire();
            sharedResource.createItem();
            System.out.println("Produced an item.");
            consumerSemaphore.release();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    produce();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    // Shared resource class
    public static class SharedResource {
        Object item;

        public void useResource() {
            // Simulate resource usage
            System.out.println("Using resource: " + item + " by " + Thread.currentThread().getName());
        }

        public void createItem() {
            item = new Object();
        }

    }
}

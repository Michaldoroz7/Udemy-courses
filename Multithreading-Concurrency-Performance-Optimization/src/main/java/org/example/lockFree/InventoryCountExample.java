package org.example.lockFree;

import java.util.concurrent.atomic.AtomicInteger;

// Example of lock-free inventory counter using AtomicInteger in Java
// This example demonstrates a simple inventory counter that allows
// multiple threads to increment and decrement the inventory count concurrently
// without using explicit locks. The AtomicInteger class provides
// atomic operations that ensure thread safety.
public class InventoryCountExample {
    public static void main(String[] args) {
        InventoryCounter inventoryCounter = new InventoryCounter();

        Thread incrementingThread = new IncrementingThread(inventoryCounter);
        Thread decrementingThread = new DecrementingThread(inventoryCounter);

        Long startTime = System.currentTimeMillis();
        incrementingThread.start();
        decrementingThread.start();

        try {
            incrementingThread.join();
            decrementingThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Long endTime = System.currentTimeMillis();

        //With synchronized increment and decrement methods total time: 17 ms
        //With AtomicInteger increment and decrement methods total time: 3 ms
        System.out.println("Total time: " + (endTime - startTime) + " ms");
        System.out.println("Final inventory count: " + inventoryCounter.getItems());

    }

    // Thread that decrements the inventory count
    public static class DecrementingThread extends Thread {
        private final InventoryCounter inventoryCounter;

        public DecrementingThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100000; i++) {
                inventoryCounter.decrement();
            }
        }
    }

    // Thread that increments the inventory count
    public static class IncrementingThread extends Thread {
        private final InventoryCounter inventoryCounter;

        public IncrementingThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100000; i++) {
                inventoryCounter.increment();
            }


        }
    }

    // Lock-free inventory counter using AtomicInteger
    private static class InventoryCounter {
        // AtomicInteger to hold the inventory count
        private AtomicInteger items = new AtomicInteger(0);

        // Increment the inventory count atomically
        public void increment() {
            items.incrementAndGet();
        }

        // Decrement the inventory count atomically
        public void decrement() {
            items.decrementAndGet();
        }

        // Get the current inventory count
        public int getItems() {
            return items.get();
        }
    }
}

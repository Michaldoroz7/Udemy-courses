package org.example.reentrantReadWriteLock;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// Reentrant Read-Write Lock is a synchronization mechanism that allows multiple threads
// to read a shared resource concurrently while ensuring exclusive access for write operations.
// This is useful in scenarios where read operations are more frequent than write operations,
// as it improves concurrency and performance by allowing multiple readers to access the resource simultaneously.
public class ReentrantReadWriteLockExample {
    public static final int HIGH_PRICE = 1000;

    public static void main(String[] args) {
        InventoryDatabase inventoryDatabase = new InventoryDatabase();
        Random random = new Random();

        // Simulate adding items to the inventory
        for (int i = 0; i < 5000; i++) {
            int price = random.nextInt(HIGH_PRICE);
            inventoryDatabase.addItem(price);
        }

        // Simulate a writer thread that adds and removes items
        Thread writer = new Thread(() ->  {
            while (true) {
                inventoryDatabase.addItem(random.nextInt(HIGH_PRICE));
                inventoryDatabase.removeItem(random.nextInt(HIGH_PRICE));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
        });

        // Setting the writer thread as a daemon thread to allow the program to exit gracefully
        writer.setDaemon(true);
        writer.start();

        int numberOfThreads = 8;
        List<Thread> threads = new ArrayList<>();

        for (int readerIndex = 0; readerIndex < numberOfThreads; readerIndex++) {
            Thread reader = new Thread(() -> {
               for (int i = 0; i < 10000; i++) {
                   int upperBound = random.nextInt(HIGH_PRICE);
                   int lowerBound = upperBound > 0 ? random.nextInt(upperBound) : 0;
                   inventoryDatabase.getNumberOfItemsInPriceRange(lowerBound, upperBound);
               }
            });

            reader.setDaemon(true);
            threads.add(reader);
        }

        long startTime = System.currentTimeMillis();
        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }
        long endTime = System.currentTimeMillis();

        //By setting lock in every method, our result is around 250ms for 8 threads without concurrent reads
        //By using ReentrantReadWriteLock, our result is around 60ms for 8 threads with concurrent reads
        System.out.println("Total time taken: " + (endTime - startTime) + " ms");

    }


    // Inventory Database that supports concurrent read and exclusive write operations
    public static class InventoryDatabase {
        private TreeMap<Integer, Integer> priceToCountMap = new TreeMap<>();

        // Reentrant Lock for synchronizing access to the inventory database
        private ReentrantLock lock = new ReentrantLock();

        // Reentrant Read-Write Lock for allowing concurrent reads and exclusive writes
        private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

        // Read and Write locks derived from the Reentrant Read-Write Lock
        private Lock readLock = readWriteLock.readLock();
        private Lock writeLock = readWriteLock.writeLock();

        // Get the number of items within a specified price range
        public int getNumberOfItemsInPriceRange(int lowerBound, int upperBound) {
            readLock.lock();
            try {
                Integer fromKey = priceToCountMap.ceilingKey(lowerBound);
                Integer toKey = priceToCountMap.floorKey(upperBound);

                if (fromKey == null || toKey == null) {
                    return 0;
                }

                // Get the sub-map of prices within the specified range
                NavigableMap<Integer, Integer> rangeOfPrices = priceToCountMap.subMap(fromKey, true, toKey, true);
                int sum = 0;
                for (int numberOfItemsForPrice : rangeOfPrices.values()) {
                    sum += numberOfItemsForPrice;
                }
                return sum;
            } finally {
                readLock.unlock();
            }

        }

        public void addItem(int price) {
            writeLock.lock();
            try {
                Integer numberOfItemsForPrice = priceToCountMap.get(price);
                if (numberOfItemsForPrice == null) {
                    priceToCountMap.put(price, 1);
                } else {
                    priceToCountMap.put(price, numberOfItemsForPrice + 1);
                }
            } finally {
                writeLock.unlock();
            }
        }

        public void removeItem(int price) {
            writeLock.lock();
            try {
                Integer numberOfItemsForPrice = priceToCountMap.get(price);
                if (numberOfItemsForPrice == null || numberOfItemsForPrice == 1) {
                    priceToCountMap.remove(price);
                } else {
                    priceToCountMap.put(price, numberOfItemsForPrice - 1);
                }
            } finally {
                writeLock.unlock();
            }
        }
    }
}

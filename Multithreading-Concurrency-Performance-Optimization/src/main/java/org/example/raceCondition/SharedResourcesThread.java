package org.example.raceCondition;

// Demonstrates race condition with shared resources in multithreading
// Two threads incrementing and decrementing a shared inventory counter
public class SharedResourcesThread {
    public static void main(String[] args) throws InterruptedException {

        //In current implementation there is a race condition on the items variable of IntentoryCounter class.
        //It causes inconsistent final value of items after both threads complete their execution.
        //To fix this, we can make increment() and decrement() methods synchronized to ensure atomicity of operations.
        //Issue there is that operations on items ++ and -- are not atomic operations. They consist of multiple steps (read, modify, write).
        //Atomic operations ensure that a variable is updated in a single step without interruption.
        IntentoryCounter intentoryCounter = new IntentoryCounter();
        IncrementingThread incrementingThread = new IncrementingThread(intentoryCounter);
        DecrementingThread decrementingThread = new DecrementingThread(intentoryCounter);

        incrementingThread.start();
        decrementingThread.start();

        incrementingThread.join();
        decrementingThread.join();

        System.out.println("Final items in inventory: " + intentoryCounter.getItems());
    }

    public static class DecrementingThread extends Thread {
        private final IntentoryCounter intentoryCounter;

        public DecrementingThread(IntentoryCounter intentoryCounter) {
            this.intentoryCounter = intentoryCounter;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                intentoryCounter.decrement();
            }
        }
    }

    public static class IncrementingThread extends Thread {
        private final IntentoryCounter intentoryCounter;

        public IncrementingThread(IntentoryCounter intentoryCounter) {
            this.intentoryCounter = intentoryCounter;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                intentoryCounter.increment();
            }
        }
    }

    private static class IntentoryCounter {
        private int items = 0;
        Object lock = new Object();

        public int getItems() {
            synchronized (lock) {
                return items;
            }
        }

        // Synchronized methods to ensure atomicity of increment and decrement operations
        // This prevents race conditions - only one thread can execute these methods at a time, if one thread is executing one of these methods, other threads trying to execute either method will be blocked until the first thread completes.
        public void increment() {
            synchronized (lock) {
                items++;
            }
        }

        // There is also possibility to use synchronized keywoard on a block level instead of method level.
        // It allows to synchronize only a specific section of code, rather than the entire method.
        // This can be useful for improving performance when only a small part of the method needs synchronization.
        public void decrement() {
            synchronized (lock) {
                items--;
            }
        }
    }
}


package org.example.dataRace;

// This code demonstrates a data race condition
// between two threads accessing shared variables without synchronization.

// Every shared variable which is modified by multiple threads should be guarded
// by synchronization mechanisms like synchronized blocks, locks, or atomic variables.
// or declared as volatile if only simple read/write operations are performed.
public class DataRace {
    public static void main(String[] args) {
        SharedClass sharedClass = new SharedClass();

        // Thread that increments shared variables
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                sharedClass.increment();
            }
        });

        // Thread that checks for data race condition
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                sharedClass.checkForDataRace();
            }
        });

        thread1.start();
        thread2.start();
    }

    // Shared class with two integer variables
    // accessed by multiple threads without synchronization
    public static class SharedClass {
        // by adding volatile we can make the data race less likely to occur
        private volatile int x = 0;
        private volatile int y = 0;

        public void increment() {
            x++;
            y++;
        }

        public void checkForDataRace() {
            if (y > x) {
                System.out.println("y > x - Data race detected!");
            }
        }
    }
}

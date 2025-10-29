package org.example.deadlock;


//Fine-Gained Locking to prevent deadlocks is a technique where locks are acquired in a specific order
//to avoid circular wait conditions that lead to deadlocks. By ensuring that all threads
//acquire locks in the same order, we can prevent deadlocks from occurring.

import java.util.Random;

//Coarse-Grained Locking involves using a single lock to protect multiple shared resources.
//While this approach can simplify the locking mechanism and reduce the chances of deadlocks,
//it can also lead to reduced concurrency and potential performance bottlenecks,
//as threads may be blocked waiting for the single lock even when they could safely access different resources

//Mutual Exclusion Locks (Mutexes) are synchronization primitives that allow only one thread
//to access a shared resource at a time. By using mutexes, we can prevent race conditions and ensure data consistency.

//Hold and Wait is a condition where a thread holds at least one lock and is waiting to acquire additional locks
//that are currently held by other threads. This condition can lead to deadlocks if not managed properly.

//Non-Preemptive Scheduling is a scheduling approach where a thread cannot be forcibly removed from the CPU
//until it voluntarily releases the CPU. This can lead to deadlocks if a thread holding a lock is waiting for another lock held by a thread that is not scheduled to run.

//Circular Wait is a condition where a set of threads are waiting for each other in a circular chain.
//For example, Thread A is waiting for a lock held by Thread B, Thread B is waiting for a lock held by Thread C,
//and Thread C is waiting for a lock held by Thread A. This condition can lead to deadlocks if not addressed.

//Avoiding Circular Wait by enforcing a strict order in which locks must be acquired can help prevent deadlocks.
//Other techniques:
//1. Watchdog Timers: Implementing timeouts for lock acquisition attempts can help detect and recover from deadlocks.
//2. Deadlock Detection Algorithms: Periodically checking for cycles in the resource allocation graph can help identify deadlocks and take corrective actions.
//3. Resource Hierarchies: Assigning a hierarchy to resources and ensuring that threads acquire locks in order of their hierarchy can prevent circular wait conditions.
//4. Lock Timeout: Implementing timeouts for lock acquisition attempts can help prevent threads from waiting indefinitely, reducing the chances of deadlocks.
//5. Try-Lock Mechanism: Using try-lock mechanisms that allow threads to attempt to acquire locks without blocking can help avoid deadlocks by allowing threads to back off and retry later if they cannot acquire all necessary locks.
public class deadLock {
    public static void main(String[] args) {
        Intersection intersection = new Intersection();

        Thread trainAThread = new Thread(new TrainA(intersection), "TrainA");
        Thread trainBThread = new Thread(new TrainB(intersection), "TrainB");

        trainAThread.start();
        trainBThread.start();

    }

    // TrainA tries to take Road A then Road B
    public static class TrainA implements Runnable {
        private final Intersection intersection;
        private Random random = new Random();

        public TrainA(Intersection intersection) {
            this.intersection = intersection;
        }

        @Override
        public void run() {
            while (true) {
                long sleepTime = random.nextInt(5);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                }
                intersection.takeRoadA();
            }
        }
    }

    // TrainB tries to take Road B then Road A
    public static class TrainB implements Runnable {
        private final Intersection intersection;
        private Random random = new Random();

        public TrainB(Intersection intersection) {
            this.intersection = intersection;
        }

        @Override
        public void run() {
            while (true) {
                long sleepTime = random.nextInt(5);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                }
                intersection.takeRoadB();
            }
        }
    }

    // Intersection class with two roads
    // synchronized to prevent deadlocks
    // by acquiring locks in a consistent order
    public static class Intersection {
        private Object roadA = new Object();
        private Object roadB = new Object();

        public void takeRoadA() {
            synchronized (roadA) {
                System.out.println("Road A is locked by thread " + Thread.currentThread().getName());
                synchronized (roadB) {
                    System.out.println("Crossing Intersection from Road A");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }

        public void takeRoadB() {
            synchronized (roadA) { // Changed order to prevent deadlock
                System.out.println("Road A is locked by thread " + Thread.currentThread().getName());
                synchronized (roadB) {
                    System.out.println("Crossing Intersection from Road B");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }
}

package org.example.byExtendingClass;

// Demonstrates creating a thread by extending the Thread class
public class ThreadByExtendingClass {

    public static void main(String[] args) {

        // Create a new thread by extending the Thread class

        MyThread myThread = new MyThread();
        myThread.setName("New Worker Thread");
        myThread.setPriority(Thread.MAX_PRIORITY);

        System.out.println("Hello from current thread! " + Thread.currentThread().getName());
        myThread.start();
        System.out.println("Hello again from current thread! " + Thread.currentThread().getName());

    }

    // Define a class that extends Thread
    // it allows us to not use static Thread. methods
    private static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("Hello from a separate thread! " + currentThread().getName());
            System.out.println("Thread Priority: " + currentThread().getPriority());

        }
    }
}

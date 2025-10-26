package org.example.byCreatingObject;



public class ThreadByCreatingObject {
    public static void main(String[] args) {

        // Create a new thread that prints a message
        Thread thread = new Thread(new Runnable() {
            // Implement the run method to print a message
            @Override
            public void run() {
                System.out.println("Hello from a separate thread! " + Thread.currentThread().getName());
                System.out.println("Thread Priority: " + Thread.currentThread().getPriority());
            }
        });

        // Set the name of the new thread
        thread.setName("New Worker Thread");

        thread.setPriority(Thread.MAX_PRIORITY);

        // Print a message from the main thread
        // Start the new thread
        // Print another message from the main thread
        System.out.println("Hello from current thread! " +  Thread.currentThread().getName());
        thread.start();
        System.out.println("Hello again from current thread! " + Thread.currentThread().getName());



    }
}
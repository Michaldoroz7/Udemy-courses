package org.example.byCreatingObject;


// Demonstrates handling uncaught exceptions in a separate thread
public class UncaughExceptionInThread {
    public static void main(String[] args) {
        // Create a new thread that will throw an exception
        Thread thread = new Thread(new Runnable() {

            // Implement the run method to throw an exception
            @Override
            public void run() {
                System.out.println("Thread started: " + Thread.currentThread().getName());
                // This will cause an uncaught exception
                int result = 10 / 0;
                System.out.println("Result: " + result);
            }
        });

        // Set the name of the new thread
        thread.setName("Exception Thread");

        // Set an uncaught exception handler for the thread
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("Uncaught exception in thread '" + t.getName() + "': " + e.getMessage());
            }
        });

        thread.start();
    }
}

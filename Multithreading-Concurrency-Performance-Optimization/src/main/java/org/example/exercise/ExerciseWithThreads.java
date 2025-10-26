package org.example.exercise;

import java.util.List;
import java.util.Random;

public class ExerciseWithThreads {
    private static final int MAX_CODE = 9999;


    public static void main(String[] args) {

        // Create a vault with a random secret code
        Random random = new Random();
        Vault vault = new Vault(random.nextInt(MAX_CODE));

        // Create hacker threads and a police thread
        List<Thread> threads = List.of(
                new AscendingHackerThread(vault),
                new DescendingHackerThread(vault),
                new PoliceThread()
        );

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

    }

    // Represents a vault that can be hacked
    // It contains a secret code and a method to check if a guess is correct
    private static class Vault {
        private final int secretCode;

        public Vault(int secretCode) {
            this.secretCode = secretCode;
        }

        public boolean isCorrectCode(int guess) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return this.secretCode == guess;
        }
    }

    // Abstract class representing a hacker thread
    // It extends the Thread class
    // and sets the thread name and priority
    private static abstract class HackerThread extends Thread {
        protected Vault vault;

        public HackerThread(Vault vault) {
            this.vault = vault;
            this.setName(this.getClass().getSimpleName());
            this.setPriority(Thread.MAX_PRIORITY);
        }

        @Override
        public void start() {
            System.out.println("Starting thread: " + this.getName());
            super.start();
        }
    }

    // Hacker thread that tries to guess the code in ascending order
    private static class AscendingHackerThread extends HackerThread {
        public AscendingHackerThread(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for (int guess = 0; guess <= MAX_CODE; guess++) {
                if (vault.isCorrectCode(guess)) {
                    System.out.println(this.getName() + " guessed the code " + guess);
                    System.exit(0);
                }
            }
        }
    }

    // Hacker thread that tries to guess the code in descending order
    private static class DescendingHackerThread extends HackerThread {
        public DescendingHackerThread(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for (int guess = MAX_CODE; guess >= 0; guess--) {
                if (vault.isCorrectCode(guess)) {
                    System.out.println(this.getName() + " guessed the code " + guess);
                    System.exit(0);
                }
            }
        }
    }

    private static class PoliceThread extends Thread {
        @Override
        public void run() {
            for (int i = 10; i > 0; i--) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(i);
            }
            System.out.println("Game over for hackers!");
            System.exit(0);
        }
    }
}

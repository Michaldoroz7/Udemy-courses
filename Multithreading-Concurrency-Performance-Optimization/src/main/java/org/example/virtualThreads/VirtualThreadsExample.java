package org.example.virtualThreads;

import java.util.ArrayList;
import java.util.List;

//Example of implementation of Virtual Threads which are not that heavy in resources like normal Threads (Plaftorm Threads which are became Carrier Threads for virtual ones)
//During blocking task, virtual threads are unmouted from worker and mount when blocking section ends. Since it`s not in our control, there is a possibility that it will be mounted in different workers during its lifetime
public class VirtualThreadsExample {

    private static final int NUMBER_OF_VIRTUAL_THREADS = 10;

    public static void main(String[] args) throws InterruptedException {


        List<Thread> virtualThreads = new ArrayList<>();

        for (int i = 0; i < NUMBER_OF_VIRTUAL_THREADS; i++) {
            Thread virtualThread = Thread.ofVirtual().unstarted(new BlockingTask());
            virtualThreads.add(virtualThread);
        }

        for (Thread thread : virtualThreads) {
            thread.start();
        }

        for (Thread thread : virtualThreads) {
            thread.join();
        }

        //Thread platformThread = Thread.ofPlatform().unstarted(runnable); // Inside thread: Thread[#27,Thread-0,5,main] -> Classic way of creating thread which is manageable by the OS
        //Thread virtualThread = Thread.ofVirtual().unstarted(runnable); // Inside thread: VirtualThread[#28]/runnable@ForkJoinPool-1-worker-1 -> JVM created internal thread pool of platform thread which is called ForkJoinPool and its taking care of VirtualThreads


        //virtualThread.start();
        //virtualThread.join();

        //platformThread.start();
        //platformThread.join();
    }

    private static class BlockingTask implements Runnable {

        @Override
        public void run() {
            System.out.println("Inside thread: " + Thread.currentThread() + " Before blocking call");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Inside thread: " + Thread.currentThread() + " After blocking call");
        }
    }


}

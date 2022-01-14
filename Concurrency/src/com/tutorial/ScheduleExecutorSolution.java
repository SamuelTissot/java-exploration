package com.tutorial;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ScheduleExecutorSolution {
    public static void main(String[] args) {

        System.out.println("---- Simple Schedule Executor ----");
        simpleExample();

        System.out.println("---- Fix Rate ----");
        fixRate();

        System.out.println("---- Delayed Rate ----");
        delayedRate();
    }

    private static void simpleExample() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        // in this case you only need the FUTURE not scheduleFuture
        // schedule will only schedule it once.
        ScheduledFuture<Double> futureDouble = scheduledExecutorService.schedule(() -> {
            Thread.sleep(200);
            System.out.printf("simple scheduled Executor, threadId: %d\n", Thread.currentThread().getId());
            return Math.random() * 100;
        }, 1000, TimeUnit.MILLISECONDS);

        try {
            // get is blocking
            Double result = futureDouble.get();
            System.out.printf("Result of simple scheduled Executor: %f\n", result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        scheduledExecutorService.shutdown();
    }

    // with FIX rate you can have a big problem if you spawn a new thread every 1s but each
    // thread tasks takes 2s to execute. ---> out of memory ......  boom
    private static void fixRate() {

        AtomicInteger atomicInteger = new AtomicInteger(0);

        ScheduledExecutorService fixRateScheduler = Executors.newScheduledThreadPool(10);

        // you can't return a value from the Fix or delayed scheduled executor
        ScheduledFuture<?> scheduledFuture = fixRateScheduler.scheduleAtFixedRate(() -> {
            System.out.printf("fixRate Executor, threadId: %d\n", Thread.currentThread().getId());
            atomicInteger.incrementAndGet();
        }, 20, 100, TimeUnit.MILLISECONDS);


        (new Thread(()->{
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }

                // make sure you account for cancellation
                if (scheduledFuture.isCancelled() || scheduledFuture.isDone()) {
                    break;
                }

                int current = atomicInteger.get();
                System.out.printf("fixRate scheduled Executor's current value: %d\n", current);
                if (current >= 10) {
                    System.out.println("shutting down Fix Rate scheduler");
                    scheduledFuture.cancel(true);
                    fixRateScheduler.shutdown();
                    break;
                }
            }
        })).start();


        try {
            // await is blocking
            // like join for scheduled threads
            System.out.println("waiting for fixRate Scheduler showdown");
            boolean isTerminated = fixRateScheduler.awaitTermination(2000, TimeUnit.MILLISECONDS);
            System.out.printf("fixRate Executor terminated: %b\n", isTerminated);

            if (!isTerminated) {
                System.out.print("fixRate Executor manual shutdown\n");
                fixRateScheduler.shutdown();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void delayedRate() {

        AtomicInteger atomicInteger = new AtomicInteger(0);
        ScheduledExecutorService delayedExecutor = Executors.newScheduledThreadPool(10);

        // you can't return a value from the Fix or delayed scheduled executor
        ScheduledFuture<?> scheduledFuture = delayedExecutor.scheduleWithFixedDelay(() -> {
            atomicInteger.incrementAndGet();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 0, 100, TimeUnit.MILLISECONDS);



        Thread t = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                delayedExecutor.shutdown();
            }
        });

        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("delayed scheduled Executor was able to make %d iteration\n", atomicInteger.get());
    }
}

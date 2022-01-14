package com.tutorial;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;

public class ExecutorSolution {

    public static void main(String[] args) {
//        simpleExample();

        System.out.println("---- executorAndInvoke ----");
//        executorAndInvoke();

        System.out.println("---- Using Future Simple Example ----");
//        usingFutures();

        System.out.println("---- Fixed ThreadPool ----");
        fixedThreadPool();

        System.out.println("---- cached threadpool ----");
//        cachedThreadPool();
    }

    private static void simpleExample() {
        // output main thread id
        System.out.printf("main thread id: %d\n", Thread.currentThread().getId());

        // create a single thread executor
        // everything it will execute will be on the same thread
        // execute takes in a RUNNABLE
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> System.out.printf("1 - %d, on thread id: %d\n", Math.round(Math.random() * 100), Thread.currentThread().getId()));
        executorService.execute(() -> System.out.printf("2 - %d, on thread id: %d\n", Math.round(Math.random() * 100), Thread.currentThread().getId()));
        executorService.execute(() -> System.out.printf("3 - %d, on thread id: %d\n", Math.round(Math.random() * 100), Thread.currentThread().getId()));
        executorService.execute(() -> System.out.printf("4 - %d, on thread id: %d\n", Math.round(Math.random() * 100), Thread.currentThread().getId()));


        // very IMPORTANT not to forget to close the executor
        executorService.shutdown();

        try {
            boolean terminated = executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
            System.out.printf("simple executor terminated %b\n", terminated);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    // With INVOKE you can invoke multiple tasks at once `invokeAll`
    // or only one of many `invokeAny
    private static void executorAndInvoke() {

        // if we had a executor with a thread pool it would do them concurrently
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        // the task execution will be that of the longest tasks
        List<Callable<String>> tasks = new ArrayList<>();
        tasks.add(() -> {
            Thread.sleep(1000);
            return String.format("1 - %d, on thread id: %d", Math.round(Math.random() * 100), Thread.currentThread().getId());
        });
        tasks.add(() -> String.format("2 - %d, on thread id: %d", Math.round(Math.random() * 100), Thread.currentThread().getId()));
        tasks.add(() -> String.format("3 - %d, on thread id: %d", Math.round(Math.random() * 100), Thread.currentThread().getId()));

        try {
            List<Future<String>> output = executorService.invokeAll(tasks);
            output.forEach((future) -> {
                if (future.isDone()) {
                    try {
                        System.out.println(future.get());
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Never forget to close an executor
        executorService.shutdown();

        try {
            boolean terminated = executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
            System.out.printf("invoke executor terminated %b\n", terminated);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private static void usingFutures() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Callable<Double> task = () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Math.random();
        };

        Future<Double> doubleFuture = executorService.submit(task);

        // NOT THE BEST WAY TO DO THIS, RESOURCES LOST
        // used to demonstrate
        while (!doubleFuture.isDone()) {
            if (doubleFuture.isCancelled()) {
                System.out.println("the doubleFuture was cancelled, aborting");
                return;
            }
            try {
                System.out.println("sleeping");
                Thread.sleep(100); // idle thread :/
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        // if we get here we know the future work has been completed
        try {
            Double result = doubleFuture.get();
            System.out.printf("double future result: %f\n", result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // IMPORTANT close the executor service
        executorService.shutdown();

        try {
            boolean terminated = executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
            System.out.printf("future example executor terminated %b\n", terminated);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    // fixed thread-pools will always keep the threads available
    private static void fixedThreadPool() {
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        Function<Integer, Future<Double>> work = taskNumber -> executorService.submit(() -> {
            System.out.printf("processing task number: %d, threadId: %d\n", taskNumber, Thread.currentThread().getId());
            try {
                Thread.sleep((int) (Math.random() * 200));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Math.random();
        });

        for (int i = 0; i < 100; i++) {
            // omitting return future
            work.apply(i);
        }

        // IMPORTANT close the executor service
        executorService.shutdown();

        try {
            boolean terminated = executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
            System.out.printf("fixed threadPool terminated %b\n", terminated);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Cached Threadpool will create and destroy the threads when they are needed/not needed
    private static void cachedThreadPool() {
        ExecutorService executorService = Executors.newCachedThreadPool();

        Function<Integer, Future<Double>> work = taskNumber -> executorService.submit(() -> {
            System.out.printf("processing task number: %d, threadId: %d\n", taskNumber, Thread.currentThread().getId());
            try {
                Thread.sleep((int) (Math.random() * 200));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Math.random();
        });

        for (int i = 0; i < 100; i++) {
            // omitting return future
            work.apply(i);
        }


        // IMPORTANT close the executor service
        executorService.shutdown();


        try {
            boolean terminated = executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
            System.out.printf("cached threadPool terminated %b\n", terminated);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}

package com.tutorial;

import com.tutorial.impl.CallableClass;
import com.tutorial.impl.CustomThread;
import com.tutorial.impl.RunnableClass;
import com.tutorial.impl.SlowCounter;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Introduction {
    public static void main(String[] args) {

        System.out.println("---- Introduction Simple Thread ----");
//        SimpleThreadExample();

        System.out.println("---- Introduction Custom Thread Class ----");
//        withCustomThreadClass();

        System.out.println("---- The Runnable Interface ----");
//        theRunnableInterface();

        System.out.println("---- Getting a return Value ----");
        gettingAReturnValue();

        System.out.println("---- Threads and Interruptions ----");
//        interruptingThreads();

        System.out.println("---- Waiting for threads to finish ----");
        waitingForThreads();

    }

    public static void SimpleThreadExample() {
        Thread thread = new Thread(() -> System.out.printf("SimpleThreadExample threadId: %d%n", Thread.currentThread().getId()));

        thread.start();

        // prints the Main thread ID
        System.out.printf("main threadId: %d%n", Thread.currentThread().getId());

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void withCustomThreadClass() {
        // code for interface Thread, not CustomThread
        // to avoid ....
        Thread customThread = new CustomThread();
        customThread.start();

        try {
            customThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void theRunnableInterface() {
        //With a Runnable interface
        Runnable runnableClass = new RunnableClass();
        // notice we pass in the instance
        // this is COMPOSITION, much better.
        Thread threadA = new Thread(runnableClass);
        threadA.start();

        try {
            threadA.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void gettingAReturnValue() {
        // With a Callable interface you can return Values,
        // but it must be done within an Executor
        CallableClass callableClass = new CallableClass();
        ExecutorService executorService = Executors.newSingleThreadExecutor(); // using an executor
        Future<String> future = executorService.submit(callableClass);

        try {
            // future->get is blocking
            System.out.println(future.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            // important to always shutdown
            executorService.shutdown();
        }
    }

    private static void interruptingThreads() {
        // Interrupting a Thread
        Runnable slowCounter = new SlowCounter();
        Thread threadB = new Thread(slowCounter);
        threadB.start();
        threadB.interrupt();

        try {
            threadB.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("interrupted thread did not finish printing it's 10 digits");

        // let the main thread finish printing the stack
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void waitingForThreads() {
        // Join acts like a "wait" until a thread is finish
        Thread counterThreadA = new Thread(new SlowCounter("counter A", 10, 100));
        Thread counterThreadB = new Thread(new SlowCounter("counter B", 10, 100));

        counterThreadA.start();

        try {
            // join waits until thread A has finish before continuing
            counterThreadA.join(); // you can pass in a wait time before it restart
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // counterThreadB will only start when counterThreadA has finished
        counterThreadB.start();

        try {
            counterThreadB.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

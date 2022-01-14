package com.tutorials.concept;

import com.tutorials.concept.context.Context;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class SimpleContext {

    public static void main(String[] args) {
        Function<Context, Runnable> runnableWithContext = (ctx) -> () -> {
            AtomicBoolean done = new AtomicBoolean(false);

            Thread worker = new Thread(() -> {
                try {
                    long duration = (long) (Math.random() * 100);

                    Thread.sleep(duration);

                    if (duration > 50) {
                        ctx.cancel();
                    }

                    System.out.printf("thread %d slept for %d and has finished\n", Thread.currentThread().getId(), duration);
                } catch (InterruptedException e) {
                    System.out.printf("thread %d was interrupted\n", Thread.currentThread().getId());
                } finally {
                    done.set(true);
                }
            });


            ScheduledExecutorService delayedExecutor = Executors.newScheduledThreadPool(1);
            delayedExecutor.scheduleWithFixedDelay(() -> {
                if (ctx.cancelled()) {
                    System.out.println("the process has been cancelled, interrupting the worker and shutting down");
                    worker.interrupt();
                    delayedExecutor.shutdown();
                }

                if (done.get()) {
                    System.out.println("worker is done, shutting down");
                    delayedExecutor.shutdown();
                }

            }, 0, 10, TimeUnit.MILLISECONDS);


            worker.start();
        };


        Context context = new Context();
        for (int i = 0; i < 100; i++) {
            new Thread(runnableWithContext.apply(context)).start();
        }
    }
}

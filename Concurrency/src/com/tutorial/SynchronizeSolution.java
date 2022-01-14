package com.tutorial;

// Synchronize is good for quick thread syncronization (locking)
// but it does have drawbacks.
// - Other threads my sleep
// - locking is done on a method or object, which increase the scope of the lock
// - no fine grain control
public class SynchronizeSolution {

    static int counter = 0;

    // the object needs to be shared between all instances else it will not work
    public static final Object lock = new Object();

    public static void main(String[] args) {

        usingSynchronizedOnMethod();

        counter = 0;
        incrementCounterWithObjectLock();

    }

    // synchronized at the method level
    private static void usingSynchronizedOnMethod() {
        Thread[] wait = spawnThreads(SynchronizeSolution::synchronizedCounterMethod);
        waitForThreads(wait);
    }

    private synchronized static void synchronizedCounterMethod() {
        System.out.printf("%s->synchronizedCounterMethod, before: %d, threadId: %d\n", SynchronizeSolution.class.getSimpleName(), counter, Thread.currentThread().getId());
        counter++;
        System.out.printf("%s->synchronizedCounterMethod, after: %d, threadId: %d\n", SynchronizeSolution.class.getSimpleName(), counter, Thread.currentThread().getId());
    }

    // synchronized on an object instead of the method
    private static void incrementCounterWithObjectLock() {
        Thread[] wait = spawnThreads(() -> {
            synchronized (lock) {
                System.out.printf("%s->incrementCounterWithObjectLock, before: %d, threadId: %d\n", SynchronizeSolution.class.getSimpleName() , counter, Thread.currentThread().getId());
                counter++;
                System.out.printf("%s->incrementCounterWithObjectLock, after: %d, threadId: %d\n", SynchronizeSolution.class.getSimpleName(), counter, Thread.currentThread().getId());
            }
        });
        waitForThreads(wait);
    }


    private static Thread[] spawnThreads(Runnable fn) {
        // syncronized method
        Thread[] wait = new Thread[10];
        for (int i=0; i < 10; i++) {
            Thread t = new Thread(fn);
            t.start();
            wait[i] = t;
        }

        return wait;
    }

    private static void waitForThreads(Thread[] wait) {
        // wait for each thread to complete
        for (Thread w : wait) {
            try {
                w.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

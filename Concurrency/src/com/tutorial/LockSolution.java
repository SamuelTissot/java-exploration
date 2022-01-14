package com.tutorial;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// Locks in Jova are really like Mutex.
// and can be applied to smaller scoped that the Synchronized keyword
// Some Advantages of the LOCK
// is that you can only lock it for write but keep reading it :)
public class LockSolution {

    private static int counter = 0;
    private static final Lock lock = new ReentrantLock();
    private static final ReadWriteLock RWLock = new ReentrantReadWriteLock();

    public static void main(String[] args) {

        countWithLock();

        counter = 0;
        ifUnlocked();

        counter = 0;
        tryLockBetweenTimeFrame();

        counter = 0;
        Thread[] threads = onlyLockWrites();
        (new Thread( () -> {
            while ( counter < 10 ) {
                System.out.printf("reading counter: %d\n", counter);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } )).start();
        waitForThreads(threads);
    }


    // basic locking with a LOCK
    private static void countWithLock() {
        Thread[] wait = spawnThreads(() -> {
            // it's good practice adding the lock/unlock in  a try/catch/finally to make
            // sure it is released
            try {
                lock.lock();
                System.out.printf("%s->synchronizedCounterMethod, before: %d, threadId: %d\n", LockSolution.class.getSimpleName(), counter, Thread.currentThread().getId());
                counter++;
                System.out.printf("%s->synchronizedCounterMethod, after: %d, threadId: %d\n", LockSolution.class.getSimpleName(), counter, Thread.currentThread().getId());
            } finally {
                lock.unlock();

            }
        });

        waitForThreads(wait);
    }

    // if available run the code else go do something else
    // this will NOT increment to 10, it will only increment if the lock is available
    private static void ifUnlocked() {

        Thread[] wait = spawnThreads(() -> {
            if (lock.tryLock()) { // only execute if the lock is avilable
                try {
                    System.out.printf("%s->ifUnlocked, before: %d, threadId: %d\n", LockSolution.class.getSimpleName(), counter, Thread.currentThread().getId());
                    counter++;
                    System.out.printf("%s->ifUnlocked, after: %d, threadId: %d\n", LockSolution.class.getSimpleName(), counter, Thread.currentThread().getId());
                } finally {
                    lock.unlock(); // make sure you still unlock
                }
            } else {
                System.out.println("ifUnlocked, couldn't get access to the lock, so doing something else");
            }
        });

        waitForThreads(wait);
    }

    private static void tryLockBetweenTimeFrame() {

        Thread[] threads = spawnThreads(() -> {
            try {
                // try to get access to the lock variable for 1000 ms
                // it throws a InterruptedException
                if (lock.tryLock(1000, TimeUnit.MILLISECONDS)) {
                    try {
                        System.out.printf("%s->tryLockBetweenTimeFrame, before: %d, threadId: %d\n", LockSolution.class.getSimpleName(), counter, Thread.currentThread().getId());
                        counter++;
                        System.out.printf("%s->tryLockBetweenTimeFrame, after: %d, threadId: %d\n", LockSolution.class.getSimpleName(), counter, Thread.currentThread().getId());
                    } finally {
                        lock.unlock(); // make sure you still unlock
                    }
                } else {
                    System.out.println("tryLockBetweenTimeFrame, couldn't get access to the lock, so doing something else");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        waitForThreads(threads);

    }

    private static Thread[] onlyLockWrites() {
        return spawnThreads(() -> {
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                RWLock.writeLock().lock();
                System.out.printf("%s->onlyLockWrites, before: %d, threadId: %d\n", LockSolution.class.getSimpleName(), counter, Thread.currentThread().getId());
                counter++;
                System.out.printf("%s->onlyLockWrites, after: %d, threadId: %d\n", LockSolution.class.getSimpleName(), counter, Thread.currentThread().getId());
            } finally {
                RWLock.writeLock().unlock();

            }
        });
    }

    private static Thread[] spawnThreads(Runnable fn) {
        // syncronized method
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(fn);
            t.start();
            threads[i] = t;
        }

        return threads;
    }

    private static void waitForThreads(Thread[] threads) {
        // wait for each thread to complete
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

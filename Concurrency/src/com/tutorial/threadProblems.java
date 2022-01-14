package com.tutorial;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class threadProblems {
    public static void main(String[] args) {
        System.out.println("------- deadlock--------");
//        deadlock();

        System.out.println("------- livelock--------");
        livelock();
    }

    private static void deadlock() {
        final String resource1 = "one";
        final String resource2 = "two";

        Thread t1 = new Thread(() -> {
            // holding a lock on resource1
            synchronized (resource1) {
                System.out.printf("thread: %d, has a lock on resource ONE\n", Thread.currentThread().getId());

                // make sure it's not to fast
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {/* ignoring Exception*/}

                // waiting to have access to resource2 in order to finish AND
                // RELEASE resource1
                synchronized (resource2) {
                    System.out.printf("thread: %d, has a lock on resource TWO\n", Thread.currentThread().getId());
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (resource2) {
                System.out.printf("thread: %d, has a lock on resource TWO\n", Thread.currentThread().getId());

                // make sure it's not to fast
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {/* ignoring Exception*/}

                synchronized (resource1) {
                    System.out.printf("thread: %d, has a lock on resource ONE\n", Thread.currentThread().getId());
                }
            }
        });

        t1.start();
        t2.start();
    }


    static class Friend {
        private final String name;
        private final boolean done;
        private boolean waiting;

        public Friend(final String name, boolean waiting) {
            this.name = name;
            this.done = false;
            this.waiting = waiting;
        }

        synchronized public boolean isDone() {
            return done;
        }

        synchronized public boolean isWaiting() {
            return waiting;
        }

        synchronized public void setWaiting(final boolean waiting) {
            this.waiting = waiting;
        }

        synchronized public void endCall(int angerLevel) {
            System.out.printf("%-10s: %-10s %-10s\n", name, "no you hangup", String.join("", Collections.nCopies(angerLevel, "!")));
            waiting = true;
        }
    }


    private static void livelock() {
        AtomicInteger angerCounter = new AtomicInteger();

        Friend JL = new Friend("Jean-loop", true);
        Friend emilio = new Friend("Emilio", false);

        Thread JLOnThePhone = new Thread(() -> {
            while (!JL.isDone()) {
                if (emilio.isWaiting()) {
                    try {Thread.sleep(500);} catch (InterruptedException e) {/* ignoring Exception*/}
                    continue;
                }

                emilio.endCall(angerCounter.incrementAndGet());
                emilio.setWaiting(true);
                JL.setWaiting(false);
            }
        });

        Thread t2 = new Thread(() -> {
            while (!emilio.isDone()) {
                if (JL.isWaiting()) {
                    try {Thread.sleep(500);} catch (InterruptedException e) {/* ignoring Exception*/}
                    continue;
                }

                JL.endCall(angerCounter.incrementAndGet());
                JL.setWaiting(true);
                emilio.setWaiting(false);
            }

        });

        JLOnThePhone.start();
        t2.start();

    }
}

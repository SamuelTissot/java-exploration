package com.tutorial.impl;


/**
 * SlowCounter counts to N with a sleep time of T
 * N defaults to 10
 * T defaults to 500 ms
 */
public class SlowCounter implements Runnable {
    private final String name;
    private final int n;
    private final int sleepTime;

    public SlowCounter() {
        this.name = SlowCounter.class.getSimpleName();
        this.n = 10;
        this.sleepTime = 500;
    }

    public SlowCounter(final String name, final int n, final int sleepTime) {
        this.name = name;
        this.n = n;
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {
        for (int i = 0; i < n; i++) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                /*
                  The Try/catch needs to be written in such a way that
                  processing is interrupted
                 */
                e.printStackTrace();
                // set the interrupted flag to true
                // it you don't set the thread to interrupted here
                // it will just keep on going (like a little hickup)
                Thread.currentThread().interrupt();
                // break OR return, if not it will keep on going
//                break;
                return;
            }
            System.out.printf("%s processing value value: %s -- threadId: %d\n", name, i, Thread.currentThread().getId());
        }
    }
}

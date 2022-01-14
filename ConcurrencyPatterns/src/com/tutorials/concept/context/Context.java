package com.tutorials.concept.context;

public class Context {
    private boolean done = false;

    public synchronized void cancel() {
        this.done = true;
    }

    public synchronized boolean cancelled() {
        return done;
    }
}

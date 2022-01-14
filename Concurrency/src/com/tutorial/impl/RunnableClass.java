package com.tutorial.impl;

public class RunnableClass implements Runnable {
    @Override
    public void run() {
        System.out.printf("%s threadId: %d\n", Runnable.class.getSimpleName(), Thread.currentThread().getId());
    }
}

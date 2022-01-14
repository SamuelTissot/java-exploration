package com.tutorial.impl;

public class CustomThread extends Thread {
    @Override
    public void run() {
        System.out.printf("%s threadId: %d\n", CustomThread.class.getSimpleName(), Thread.currentThread().getId());
    }
}

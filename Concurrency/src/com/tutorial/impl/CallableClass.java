package com.tutorial.impl;

import java.util.concurrent.Callable;

public class CallableClass implements Callable<String> {
    @Override
    public String call() {
        return String.format("%s threadId: %d\n", CallableClass.class.getSimpleName(), Thread.currentThread().getId());
    }
}

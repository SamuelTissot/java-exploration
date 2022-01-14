package com.tutorials.concept.tasks;

public class TaskResponse {
    private final Exception exception;
    private final String value;

    public TaskResponse(final Exception exception) {
        this.exception = exception;
        this.value = "";
    }

    public TaskResponse(final String value) {
        this.value = value;
        this.exception = null;
    }

    public boolean isSuccess() {
        return exception == null;
    }

    public Exception error() {
        return exception;
    }

    public String value() {
        return value;
    }
}

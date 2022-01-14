package com.tutorials.concept.tasks;

import com.tutorials.concept.context.Context;
import com.tutorials.data.Jokes;

import java.util.concurrent.Callable;

public class Tasks implements Callable<TaskResponse> {

    private final Context ctx;
    private final int idx;

    public Tasks(final Context ctx, int idx) {
        this.ctx = ctx;
        this.idx = idx;
    }

    @Override
    public TaskResponse call() throws Exception {


        if (idx >= 70 && idx < 80) {
            ctx.cancel();
        }

        if (idx == 5) {
            return new TaskResponse(new Exception("evil number"));
//            throw new Exception("evil number");
        }

        Thread.sleep(2000);

        if (ctx.cancelled()) {
            return new TaskResponse("context cancelled");
        }

        try {
            return new TaskResponse(Jokes.get(idx));
        } catch (ArrayIndexOutOfBoundsException e) {
            return new TaskResponse(e);
        }
    }
}

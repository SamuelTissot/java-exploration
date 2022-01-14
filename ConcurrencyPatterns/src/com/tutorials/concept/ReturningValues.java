package com.tutorials.concept;

import com.tutorials.concept.context.Context;
import com.tutorials.concept.tasks.TaskResponse;
import com.tutorials.concept.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class ReturningValues {
    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(100);

        List<Future<TaskResponse>> responses = new ArrayList<>();
        Context ctx = new Context();
        for (int i = 0; i < 100; i++) {
            // create a new tasks
            Tasks tasks = new Tasks(ctx, i);
            //submit the tasks AND add it to the responses
            responses.add(executorService.submit(tasks));
        }

        AtomicInteger idx = new AtomicInteger(0);
        responses.forEach((Future<TaskResponse> futureResponse) -> {
                TaskResponse response;

                try {
                    // get is blocking
                    response = futureResponse.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    ctx.cancelled();
                    return;
                }

                String output = response.isSuccess() ? response.value() : response.error().getMessage();

                System.out.printf("%d\t\t%s\n", idx.incrementAndGet(), output);
        });
    }
}

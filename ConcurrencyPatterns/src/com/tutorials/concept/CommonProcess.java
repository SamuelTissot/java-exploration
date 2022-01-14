package com.tutorials.concept;

import com.tutorials.concept.context.Context;

import java.util.function.BiFunction;

public class CommonProcess {
    public static void main(String[] args) {
        BiFunction<Context, Integer, Runnable> runnableWithContext = (ctx, idx) -> () -> {
            if (idx == 50) {
                System.out.println("cancelling processes");
                ctx.cancel();
                return;
            }

            if (ctx.cancelled()) {
                System.out.printf("context Cancelled aborting process %d\n", idx);
                return;
            }

            // working
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.printf("process %d has done its work\n", idx);
        };


        Context context = new Context();
        for (Integer i = 0; i < 100; i++) {
            new Thread(runnableWithContext.apply(context, i)).start();
        }
    }
}

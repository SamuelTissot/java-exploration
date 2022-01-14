package com.tutorials.concept.pipes;

import com.tutorials.concept.channel.Channel;
import com.tutorials.concept.context.Context;


public class IntGenerator {

    private final Integer n;

    public IntGenerator(final Integer n) {
        this.n = n;
    }


    public Channel<Integer> run(Context ctx) {
        Channel<Integer> out = new Channel<>();

        (new Thread(()->{

            for (Integer i = 0; i < n; i++) {

                // look to see if the context was cancelled
                if (ctx.cancelled()) {
                    Thread.currentThread().interrupt();
                    return;
                }

                try {
                    out.put(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // don't forget to close
            out.close();

        })).start();

        return out;
    }
}

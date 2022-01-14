package com.tutorials.concept.pipes;

import com.tutorials.concept.channel.Channel;
import com.tutorials.concept.channel.ChannelClosedException;
import com.tutorials.concept.context.Context;

import java.util.function.Function;

public class Pipe<T>  {

    public Channel<T> run(Context ctx, Channel<T> channel, Function<T, T> fn) {
        Channel<T> out = new Channel<>();

        (new Thread(()->{

            while (!channel.closed()) {
                if (ctx.cancelled()) {
                    Thread.currentThread().interrupt();
                    return;
                }

                // apply the method
                try {
                    out.put(fn.apply(channel.take()));
                } catch (InterruptedException | ChannelClosedException e) {
                    break;
                }
            }

            // don't forget to close it
            out.close();
        })).start();

        return out;
    }
}

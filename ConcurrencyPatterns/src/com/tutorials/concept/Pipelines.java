package com.tutorials.concept;

import com.tutorials.concept.channel.Channel;
import com.tutorials.concept.channel.ChannelClosedException;
import com.tutorials.concept.context.Context;
import com.tutorials.concept.pipes.IntGenerator;
import com.tutorials.concept.pipes.Pipe;

import java.util.function.Function;

public class Pipelines {
    public static void main(String[] args) {

        Function<Integer, Function<Integer, Integer>> addition = (Integer addVal) -> (Integer in) -> in + addVal;


        Context ctx = new Context();
        Channel<Integer> output =
                new Pipe<Integer>().run(ctx,
                        new Pipe<Integer>().run(ctx,
                                new IntGenerator(100).run(ctx),
                                addition.apply(100)), addition.apply(200));


        while (!output.closed()) {
            if (ctx.cancelled()) {
                output.close();
                break;
            }

            try {
                System.out.println(output.take());
            } catch (InterruptedException | ChannelClosedException e) {
                break;
            }
        }
    }
}

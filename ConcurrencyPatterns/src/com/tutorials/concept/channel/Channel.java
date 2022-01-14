package com.tutorials.concept.channel;

import java.util.concurrent.*;


public class Channel<T> {
    private final BlockingQueue<T> queue = new LinkedBlockingDeque<>(1);
    private boolean isClosed = false;

    public void put(T v) throws InterruptedException {
        queue.put(v);
    }

    public T take() throws InterruptedException, ChannelClosedException {
        if (isClosed) {
            throw new ChannelClosedException("channel is closed");
        }
        T taken = queue.poll(50, TimeUnit.MILLISECONDS);
        if (taken == null) {
            return take();
        }
        return taken;
    }

    public void close() {
        // need to wait until the queue is at 0 to close
        // drain the queue
        new Thread(() -> {
            while (queue.size() > 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            isClosed = true;
        }).start();
    }

    public boolean closed() {
        return isClosed;
    }

}

package com.qnective.qtalkng;

import io.vertx.core.*;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.eventbus.ReplyFailure;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;


class Assignable<T>
{
    public T value;
}

public class Sync
{
    public static Vertx vertx = Vertx.vertx();
    public static Context context = vertx.getOrCreateContext();

    public static <T> AsyncResult<T> awaitResult(Consumer<Handler<AsyncResult<T>>> handler)
    {
        final Assignable<AsyncResult<T>> theResult = new Assignable<>();

        CountDownLatch latch = new CountDownLatch(1);

        vertx.runOnContext(
                theVoid -> {
                    System.out.println("executeBlocking handler " + Thread.currentThread());

                    handler.accept(new Handler<AsyncResult<T>>() {
                        @Override
                        public void handle(AsyncResult<T> event) {
                            // executes in the blocking thread
                            System.out.println("executeBlocking: completion callback " + Thread.currentThread());
                            theResult.value = event;
                            latch.countDown();
                        }
                    });
                }
        );

//        vertx.getOrCreateContext().executeBlocking(
//                objectFuture -> {
//                    System.out.println("executeBlocking handler " + Thread.currentThread());
//
//                    handler.accept(new Handler<AsyncResult<T>>() {
//                        @Override
//                        public void handle(AsyncResult<T> event) {
//                            // executes in the blocking thread
//                            System.out.println("executeBlocking: completion callback " + Thread.currentThread());
//                            theResult.value = event;
//                            latch.countDown();
//                            objectFuture.complete();
//                        }
//                    });
//                },
//                false,
//                asyncResult -> {
//                }
//        );

        System.out.println("awaitResult: Awaiting latch " + Thread.currentThread());
        try {
            latch.await();
            return theResult.value;
        } catch (InterruptedException e) {
            return new AsyncResult<T>() {
                @Override
                public T result() {
                    return null;
                }

                @Override
                public Throwable cause() {
                    return new ReplyException(ReplyFailure.TIMEOUT);
                }

                @Override
                public boolean succeeded() {
                    return false;
                }

                @Override
                public boolean failed() {
                    return true;
                }
            };
        }
    }
}

package com.qnective.qtalkng;

import io.vertx.core.*;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.eventbus.ReplyFailure;

import java.util.concurrent.*;
import java.util.function.Consumer;


class Assignable<T>
{
    public T value;
}

public class Sync
{
    public static Vertx vertx = Vertx.vertx();
    public static Context context = vertx.getOrCreateContext();

    public static <T> T awaitEvent(Consumer<Handler<T>> handler) throws InterruptedException, ExecutionException
    {
        CompletableFuture<T> future = new CompletableFuture<>();

        vertx.runOnContext(
                aVoid ->
                {
                    handler.accept(future::complete);
                }
        );
        return future.get();
    }

    public static <T> AsyncResult<T> awaitResult(Consumer<Handler<AsyncResult<T>>> handler)
            throws ExecutionException, InterruptedException
    {
        CompletableFuture<AsyncResult<T>> completableFuture = new CompletableFuture<>();
        vertx.runOnContext(
                theVoid -> {
                    System.out.println("executeBlocking handler " + Thread.currentThread());

                    handler.accept(completableFuture::complete);
                }
        );

        return completableFuture.get();
    }
}

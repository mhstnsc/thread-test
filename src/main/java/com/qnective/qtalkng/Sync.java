package com.qnective.qtalkng;
import io.vertx.core.*;

import java.util.concurrent.*;
import java.util.function.Consumer;


public class Sync
{
    private static Vertx vertx = Vertx.vertx();

    /** Synchronous wait for an asynchronous operation to finish
     *
     * If your handler already implements a timeout you might consider awaitResult.
     *
     * */
    @SuppressWarnings("unused")
    public static <T> T awaitEvent(int timeoutMsec, Consumer<Handler<T>> handler)
            throws InterruptedException, ExecutionException, TimeoutException
    {
        if(!Vertx.currentContext().isWorkerContext())
        {
            throw new UnsupportedOperationException("You should not block the eventloop thread");
        }

        CompletableFuture<T> completableFuture = new CompletableFuture<>();

        vertx.runOnContext(
                aVoid ->
                {
                    try
                    {
                        handler.accept(completableFuture::complete);
                    }
                    catch(RuntimeException ex)
                    {
                        completableFuture.completeExceptionally(ex);
                    }
                }
        );
        return completableFuture.get(timeoutMsec, TimeUnit.MILLISECONDS);
    }


    /** Wait for an AsyncResult.
     *
     * This function will wait indefinitelly. It is your responsability
     * to complete the request with a failure AsyncResult in case of timeout.
     *
     * Typical usage is for EventBus.send should already provide you with this.
     *
     * */
    @SuppressWarnings("unused")
    public static <T> T awaitResult(Consumer<Handler<AsyncResult<T>>> handler)
            throws ExecutionException, InterruptedException
    {
        if(!Vertx.currentContext().isWorkerContext())
        {
            throw new UnsupportedOperationException("You should not block the eventloop thread");
        }

        CompletableFuture<T> completableFuture = new CompletableFuture<>();

        vertx.runOnContext(
                theVoid -> {
                    try
                    {
                        handler.accept(
                                asyncResult ->
                                {
                                    if (asyncResult.succeeded())
                                    {
                                        completableFuture.complete(asyncResult.result());
                                    }
                                    else
                                    {
                                        completableFuture.completeExceptionally(asyncResult.cause());
                                    }
                                }
                        );
                    }
                    catch(RuntimeException ex)
                    {
                        completableFuture.completeExceptionally(ex);
                    }
                }
        );

        return completableFuture.get();
    }
}

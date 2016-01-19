package com.qnective.qtalkng;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClientRequest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


public class SerializedRequester extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        super.start();

        // do one request

        vertx.setTimer(
                1000,
                event ->
                {
                    System.out.println("Requester: before awaitResult " + Thread.currentThread());
                    try {
                        AsyncResult<Message<String>> reply = Sync.awaitResult(
                                (Handler<AsyncResult<Message<String>>> h) ->
                                {
                                    System.out.println("sending first request " + Thread.currentThread());
                                    vertx.eventBus().send(PooledBackend.MY_ADDRESS, new byte[0], h);
                                });
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Requester: After awaitResult " + Thread.currentThread());

                    try {
                        Buffer reply2 = Sync.awaitEvent(h ->
                        {
                            HttpClientRequest request = vertx.createHttpClient().get(
                                    12345,
                                    "localhost",
                                    "/",
                                    httpClientResponse -> {
                                        httpClientResponse.bodyHandler(h);
                                    }
                            );
                            request.end();
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    // do the next request
                    System.out.println("Requester: After awaitResult2 " + Thread.currentThread());
                }
        );

    }


}

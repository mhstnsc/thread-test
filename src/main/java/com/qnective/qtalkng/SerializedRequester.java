package com.qnective.qtalkng;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;

import java.util.concurrent.CountDownLatch;

/**
 * Created by mihai.stanescu on 1/11/2016.
 */
public class SerializedRequester extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        super.start();

        // do one request

        vertx.setTimer(1000,
                event ->
                {
                    System.out.println("Requester: before awaitResult " + Thread.currentThread());
                    AsyncResult<Message<String>> reply = Sync.awaitResult(
                            (Handler<AsyncResult<Message<String>>> h) ->
                            {
                                System.out.println("sending first request " + Thread.currentThread());
                                vertx.eventBus().send(PooledBackend.MY_ADDRESS, new byte[0], h);
                            });
                    System.out.println("Requester: After awaitResult " + Thread.currentThread());
//                    System.out.println("got reply " + reply.result().body());
//
//                    AsyncResult<Message<String>> reply2 = Sync.awaitResult(
//                            (Handler<AsyncResult<Message<String>>> h) ->
//                            {
//                                System.out.println("sending second request");
//                                vertx.eventBus().send(PooledBackend.MY_ADDRESS, new byte[0], h);
//                            });

                    // do the next request
                }
        );

    }


}

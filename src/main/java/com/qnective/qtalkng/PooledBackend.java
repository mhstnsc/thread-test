package com.qnective.qtalkng;

import io.vertx.core.*;

public class PooledBackend extends AbstractVerticle
{
    public static final String MY_ADDRESS = "mihai";

    @Override
    public void start(Future<Void> startFuture) throws Exception
    {
            getVertx().eventBus().consumer(MY_ADDRESS,
                    event ->
                    {
                        //simulate a sleep of some duration
                        try {
                            System.out.println("got request in the backend " + Thread.currentThread());
                            Thread.sleep(ApplicationStarter.REQUEST_DURATION);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        event.reply(new byte[0]);
                    }
            );

        startFuture.complete();
        System.out.println("PooledBacked: start completed: " + Thread.currentThread());
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        System.out.println("backend stopped");;
        stopFuture.complete();
    }
}

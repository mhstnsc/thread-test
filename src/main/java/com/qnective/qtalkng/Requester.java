package com.qnective.qtalkng;

import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mihai.stanescu on 1/5/2016.
 */
public class Requester implements Verticle {

    Vertx vertx;

    int tokens = ApplicationStarter.POOL_SIZE;
    int finishedNo = 0;
    byte[] payload = new byte[0];

    List<Integer> samples = new ArrayList<>();

    @Override
    public Vertx getVertx() {
        return vertx;
    }

    @Override
    public void init(Vertx vertx, Context context) {
        this.vertx = vertx;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception
    {
        for(int i=0; i<ApplicationStarter.AVERAGE_SAMPLES; i++) {
            int estimated = ApplicationStarter.POOL_SIZE * 1000 / ApplicationStarter.REQUEST_DURATION;
            samples.add(estimated);
        }



        vertx.setPeriodic(1000,
                (timerId)->
        {
            samples.remove(0);
            samples.add(finishedNo);

            int sum = samples.stream().reduce((integer, integer2) -> integer + integer2).get();

            int estimated = ApplicationStarter.POOL_SIZE * 1000 / ApplicationStarter.REQUEST_DURATION;
            System.out.printf("req/sec: %d, estimated: %d, percent: %d, 6-averaged: %d\n", finishedNo,
                    estimated, finishedNo * 100 / estimated,
                    sum * 100 / (ApplicationStarter.AVERAGE_SAMPLES * estimated)
                    );

            finishedNo = 0;
        });

        fire();

        startFuture.complete();
    }

    void fire()
    {
        int count = 0;
        while(tokens > 0 && count < 500)
        {
            tokens--;
            count++;
            vertx.eventBus().send(PooledBackend.MY_ADDRESS, payload,
                    event -> {
                        if(event.failed())
                        {
                            System.out.println("error: " + event.cause());
                            return;
                        }
                        finishedNo++;
                        tokens++;
                        fire();
                    });
        }
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {

    }
}

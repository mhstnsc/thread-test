package com.qnective.qtalkng;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.VertxImpl;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.dropwizard.Match;
import io.vertx.ext.dropwizard.MatchType;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class ApplicationStarter
{
    public static final int POOL_SIZE = 500;
    public static final int REQUEST_DURATION = 2000;
    public static final int AVERAGE_SAMPLES=2;
    public static void main(String[] args)
    {
        VertxOptions vertxOptions = new VertxOptions()
                .setClustered(true)
                .setClusterManager(new HazelcastClusterManager())
                .setWorkerPoolSize(POOL_SIZE)
                .setMetricsOptions(
                        new DropwizardMetricsOptions()
                                .setEnabled(true)
                                .addMonitoredEventBusHandler(new Match().setValue(".*").setType(MatchType.REGEX))
                );

        Vertx.clusteredVertx(vertxOptions,
                res -> {
                    System.out.println("Cluster node started....");
                    res.result().deployVerticle("com.qnective.qtalkng.PooledBackend", new DeploymentOptions().setWorker(true).setInstances(5),
                        event -> {
                            res.result().deployVerticle("com.qnective.qtalkng.RestBackend");
                            if(event.succeeded()) {
                                System.out.println("backend deployed " + Thread.currentThread());
                                res.result().deployVerticle(new SerializedRequester(), new DeploymentOptions().setWorker(true));
//                                res.result().deployVerticle(new Requester());
                            }
                        }
                    );
                });
    }
}

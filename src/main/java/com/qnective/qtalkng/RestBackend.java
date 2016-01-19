package com.qnective.qtalkng;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;

public class RestBackend extends AbstractVerticle {

    Router router;

    @Override
    public void start() throws Exception {
        HttpServer server = vertx.createHttpServer();

        router = Router.router(vertx);

        router.route().handler(routingContext ->
        {
            System.out.println("got http request");
            routingContext.response().end("Yuhuu");
        });

        vertx.createHttpServer().requestHandler(router::accept).listen(
                12345,
                asyncResult ->
                {
                    System.out.println("backend http listening");
                });
    }
}

package org.api.rest.common.abs;

import org.api.rest.common.Launcher;

import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

public class AbstractRestVerticle extends io.vertx.core.AbstractVerticle {

    private static final String HEALTHCHECK = "/healthcheck";
    
    public static Router router;
    public static void setRouter(Router _router) {
        router = _router;
    }
    
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        
    }
    
    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        
    }
    
    public Future<Void> createHtppServer(String host, int port){
        router.get(HEALTHCHECK).handler(context -> {
            Launcher.setHost(context.request().host());
            context.response().setStatusCode(200).putHeader("content-type", "application/json").end("WORKING");
        });
        
        Future<HttpServer> future = Future.future();
        vertx.createHttpServer().requestHandler(router).listen(port, host, future.completer());
        return future.map(m -> null);
    }
}

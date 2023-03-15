package org.api.rest.common;

import org.api.rest.common.abs.AbstractRestVerticle;

import io.vertx.core.Future;

public class MainRestVerticle extends AbstractRestVerticle {
    
    private final String SERVICE_NAME = "api.rest.common";
    
    public static void main(String[] args) {
        new Launcher().dispatch(new String[] {
          "run", "java:"+MainRestVerticle.class.getCanonicalName()
        });
    }
    
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);
        
        createHtppServer("0.0.0.0", 8080)
            // .compose(serverCreated -> publish(SERVICE_NAME, host, port))
            .setHandler(startFuture.completer());
    }
    
}

package org.api.rest.common.abs;

import java.lang.reflect.Method;
import java.util.Set;

import org.api.rest.common.Launcher;
import org.api.rest.common.annotation.RestVerticle;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class RestSubVerticle extends io.vertx.core.AbstractVerticle {
    
    private Router router;
    
    public RestSubVerticle(Router router){
        this.router = router;
    }
    
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Set<Class<?>> annotatedClass = Launcher.annotatedRestVerticle;;
        
        for(Class<?> clazz : annotatedClass) {
            
            Handler<RoutingContext> handler = (Handler<RoutingContext>) clazz.getConstructor().newInstance();

            Method setVertx = handler.getClass().getMethod("setVertx", Vertx.class);
            setVertx.invoke(handler, vertx);
            
            for(RestVerticle anno : clazz.getAnnotationsByType(RestVerticle.class) ) {
                router.route(anno.url()).handler(handler);
            }
        }
    }
    
    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        
    }

}

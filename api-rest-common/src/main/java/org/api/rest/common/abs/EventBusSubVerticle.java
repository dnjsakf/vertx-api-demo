package org.api.rest.common.abs;

import java.lang.reflect.Method;
import java.util.Set;

import org.api.rest.common.Launcher;
import org.api.rest.common.annotation.EventBusVerticle;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;

public class EventBusSubVerticle extends io.vertx.core.AbstractVerticle {
    
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Set<Class<?>> annotatedClass = Launcher.annotatedEventBusVerticle;;
        
        for(Class<?> clazz : annotatedClass) {
            
            Handler<Message<Object>> handler = (Handler<Message<Object>>) clazz.getConstructor().newInstance();
            
            Method setVertx = handler.getClass().getMethod("setVertx", Vertx.class);
            setVertx.invoke(handler, vertx);
            
            for(EventBusVerticle anno : clazz.getAnnotationsByType(EventBusVerticle.class) ) {
                vertx.eventBus()
                    .consumer(anno.address())
                    .handler(handler);
            }
        }
        
    }
}

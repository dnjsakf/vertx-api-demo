package org.api.rest.common.abs;

import java.util.Set;

import org.api.rest.common.Launcher;
import org.api.rest.common.annotation.RestVerticle;

import io.vertx.core.Future;
import io.vertx.ext.web.Router;

public class SubRestVerticle extends io.vertx.core.AbstractVerticle {
    
    private Router router;
    
    public SubRestVerticle(Router router){
        this.router = router;
    }
    
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Set<Class<?>> annotatedClass = Launcher.annotatedRestVerticle;;
        
        for(Class<?> clazz : annotatedClass) {
            for(RestVerticle anno : clazz.getAnnotationsByType(RestVerticle.class) ) {
                String url = anno.url();
                
                //router.route(url).handler(clazz);
            }
        }
        
    }
    
    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        
    }

}

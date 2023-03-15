package org.api.rest.common.handler;

import java.lang.reflect.Method;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class RestHandler<T> implements Handler<RoutingContext> {
    
    protected Vertx vertx;
    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void handle(RoutingContext context) {
        
        HttpServerRequest request = context.request();
        HttpMethod httpMethod = request.method();
        String httpMethodName = httpMethod.name().toUpperCase();
        
        // JsonObject body = context.getBodyAsJson();
        
        try {
            JsonObject params = new JsonObject();
            
            Method method = this.getClass().getMethod(httpMethodName, RoutingContext.class, JsonObject.class);
            method.invoke(this, context, params);
            
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

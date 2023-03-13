package com.heo.dochi.api.common.handler;

import java.lang.reflect.Method;
import java.util.Map.Entry;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public abstract class RestVerticleHandler<T> implements Handler<RoutingContext>  {

    @Override
    public void handle(RoutingContext event) {
        if (event.getBody().length() == 0) {
            event.setBody(Buffer.buffer("{}"));
        }

        HttpMethod method = event.request().method();
        String methodUpperName = method.name().toUpperCase();

        try {
            JsonObject params = new JsonObject();
            params.put("ping", "pong");
            
            // Request Header 출력
            event.request().headers().forEach(action -> {
                Entry<String, String> en = action;
                System.out.println(String.format(en.getKey() + " : " + en.getValue()));
            });
            
            // Request Method 명으로 선언된 Method 실행
            // public void POST, GET, DELETE, PUT ...
            Method m = this.getClass().getMethod(methodUpperName, RoutingContext.class, JsonObject.class);
            m.invoke(this, event, params);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            JsonObject reply = new JsonObject();
            reply.put("err", event.request().path() + " -> Unsupported HTTP METHOD : " + method.name());
            event.response().setStatusCode(405).end(reply.encodePrettily());
        } catch ( Exception e ) {
            e.printStackTrace();
            JsonObject reply = new JsonObject();
            reply.put("err", "Server Error : " + e.getMessage());
            event.response().setStatusCode(500).end(reply.encodePrettily());
        }
    }
    

    protected void OPTIONS(RoutingContext context) throws Exception {
        try {
            context.response().setStatusCode(204)
                .putHeader("Content-Type", "application/json; charset=UTF-8")
                .putHeader("Access-Control-Allow-Credentials", "true")
                .putHeader("Access-Control-Allow-Headers",
                    "DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,accept,x-requested-with,Authorization,Access-Control-Allow-Origin,api_key,origin")
                .putHeader("Access-Control-Allow-Methods", "OPTIONS,PATCH,GET,PUT,POST,DELETE")
                .putHeader("Access-Control-Allow-Origin", "*")
                .end("");
        } catch (Exception e) {
            e.printStackTrace();
            JsonObject reply = new JsonObject();
            reply.put("code", 500);
            reply.put("message", "EOROR FROM SERVER.");
            context.response().end(reply.encodePrettily());
        }
    }
}

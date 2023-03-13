package com.heo.dochi.api.common.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heo.dochi.api.common.Launcher;
import com.heo.dochi.api.common.annotations.RestVerticle;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

@RestVerticle(url="/api/users")
public class UserApiVerticle extends RestVerticleHandler<RoutingContext> {
    
    public void POST(RoutingContext context, JsonObject sessionObj) throws Exception {
        Launcher.getVertx().<String>executeBlocking(future->{
            HttpServerRequest request = context.request();
            ObjectMapper mapper = new ObjectMapper();
            JsonObject json = context.getBodyAsJson();
            JsonObject reqMeta = request.headers().contains("META")?new JsonObject(request.getHeader("META")):context.getBodyAsJson().getJsonObject("meta");
            JsonObject jsonParam = context.getBodyAsJson().getJsonObject("parameters");

            try {
                final JsonNode schemaArticle = mapper.readTree(context.getBodyAsJson().toString());
                json.put("isSuccess", true);
                future.complete(json.encodePrettily());

            } catch (RuntimeException e) {
                e.printStackTrace();
                future.fail(e);
                
            } catch (Exception e) {
                e.printStackTrace();
                future.fail(e);
            }

        }, res->{
            if (res.succeeded()) {
                context.response()
                .putHeader("Content-Type", "application/json; charset=UTF-8")
                .setStatusCode(200)
                .end(res.result());

            } else if (res.failed()) {

                if(res.cause().getCause() instanceof RuntimeException ){
                    context.response().putHeader("Content-Type", "text/plain; charset=UTF-8")
                    .putHeader("Access-Control-Allow-Origin", "*").setStatusCode(400).end(res.cause().getMessage());
                }
                else {
                    context.response().putHeader("Content-Type", "application/json; charset=UTF-8")
                    .putHeader("Access-Control-Allow-Origin", "*").setStatusCode(500)
                    .end(new JsonObject().put("error", "server error").encodePrettily());   
                }
            }       
        });

    }
    
}

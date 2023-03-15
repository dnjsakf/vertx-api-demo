package org.api.rest.common.routes;

import org.api.rest.common.annotation.RestVerticle;
import org.api.rest.common.handler.RestHandler;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

@RestVerticle(url="/api/common")
public class ApiCommonVerticle<T> extends RestHandler<T> {

    public void GET(RoutingContext context, JsonObject params) {
        params.put("ping", "pong");
        
        vertx.eventBus().send("api.common", params);
        
        context.response().putHeader("Content-Type", "application/json; charset=utf-8").end(new JsonObject().put("result", "success").encodePrettily());
    }
}

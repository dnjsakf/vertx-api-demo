package org.api.rest.common.routes;

import org.api.rest.common.annotation.EventBusVerticle;
import org.api.rest.common.handler.EventBusHandler;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

@EventBusVerticle(address="api.common")
public class ApiEventBusVerticle<T> extends EventBusHandler<T> {

    @Override
    public void handle(Message<T> message) {
        System.out.println(String.format("receive.message = %s", ((JsonObject)message.body()).encodePrettily()));
    }

}

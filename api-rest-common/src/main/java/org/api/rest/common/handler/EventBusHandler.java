package org.api.rest.common.handler;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class EventBusHandler<T> implements Handler<Message<T>> {

    protected Vertx vertx;
    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void handle(Message<T> message) {
        JsonObject body = (JsonObject) message.body();
        System.out.println(message.address()+":"+body.encodePrettily());
    }
    
}

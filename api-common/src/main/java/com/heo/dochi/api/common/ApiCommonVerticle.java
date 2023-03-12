package com.heo.dochi.api.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpException;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4JLoggerFactory;
import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.types.HttpEndpoint;


public class ApiCommonVerticle extends AbstractVerticle {
    
    private static Logger logger = LoggerFactory.getLogger(ApiCommonVerticle.class);

    private static final String SERVICE_NAME = "api-coomon";
    
    protected ServiceDiscovery discovery;
    protected CircuitBreaker circuitBreaker;
    protected Set<Record> registeredRecords = new ConcurrentHashSet<>();
    
    public static void main(String[] args) {
        try {
            new Launcher().dispatch(new String[] {
                    "run",
                    "java:" + ApiCommonVerticle.class.getCanonicalName()
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void start(Future<Void> future) throws Exception {
        // init service discovery instance
        discovery = ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions().setBackendConfiguration(config()));

        // init circuit breaker instance
        JsonObject cbOptions = config().getJsonObject("circuit-breaker") != null ? config().getJsonObject("circuit-breaker") : new JsonObject();
        circuitBreaker = CircuitBreaker.create(
                cbOptions.getString("name", "circuit-breaker"), 
                vertx,
                new CircuitBreakerOptions()
                        .setMaxFailures(cbOptions.getInteger("max-failures", 5))
                        .setTimeout(cbOptions.getLong("timeout", 10000L)).setFallbackOnFailure(true)
                        .setResetTimeout(cbOptions.getLong("reset-timeout", 30000L))
        );
        
        // init Routers
        Router router = initRouter(vertx);
        
        final JsonObject CONFIG = config(); // from io.vertx.core.AbstractVerticle
        String host = CONFIG.getString("http.host", "0.0.0.0");
        int port = CONFIG.getInteger("http.port", 9090);
        
        System.out.println(String.format("http.host=%s, http.port=%d", host, port));
        logger.info("http.host=%s, http.port=%d");
        
        createHttpServer(router, host, port)
                .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port))
                .setHandler(future.completer());
    }

    @Override
    public void stop(Future<Void> future) throws Exception {
        // In current design, the publisher is responsible for removing the
        // service
        List<Future> futures = new ArrayList<>();
        registeredRecords.forEach(record -> {
            Future<Void> cleanupFuture = Future.future();
            futures.add(cleanupFuture);
            discovery.unpublish(record.getRegistration(), cleanupFuture.completer());
        });

        if (futures.isEmpty()) {
            discovery.close();
            future.complete();
        } else {
            CompositeFuture.all(futures).setHandler(ar -> {
                discovery.close();
                if (ar.failed()) {
                    future.fail(ar.cause());
                } else {
                    future.complete();
                }
            });
        }
    }
    
    protected Future<Void> createHttpServer(Router router, String host, int port) {
        Future<HttpServer> httpServerFuture = Future.future();
        vertx.createHttpServer().requestHandler(router::accept).listen(port, host, httpServerFuture.completer());
        return httpServerFuture.map(r -> null);
    }

    private Router initRouter(Vertx vertx) {
        Router router = Router.router(vertx);
        
        Set<String> allowHeaders = new HashSet<>();
        allowHeaders.add("x-requested-with");
        allowHeaders.add("Access-Control-Allow-Origin");
        allowHeaders.add("origin");
        allowHeaders.add("Content-Type");
        allowHeaders.add("accept");
        allowHeaders.add("api_key");
        allowHeaders.add("Authorization");
        allowHeaders.add("meta");
        
        Set<HttpMethod> allowMethods = new HashSet<>();
        allowMethods.add(HttpMethod.GET);
        allowMethods.add(HttpMethod.PUT);
        allowMethods.add(HttpMethod.OPTIONS);
        allowMethods.add(HttpMethod.POST);
        allowMethods.add(HttpMethod.DELETE);
        allowMethods.add(HttpMethod.PATCH);

        router.route().handler(BodyHandler.create());
        router.route().handler(CookieHandler.create());
        router.route().handler(CorsHandler.create("*")
                //.allowCredentials(true)
                .maxAgeSeconds(1728000)
                .allowedHeaders(allowHeaders)
                .allowedMethods(allowMethods));
        
        router.route().handler(this::middlewareHandler);
        router.get("/user").handler(this::getDatas);
        router.get("/user/:id").handler(this::getData);
        router.route().failureHandler(this::failureHandler);
        
        return router;
    }
    
    private void middlewareHandler(RoutingContext context){
        JsonArray datas = new JsonArray()
                .add(new JsonObject()
                        .put("id", "dochi")
                        .put("username", "dochi")
                        .put("age", 32)
                        .put("skills", new JsonArray().add("Java").add("Python")))
                .add(new JsonObject()
                        .put("id", "admin")
                        .put("username", "admin")
                        .put("age", 0)
                        .put("skills", new JsonArray()));
        context.put("datas", datas).next();
    }
    
    private void getDatas(RoutingContext context) {
        JsonArray datas = context.get("datas");
        context.response()
            .setStatusCode(200)                
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(datas.encode());
    }
    
    private void getData(RoutingContext context) {
        String id = context.pathParam("id");
        JsonArray datas = context.get("datas");
        JsonObject data = (JsonObject) datas.stream().filter( item -> ((JsonObject) item).getString("id").equals(id) )
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException());
        
        context.response()
            .setStatusCode(200)                
            .putHeader("content-type", "text/plain; charset=utf-8")
            .end(data.encode());
    }
    
    private void failureHandler(RoutingContext context){
        int statusCode = context.statusCode();
        String errorMessage = "Unkown Error";
        
        if(context.failure() instanceof HttpException){
            errorMessage = "Http Exception";
        }
        
        context.response()
              .setStatusCode(statusCode)                
              .putHeader("content-type", "application/json; charset=utf-8")
              .end(new JsonObject().put("status", statusCode).put("err", errorMessage).encodePrettily());
    }
    

    private Future<Void> publish(Record record) {
        if (discovery == null) {
            try {
                start();
            } catch (Exception e) {
                throw new IllegalStateException("Cannot create discovery service");
            }
        }

        Future<Void> future = Future.future();
        discovery.publish(record, ar -> {
            if (ar.succeeded()) {
                registeredRecords.add(record);
                logger.info("Service <" + ar.result().getName() + "> published");
                future.complete();
            } else {
                future.fail(ar.cause());
            }
        });

        return future;
    }
    
    protected Future<Void> publishHttpEndpoint(String name, String host, int port) {
        Record record = HttpEndpoint.createRecord(name, host, port, "/", new JsonObject().put("api.name", config().getString("api.name", "")));
        return publish(record);
    }
}

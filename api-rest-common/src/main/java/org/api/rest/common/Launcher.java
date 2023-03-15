package org.api.rest.common;

import java.util.HashSet;
import java.util.Set;

import org.api.rest.common.abs.AbstractRestVerticle;
import org.api.rest.common.abs.EventBusSubVerticle;
import org.api.rest.common.abs.RestSubVerticle;
import org.api.rest.common.annotation.EventBusVerticle;
import org.api.rest.common.annotation.RestVerticle;
import org.reflections.Reflections;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class Launcher extends io.vertx.core.Launcher {

    public static void main(String[] args) {
        new Launcher().dispatch(args); // run java:MainClass
    }
    
    private Vertx vertx;
    private Router router;
    public static Set<Class<?>> annotatedRestVerticle;
    public static Set<Class<?>> annotatedEventBusVerticle;
    
    /**
     * Launcher가 실행될 때, vertx 및 router 를 저장
     */
    @Override
    public void afterStartingVertx(Vertx vertx) {
       this.vertx = vertx;
       this.router = Router.router(vertx);
    }
    
    /**
     * Verticle이 실행 될 때, 초기 설정
     * - Default Handlers Settings
     *   - BodyHandler, CookieHandler, CORSHandler, StaticHandler  
     * - Annotated Class Reflection
     * - Default Verticle 배포
     */
    @Override
    public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {

        // Default Handlers Settings
        router.route().handler(BodyHandler.create());
        router.route().handler(CookieHandler.create());
        router.route().handler(StaticHandler.create("assets"));
        router.route().handler(CORSHandlerCreate("*"));
        router.route("/").handler(ctx->{
           ctx.response().setStatusCode(200).sendFile("index.html"); 
        });
        
        // Annotated Class Reflections
        Reflections clazz = new Reflections("org.api.rest");
        annotatedRestVerticle = clazz.getTypesAnnotatedWith(RestVerticle.class); // Rest Route 처리용
        annotatedEventBusVerticle = clazz.getTypesAnnotatedWith(EventBusVerticle.class); // EventBus 처리용
        
        // Deploying Sub Verticles
        DeploymentOptions subDeployOptions = new DeploymentOptions();
        subDeployOptions.setWorker(true);
        subDeployOptions.setMultiThreaded(true);
        
        AbstractRestVerticle.setRouter(router);
        
        vertx.deployVerticle(new RestSubVerticle(router), subDeployOptions);
        vertx.deployVerticle(new EventBusSubVerticle(), subDeployOptions);
    }
    
    /**
     * Create CORSHandler
     * @param allowedOriginPattern
     * @return
     */
    private CorsHandler CORSHandlerCreate(String allowedOriginPattern) {
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
        
        return CorsHandler.create(allowedOriginPattern)
                .allowedHeaders(allowHeaders)
                .allowedMethods(allowMethods);
    }
}

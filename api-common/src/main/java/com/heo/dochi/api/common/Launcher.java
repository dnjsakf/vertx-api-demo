package com.heo.dochi.api.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heo.dochi.api.common.annotations.RestVerticle;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.parsetools.JsonParser;

public class Launcher extends io.vertx.core.Launcher {
    
    private static Logger logger = LoggerFactory.getLogger(Launcher.class);
    
    private static String ACTIVE_MODE;
    private static Vertx vertx;
    private static Set<Class<?>> annotatedRestClass;

    static {
        ACTIVE_MODE = System.getProperty("spring.profiles.active", "local");
    }
    
    public static void main(String[] args) {
        new Launcher().dispatch(args);
    }
    
    public static Vertx getVertx() {
        return vertx;
    }
    public static Set<Class<?>> getRestAnnotations() {
        return annotatedRestClass;
    }
    
    @Override
    public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
        if (deploymentOptions.getConfig() == null) {
            deploymentOptions.setConfig(new JsonObject());
        }

        deploymentOptions.setWorkerPoolSize(20);
        deploymentOptions.setWorker(true);
        deploymentOptions.setInstances(2);
        
        addDeploymentOptions(deploymentOptions);
        
        // Reflections reflections = new Reflections("com.heo.dochi");
        
        StringBuffer strb = new StringBuffer();
        strb.append("\n############### afterStartingVertx ###############");
        strb.append("\n>>> Thread.currentThread.name : " + Thread.currentThread().getName());
        strb.append("\n>>> vertx deploymentIDs : " + vertx.deploymentIDs());
        strb.append("\n>>> vertx toString : " + vertx.toString());
        strb.append("\n>>> vertx hashCode : " + vertx.hashCode());
        strb.append("\n>>> vertx sharedData : " + vertx.sharedData());

        logger.debug(strb.toString());
        

        Reflections reflections = new Reflections("com.heo.dochi");
        annotatedRestClass = reflections.getTypesAnnotatedWith(RestVerticle.class);

    }
    
    @Override
    public void afterStartingVertx(Vertx _vertx) {
        vertx = _vertx;

        StringBuffer strb = new StringBuffer();
        strb.append("\n############### afterStartingVertx ###############");
        strb.append("\n>>> Thread.currentThread.name : " + Thread.currentThread().getName());
        strb.append("\n>>> vertx deploymentIDs : " + vertx.deploymentIDs());
        strb.append("\n>>> vertx toString : " + vertx.toString());
        strb.append("\n>>> vertx hashCode : " + vertx.hashCode());
        strb.append("\n>>> vertx sharedData : " + vertx.sharedData());

        logger.debug(strb.toString());
    }
    

    private void addDeploymentOptions(DeploymentOptions deploymentOptions) {
        if( deploymentOptions.getConfig() == null ) {
            deploymentOptions.setConfig(new JsonObject());
        }

        String commonJsonPath = "/conf/".concat(ACTIVE_MODE).concat("/common.json");

        InputStream commonJsonPathIs = JsonParser.class.getResourceAsStream(commonJsonPath);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonCommon = null;
        JsonObject commonJson = null;
        try {
            jsonCommon = mapper.readTree(commonJsonPathIs);
            commonJson = new JsonObject(jsonCommon.toString());
            deploymentOptions.getConfig().mergeIn(commonJson); 
            
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            logger.error("CONFIG PARSE ERROR");
            
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("CONFIG PARSE IO ERROR");
            
        } finally {
            if (commonJsonPathIs != null) {
                try {
                    commonJsonPathIs.close();
                    
                } catch (IOException e) {
                    logger.error("COMMON CONFIG PARSE");
                    
                } finally {
                    commonJsonPathIs = null;
                }
            }
        }
    }
    
}

package org.api.rest.common.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class JedisConnectionPool {

    private static JedisPool jedisPool;
    private static String host;
    private static int port;
    
    static {
        host = "127.0.0.1";
        port = 7000;
    }
    
    public JedisConnectionPool() throws Exception {
        jedisPool = initJedisPool();
    }
    
    public JedisConnectionPool(String host, int port) throws Exception {
        jedisPool = initJedisPool(host, port);
    }
    
    public static void close(Jedis jedis) {
        if( jedis != null && jedis.isConnected() ) {
            jedis.close();
            jedis = null;
        }
    }
    
    public static Jedis getJedisConnection() {
        return getJedisConnection(-1);
    }
    
    public static Jedis getJedisConnection(int db) {
        Jedis jedis = null;
        
        try {
            if (jedisPool == null) { 
                jedisPool = initJedisPool(host, port);
            }

            System.out.println(jedisPool);
            System.out.println(jedisPool.getNumActive());
            System.out.println(jedisPool.getNumIdle());
            System.out.println(jedisPool.isClosed());
            System.out.println(jedisPool.getNumWaiters());
            System.out.println(jedisPool.toString());
            
            jedis = jedisPool.getResource();
            if( db >= 0 ) {
                jedis.select(db);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            close(jedis);
        }
        
        return jedis;
    }
    
    private static JedisPool initJedisPool() throws Exception {
        return initJedisPool(host, port, 20);
    }
    
    private static JedisPool initJedisPool(String host, int port) throws Exception {
        return initJedisPool(host, port, 20);
    }
    
    private static JedisPool initJedisPool(String host, int port, int maxCnt) throws Exception {
        Jedis redis = null; 
        JedisPool pool = null;
        JedisPoolConfig config = null; 
        
        try {
            config = new JedisPoolConfig();
            config.setMaxTotal(maxCnt); 
            config.setMaxIdle(10);
            config.setMinIdle(10);
            config.setMaxWaitMillis(30000);
            
            pool = new JedisPool(config, host, port);
            redis = pool.getResource();
            redis.close();
            
        } catch(JedisConnectionException e) {
            e.printStackTrace();
            pool = null;
            
        } catch(Exception e) {
            e.printStackTrace();
            pool = null;
        }

        return pool;
    }
}

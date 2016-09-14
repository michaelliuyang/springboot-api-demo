package com.springboot.api.core.cache;

import com.springboot.api.tools.RecordLogger;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.util.Map;

public abstract class JedisManager {

    @Autowired
    protected JedisPool jedisPool;

    protected void set(int dbIndex, String key, String value, int cacheSeconds) {
        long beginTime = System.currentTimeMillis();
        boolean isBroken = false;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            jedis.set(key, value);
            if (cacheSeconds >= 0)
                jedis.expire(key, cacheSeconds);
        } catch (Exception e) {
            isBroken = true;
            throw new JedisException(e);
        } finally {
            release(jedis, isBroken);
            long endTime = System.currentTimeMillis();
            long runningTime = endTime - beginTime;
            RecordLogger.timeLog("java-redis-set", runningTime);
        }
    }

    protected String get(int dbIndex, String key) {
        long beginTime = System.currentTimeMillis();
        boolean isBroken = false;
        Jedis jedis = null;
        String value = null;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            value = jedis.get(key);
        } catch (Exception e) {
            isBroken = true;
            throw new JedisException(e);
        } finally {
            release(jedis, isBroken);
            long endTime = System.currentTimeMillis();
            long runningTime = endTime - beginTime;
            RecordLogger.timeLog("java-redis-get", runningTime);
        }
        return value;
    }

    protected void del(int dbIndex, String... key) {
        long beginTime = System.currentTimeMillis();
        boolean isBroken = false;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            jedis.del(key);
        } catch (Exception e) {
            isBroken = true;
            throw new JedisException(e);
        } finally {
            release(jedis, isBroken);
            long endTime = System.currentTimeMillis();
            long runningTime = endTime - beginTime;
            RecordLogger.timeLog("java-redis-del", runningTime);
        }
    }

    protected Map<String, String> hgetAll(int dbIndex, String key) {
        long beginTime = System.currentTimeMillis();
        boolean isBroken = false;
        Jedis jedis = null;
        Map<String, String> result = null;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            result = jedis.hgetAll(key);
        } catch (Exception e) {
            isBroken = true;
            throw new JedisException(e);
        } finally {
            release(jedis, isBroken);
            long endTime = System.currentTimeMillis();
            long runningTime = endTime - beginTime;
            RecordLogger.timeLog("java-redis-hgetAll", runningTime);
        }
        return result;
    }

    protected String hget(int dbIndex, String key, String field) {
        long beginTime = System.currentTimeMillis();
        boolean isBroken = false;
        Jedis jedis = null;
        String result = null;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            result = jedis.hget(key, field);
        } catch (Exception e) {
            isBroken = true;
            throw new JedisException(e);
        } finally {
            release(jedis, isBroken);
            long endTime = System.currentTimeMillis();
            long runningTime = endTime - beginTime;
            RecordLogger.timeLog("java-redis-hget", runningTime);
        }
        return result;
    }

    protected void hset(int dbIndex, String key, String field, String value) {
        long beginTime = System.currentTimeMillis();
        boolean isBroken = false;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            jedis.hset(key, field, value);
        } catch (Exception e) {
            isBroken = true;
            throw new JedisException(e);
        } finally {
            release(jedis, isBroken);
            long endTime = System.currentTimeMillis();
            long runningTime = endTime - beginTime;
            RecordLogger.timeLog("java-redis-hset", runningTime);
        }
    }

    protected void hmset(int dbIndex, String key, Map<String, String> maps, int cacheSeconds) {
        long beginTime = System.currentTimeMillis();
        boolean isBroken = false;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            jedis.hmset(key, maps);
            if (cacheSeconds >= 0)
                jedis.expire(key, cacheSeconds);
        } catch (Exception e) {
            isBroken = true;
            throw new JedisException(e);
        } finally {
            release(jedis, isBroken);
            long endTime = System.currentTimeMillis();
            long runningTime = endTime - beginTime;
            RecordLogger.timeLog("java-redis-hmset", runningTime);
        }
    }

    protected void incr(int dbIndex, String key, int cacheSeconds) {
        long beginTime = System.currentTimeMillis();
        boolean isBroken = false;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            jedis.incr(key);
            if (cacheSeconds >= 0)
                jedis.expire(key, cacheSeconds);
        } catch (Exception e) {
            isBroken = true;
            throw new JedisException(e);
        } finally {
            release(jedis, isBroken);
            long endTime = System.currentTimeMillis();
            long runningTime = endTime - beginTime;
            RecordLogger.timeLog("java-redis-incr", runningTime);
        }
    }

    protected Jedis getJedis() {
        return jedisPool.getResource();
    }

    protected void release(Jedis jedis, boolean isBroken) {
        if (jedis == null) {
            throw new JedisException("Jedis为空");
        }
        if (isBroken) {
            jedisPool.returnBrokenResource(jedis);
        } else {
            jedisPool.returnResource(jedis);
        }
    }

}

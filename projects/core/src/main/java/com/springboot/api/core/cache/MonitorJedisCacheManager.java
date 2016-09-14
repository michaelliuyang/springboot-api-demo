package com.springboot.api.core.cache;

import com.springboot.api.tools.LogContext;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@Component
public class MonitorJedisCacheManager extends JedisManager implements IMonitorCacheManager {

    public boolean monitor() {
        boolean result = false;
        boolean isBroken = false;
        Jedis jedis = null;
        try {
            jedis = getJedis();
            String pingResult = jedis.ping();
            result = "PONG".equals(pingResult);
        } catch (Exception e) {
            isBroken = true;
            LogContext.instance().error(e, "监控REDIS错误");
        } finally {
            try {
                release(jedis, isBroken);
            } catch (Exception e) {
                LogContext.instance().error(e, "监控REDIS错误");
            }
        }
        return result;
    }
}
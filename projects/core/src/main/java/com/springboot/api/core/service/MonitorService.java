package com.springboot.api.core.service;

import com.springboot.api.core.cache.IMonitorCacheManager;
import com.springboot.api.core.mapper.IMonitorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MonitorService {

    @Autowired
    private IMonitorMapper monitorMapper;
    @Autowired
    private IMonitorCacheManager monitorCacheManager;

    public int monitorDB() {
        return monitorMapper.monitorDB();
    }

    public boolean monitorCache() {
        return monitorCacheManager.monitor();
    }

}

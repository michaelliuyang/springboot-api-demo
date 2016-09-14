package com.springboot.api.app.controller;

import com.springboot.api.core.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class MonitorController {

    @Autowired
    private MonitorService monitorService;

    @RequestMapping(value = "monitor", method = RequestMethod.GET)
    public String index() {
        return "db:" + monitorService.monitorDB() + ",cache:" + monitorService.monitorCache();
    }

}
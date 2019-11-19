package com.wirecard.wpp.integration.demo.controllers;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationsController {

    private static final String NOTIFICATIONS = "/notifications";

    private final Logger log = LoggerFactory.getLogger(NotificationsController.class);

    @RequestMapping(value = NOTIFICATIONS, method = RequestMethod.POST)
    public void storeNotification(@RequestBody String body) {
        JSONObject jsonObj = new JSONObject(body);
        String key = jsonObj.getJSONObject("payment").getString("request-id");
        log.info("Notification for payment with request-id: {}", key);
    }

}

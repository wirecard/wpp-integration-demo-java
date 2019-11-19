package com.wirecard.wpp.integration.demo.controllers;


import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.text.MessageFormat;
import java.util.Base64;

@RestController
public class TransactionsController {

    private static final String FIND_TRANSACTION = "/find";

    private final String base64Credentials;

    @Value("${ee.api.endpoint}")
    private String eeApiEndpoint;

    @Value("${ee.maid}")
    private String maid;

    public TransactionsController(@Value("${ee.username}") String username, @Value("${ee.password}") String password) {
        this.base64Credentials = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    private ModelAndView findPayment(String eeUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "Basic " + base64Credentials);
        headers.set(HttpHeaders.ACCEPT, "application/json");
        HttpEntity<String> request = new HttpEntity<String>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(eeUrl, HttpMethod.GET, request, String.class);
        String body = responseEntity.getBody();

        return new ModelAndView("result")
                .addObject("payment", new JSONObject(body).toString(2))
                .addObject("msg", "Found")
                .addObject("status", "Found");
    }

    @RequestMapping(value = FIND_TRANSACTION, method = RequestMethod.POST)
    public ModelAndView findTransaction(@RequestParam(value = "tid", required = false) String transactionId,
                                        @RequestParam(value = "rid", required = false) String requestId) throws Exception {
        String eeUrl = "";
        if (!StringUtils.isEmpty(transactionId)) {
            eeUrl = MessageFormat.format(this.eeApiEndpoint, this.maid, transactionId);
        } else if (!StringUtils.isEmpty(requestId)) {
            eeUrl = MessageFormat.format(this.eeApiEndpoint, this.maid, "search?payment.request-id=" + requestId);
        }
        return this.findPayment(eeUrl);
    }


}

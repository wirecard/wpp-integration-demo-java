package com.wirecard.wpp.integration.demo.controllers;


import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;


@RestController
public class WppRestController {

    private final String base64Credentials;

    private final String REGISTER = "/api/register";
    private final String SEAMLESS = "seamless";
    private final String EMBEDDED = "wpp-embedded";

    @Value("${wpp.register.endpoint}")
    private String wppRegisterEndpoint;

    @Value("${ee.maid}")
    private String maid;

    @Value("${frame.ancestor}")
    private String frameAncestor;

    private JSONObject getPaymentModel(String mode, String paymentMethod) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            String payment = IOUtils.toString(classLoader.getResourceAsStream("payment" + File.separator +"payment.json"));

            JSONObject json = new JSONObject(payment);

            json.getJSONObject("payment").put("request-id",  UUID.randomUUID());
            json.getJSONObject("payment").getJSONObject("merchant-account-id").put("value", this.maid);
            json.getJSONObject("payment")
                    .getJSONObject("payment-methods")
                    .getJSONArray("payment-method").put(new JSONObject("{name:" + paymentMethod+ "}"));

            if (SEAMLESS.equals(mode) || EMBEDDED.equals(mode)){
                json.getJSONObject("options").put("mode", mode);
            }
            // frame-ancestor must be set for SEAMLESS mode and EMBEDDED integration
            if (!"".equals(mode) && null != mode){
                json.getJSONObject("options").put("frame-ancestor", frameAncestor);
            }
            return json;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public WppRestController(@Value("${ee.username}") String username, @Value("${ee.password}") String password){
        this.base64Credentials = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

    }


    @RequestMapping(value = REGISTER, method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String registerPayment(HttpServletRequest request, HttpServletResponse response) {
        try {
            String jsonRaw = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            JSONObject req = new JSONObject(jsonRaw);
            // only for demo - payment model for request
            JSONObject json=getPaymentModel(req.get("mode").toString(), req.get("payment-method").toString());
            // post request to wpp with Basic authorization - register payment and get URL to WPP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.AUTHORIZATION, "Basic " + base64Credentials);
            HttpEntity<String> request2WPP = new HttpEntity<>(json.toString(), headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(wppRegisterEndpoint, request2WPP, String.class);
            response.setStatus(responseEntity.getStatusCodeValue());
            return responseEntity.getBody();
        } catch (Exception e) {
            if (e instanceof HttpClientErrorException) {
                HttpClientErrorException he = (HttpClientErrorException) e;
                response.setStatus(he.getStatusCode().value());
                return he.getResponseBodyAsString();
            }
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return null;
    }

}

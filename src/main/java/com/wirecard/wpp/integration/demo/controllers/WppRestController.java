package com.wirecard.wpp.integration.demo.controllers;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.springframework.web.bind.annotation.RequestMethod.GET;


@RestController
public class WppRestController {

    private final String base64Credentials;

    private final String REGISTER = "/api/register";
    private final String LOADER = "/api/wpp-loader-js-url";
    private final String MAID = "/api/maid";

    @Value("${wpp.rest.payment.register.endpoint}")
    private String wppRegisterEndpoint;

    @Value("${wpp.paymentpageloader}")
    private String wppLoaderJsUrl;

    @Value("${wpp.maid}")
    private String maid;

    public WppRestController(@Value("${ee.username}") String username, @Value("${ee.password}") String password){
        this.base64Credentials = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    @RequestMapping(value = REGISTER, method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String registerJson(HttpServletRequest request, HttpServletResponse response) {
        try {
            String jsonRaw = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(jsonRaw);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.AUTHORIZATION, "Basic " + base64Credentials);
            HttpEntity<String> request2Fitzroy = new HttpEntity<>(json.toString(), headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(wppRegisterEndpoint, request2Fitzroy, String.class);
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


    @RequestMapping(value = LOADER, method = GET)
    @ResponseBody
    public String getWppLoaderJsUrl(){
        return wppLoaderJsUrl;
    }

    @RequestMapping(value = MAID, method = GET)
    @ResponseBody
    public String getWppMaid(){
        return maid;
    }

}

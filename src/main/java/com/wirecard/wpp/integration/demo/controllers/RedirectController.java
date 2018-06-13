package com.wirecard.wpp.integration.demo.controllers;


import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
public class RedirectController {

    private static final String SUCCESS = "/success";
    private static final String FAIL = "/fail";
    private static final String CANCEL = "/cancel";

    @Value("${ee.secretkey}")
    private String merchantSecretKey;

    @RequestMapping(value = SUCCESS, method = RequestMethod.POST)
    public ModelAndView success(HttpServletRequest request) throws Exception {
        return this.prepareDataForView(request)
                .addObject("msg", "Transaction was success")
                .addObject("status","Success");
    }

    @RequestMapping(value = FAIL, method = RequestMethod.POST)
    private ModelAndView fail(HttpServletRequest request) throws Exception {
        return this.prepareDataForView(request)
                .addObject("msg", "Transaction failed")
                .addObject("status","Fail");
    }

    @RequestMapping(value = CANCEL, method = RequestMethod.POST)
    private ModelAndView cancel(HttpServletRequest request) throws Exception {
        return this.prepareDataForView(request)
                .addObject("msg", "Transaction was canceled")
                .addObject("status","Cancel");
    }

    private ModelAndView prepareDataForView(HttpServletRequest request) throws Exception {
        JSONObject jsonObject = new JSONObject(this.decode(request.getParameter("response-base64")));
        return new ModelAndView("result")
                .addObject("responseSignatureBase64", request.getParameter("response-signature-base64"))
                .addObject("responseSignatureAlgorithm", request.getParameter("response-signature-algorithm"))
                .addObject("responseBase64", request.getParameter("response-base64"))
                .addObject("decodedResponseBase64", jsonObject.toString(2))
                .addObject("validSignature",
                        this.isValidSignature(request.getParameter("response-base64"),
                                request.getParameter("response-signature-base64"),
                                request.getParameter("response-signature-algorithm")));
    }


    private String decode(String s) {
        byte[] decoded = Base64.getDecoder().decode(s);
        return new String(decoded, StandardCharsets.UTF_8);
    }

    private boolean isValidSignature(String responseBase64, String responseBase64Signature, String responseSignatureAlgorithm) throws Exception {
        Mac mac = Mac.getInstance(responseSignatureAlgorithm);
        mac.init(new SecretKeySpec(merchantSecretKey.getBytes("UTF-8"), responseSignatureAlgorithm));
        return responseBase64Signature != null && responseBase64Signature.equals(DatatypeConverter.printBase64Binary(mac.doFinal(responseBase64.getBytes("UTF-8"))));
    }
}

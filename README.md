# Wirecard Payment Page (WPP) Integration demo

This is a sample integration of WPP into your e-shop using Java. 

## Requirements

* JDK 1.8
* Maven

## Properties

```
#eshop backend app port
server.port=8080

#payment gateway username (api username)
ee.username=70000-APIDEMO-CARD

#payment gateway password (api password)
ee.password=ohysS0-dvfMx

#wirecard payment page merchant-id
ee.maid=7a6dd74f-06ab-4f3f-a864-adc52687270a

#wirecard payment page merchant secret key
ee.secretkey=a8c3fce6-8df7-4fd6-a1fd-62fa229c5e55

#rest service endpoint for registering payment (initial requests)
wpp.register.endpoint=https://wpp-test.wirecard.com/api/payment/register

#origin location for SEAMLESS mode and EMBEDDED integration
frame.ancestor=http://localhost:${server.port}
```

## Payment model

All requests/responses use the JSON format.

```
{
  "payment":{
    "merchant-account-id":{
      "value":""
    },
    "request-id": "",
    "transaction-type":"auto-sale",
    "requested-amount":{
      "value":10,
      "currency":"EUR"
    },
    "payment-methods":{
      "payment-method":[]
    },
    "success-redirect-url": "http://localhost:8080/success",
    "fail-redirect-url": "http://localhost:8080/fail",
    "cancel-redirect-url": "http://localhost:8080/cancel"
  },
  "options": {
    "frame-ancestor": ""
  }
}
```

## Build

``mvn clean install``

## Run

Using the Maven Plugin
``mvn spring-boot:run`` and then open your favorite browser and visit **http://localhost:[server.port]**

## Payment

Use test card number **4200000000000018** (CVV **018** , expiration date **01/19**) for the payment

## Authors
Wirecard - Corvus Team

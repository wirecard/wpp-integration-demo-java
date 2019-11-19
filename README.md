# Wirecard Payment Page (WPP) Integration demo

[![License](https://img.shields.io/badge/license-GPLv3-blue.svg)](https://raw.githubusercontent.com/wirecard/wpp-integration-demo-java/master/LICENSE)
[![JDK v1.8](https://img.shields.io/badge/JDK-v1.8-orange.svg)](http://www.java.com/)
[![Wirecard Company](https://img.shields.io/badge/Wirecard-Company-blue.svg)](https://www.wirecard.com/)

This is a sample integration of WPP into your e-shop using Java. 

## Requirements

* JDK 1.8
* Maven

## Properties

```
#eshop backend app port
server.port=8080

#payment gateway username (api username)
ee.username=70000-APITEST-AP

#payment gateway password (api password)
ee.password=qD2wzQ_hrc!8

#wirecard payment page merchant-id
ee.maid=ab62ea6e-ba97-48ef-b3bc-bf0319e09d78

#wirecard payment page merchant secret key
ee.secretkey=c50a8e09-0648-4d2c-b638-2c14fc7606bc

#elastic engine rest endpoit
ee.api.endpoint=https://api-test.wirecard.com/engine/rest/merchants/{0}/payments/{1}

#rest service endpoint for registering payment (initial requests)
wpp.register.endpoint=https://wpp-test.wirecard.com/api/payment/register

#origin location for SEAMLESS mode and EMBEDDED integration
frame.ancestor=http://localhost:${server.port}
```

## Payment model

All requests/responses use the JSON format.

### Credit card

```
{
  "payment":{
    "merchant-account-id":{
      "value": ""
    },
    "account-holder": {
      "first-name": "John",
      "last-name": "Doe"
    },
    "request-id": "",
    "transaction-type":"auto-sale",
    "requested-amount":{
      "value":10,
      "currency":"EUR"
    },
    "payment-methods":{
      "payment-method":[
        {
          "name": "creditcard"
        }
      ]
    },
    "three-d": {
      "attempt-three-d": "true"
    },
    "success-redirect-url": "http://localhost:8080/success",
    "fail-redirect-url": "http://localhost:8080/fail",
    "cancel-redirect-url": "http://localhost:8080/cancel",
    "notifications": {
      "format": "application/json",
      "notification": [
        {
          "transaction-state": "success",
          "url": "http://your.domain.com/notifications"
        }
      ]
    }
  },
  "options": {
    "frame-ancestor": ""
  }
}
```

### iDeal

```
{
  "payment":{
    "merchant-account-id":{
      "value":""
    },
    "request-id": "",
    "transaction-type":"get-url",
    "requested-amount":{
      "value":10,
      "currency":"EUR"
    },
    "account-holder": {
      "first-name": "John",
      "last-name": "Dao"
    },
    "order-number": "15684356856",
    "descriptor": "customer-statement",
    "bank-account": {
      "bic": "INGBNL2A"
    },
    "payment-methods": {
      "payment-method": [
        {
          "name": "ideal"
        }
      ]
    },
    "success-redirect-url": "http://localhost:8080/success",
    "fail-redirect-url": "http://localhost:8080/fail",
    "cancel-redirect-url": "http://localhost:8080/cancel",
    "notifications": {
      "format": "application/json",
      "notification": [
        {
          "transaction-state": "success",
          "url": "http://your.domain.com/notifications"
        }
      ]
    }
  },
  "options": {
    "frame-ancestor": ""
  }
}
```

### PayPal

```
{
  "payment":{
    "merchant-account-id":{
      "value":""
    },
    "account-holder": {
      "first-name": "John",
      "last-name": "Dao"
    },
    "request-id": "",
    "transaction-type":"auto-sale",
    "requested-amount":{
      "value":10,
      "currency":"EUR"
    },
    "payment-methods": {
      "payment-method": [
        {
          "name": "paypal"
        }
      ]
    },
    "success-redirect-url": "http://localhost:8080/success",
    "fail-redirect-url": "http://localhost:8080/fail",
    "cancel-redirect-url": "http://localhost:8080/cancel",
    "notifications": {
      "format": "application/json",
      "notification": [
        {
          "transaction-state": "success",
          "url": "http://your.domain.com/notifications"
        }
      ]
    }
  },
  "options": {
    "frame-ancestor": "http://localhost:8080"
  }
}
```

### Sepa

```
{
  "payment":{
    "merchant-account-id":{
      "value":""
    },
    "request-id": "",
    "transaction-type": "debit",
    "requested-amount":{
      "value":10,
      "currency":"EUR"
    },
    "account-holder": {
      "first-name": "John",
      "last-name": "Dao"
    },
    "payment-methods": {
      "payment-method": [
        {
          "name": "sepadirectdebit"
        }
      ]
    },
    "mandate": {
      "mandate-id": "12345678",
      "signed-date": "2018-08-14"
    },
    "creditor-id": "DE98ZZZ09999999999",
    "success-redirect-url": "http://localhost:8080/success",
    "fail-redirect-url": "http://localhost:8080/fail",
    "cancel-redirect-url": "http://localhost:8080/cancel",
    "notifications": {
      "format": "application/json",
      "notification": [
        {
          "transaction-state": "success",
          "url": "http://your.domain.com/notifications"
        }
      ]
    }
  },
  "options": {
    "frame-ancestor": ""
  }
}
```

### Sofort

```
{
  "payment":{
    "merchant-account-id":{
      "value":""
    },
    "request-id": "",
    "transaction-type": "debit",
    "requested-amount":{
      "value":10,
      "currency":"EUR"
    },
    "account-holder": {
      "first-name": "John",
      "last-name": "Dao"
    },
    "payment-methods": {
      "payment-method": [
        {
          "name": "sofortbanking"
        }
      ]
    },
    "descriptor": "FANZEE XRZ-1282",
    "success-redirect-url": "http://localhost:8080/success",
    "fail-redirect-url": "http://localhost:8080/fail",
    "cancel-redirect-url": "http://localhost:8080/cancel",
    "notifications": {
      "format": "application/json",
      "notification": [
        {
          "transaction-state": "success",
          "url": "http://your.domain.com/notifications"
        }
      ]
    }
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

### Credit Card

```
First & last name: arbitrary
Credit card number (PAN): 4012000300001003
CVV: 003
Expiry date: arbitrary month / year in the future
```

### SEPA Direct Debit

You can enter the following IBAN in the input mask.
Optionally you can provide the IBAN in the request body, so that the payment will be executed without consumer interaction (so-called "silent pay").
```
"bank-account": {
      "iban": "DE42512308000000060004"
}
```
 
### Sofort

```
Country: whatever you like  
Bank Name: Demo Bank
Reference Number: any alphanumeric combination with more than 3 characters
Password: any alphanumeric combination with more than 3 characters
Tan Code: 12345
```

### PayPal
```
Email: paypal.buyer2@wirecard.com
Password: Wirecardbuyer
```

### iDeal

No further test data needed.

## Authors
Wirecard

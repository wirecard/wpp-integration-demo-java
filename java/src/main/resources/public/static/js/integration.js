var clientApi = {
    request: {
        "payment":{
            "merchant-account-id":{
                "value":""
            },
            "request-id": "",
            "transaction-type":"authorization",
            "requested-amount":{
                "value":10,
                "currency":"EUR"
            },
            "account-holder":{
                "first-name":"John",
                "last-name":"Doe"
            },
            "payment-methods":{
                "payment-method":[
                    {
                        "name":"creditcard"
                    }
                ]
            },
            "success-redirect-url": "http://localhost:8088/success",
            "fail-redirect-url": "http://localhost:8088/fail",
            "cancel-redirect-url": "http://localhost:8088/cancel"
        },
        "options": {
            "frame-ancestor": ""
        }
    },
    downloadWppLoader:function () {
        var loader = $.get("/api/wpp-loader-js-url", function (paymentPageJsUrl) {
            var el = document.createElement('script')
            el.setAttribute('type', 'text/javascript')
            el.setAttribute('src', paymentPageJsUrl)
            document.getElementsByTagName('head')[0].appendChild(el)
        })
            .done(function () {
                console.log('wpp loader downloaded')
            })
            .fail(function () {
                console.log('wpp loader download error')
            })
    },
    setMaid: function () {
        var self = this
        var maid = $.get("/api/maid", function (id) {
            self.request["payment"]["merchant-account-id"]["value"] = id
        }).done(function () {
            console.log('get wpp maid success')
        })
            .fail(function () {
                console.log('get wpp maid error')
            })
    },
    register:function (request, succ, fail) {
        const URL = '/api/register'
        $.ajax({
            url: URL,
            type: 'POST',
            data: request,
            contentType: 'application/json',
            dataType: 'text',
            success: function (response) {
                succ(response)
            },
            error: function (err) {
                console.log('Error while registering request', err)
                fail(err)
            }
        })
    },
    payJson:function (request,paymentMode) {
        var self = this;
        this.register(request, function (resp) {
            var respObj = JSON.parse(resp)
            if (respObj['payment-redirect-url']) {
                switch (paymentMode) {
                    case 'wpp-embedded' :
                        WPP.embeddedPayUrl(respObj['payment-redirect-url'])
                        break;
                    case 'wpp-standalone' : // 'wpp-standalone'
                        WPP.hostedPayUrl(respObj['payment-redirect-url'])
                        break
                    case 'wpp-seamless-render':
                        WPP.seamlessRender({
                            wrappingDivId: 'seamless-form-target',
                            url: respObj['payment-redirect-url'],
                            onSuccess: function () {
                                console.log('Seamless form rendered.')

                            },
                            onError: function (response) {
                                self.stringifySeamlessPaymentResponse(response)
                            }
                        })
                        break
                    case 'wpp-seamless-submit':
                        WPP.seamlessSubmit({
                            onSuccess: function (response) {
                                console.log("success")
                                self.stringifySeamlessPaymentResponse(response)
                            },
                            onError: function (response) {
                                console.error(response)
                                self.stringifySeamlessPaymentResponse(response)
                            }
                        })
                        break
                    default:
                        break
                }
            } else {
                console.error('No redirect-url find in response: ' + response)
            }
        }, function (err) {
            console.error('Payment registration failed: ' + err.status + ': ' + err.statusText + ' : ' + err.responseText)
        } )
    },
    uuid: function guid() {
        function s4() {
            return Math.floor((1 + Math.random()) * 0x10000)
                .toString(16)
                .substring(1);
        }
        return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
    },
    locationOrigin:  function () {
        return window.location.protocol + '//' + window.location.hostname + (window.location.port ? (':' + window.location.port) : '')
    },
    pay: function (mode) {
        console.log("mode: ", mode)
        this.request["payment"]["request-id"] = this.uuid()
        switch (mode) {
            case 'wpp-standalone':
                this.show("submit", false)
                this.request["options"]["mode"] = ""
                this.payJson(JSON.stringify(this.request), 'wpp-standalone')
                break
            case 'wpp-embedded':
                this.show("submit", false)
                this.request["options"]["mode"] = ""
                this.request["options"]["frame-ancestor"] = this.locationOrigin()
                this.payJson(JSON.stringify(this.request), 'wpp-embedded')
                break;
            case 'wpp-seamless-render':
                this.show("submit", true)
                this.request["options"]["frame-ancestor"] = this.locationOrigin()
                this.request["options"]["mode"] = "seamless"
                this.payJson(JSON.stringify(this.request), 'wpp-seamless-render')
                break;
            case 'wpp-seamless-submit':
                this.request["options"]["frame-ancestor"] = this.locationOrigin()
                this.request["options"]["mode"] = "seamless"
                this.payJson(JSON.stringify(this.request), 'wpp-seamless-submit')
                break;
        }
    },
    show: function(id,show) {
        var style = document.getElementById(id).style.display;
        if (show) {
            document.getElementById(id).style.display = '';
        } else {
            document.getElementById(id).style.display = "none"
        }
    },
    stringifySeamlessPaymentResponse: function (response) {
        return response ? JSON.stringify(response).replace(/,/g, ', ').replace(/":"/g, '": "') : "Unknown error";
    }
}
clientApi.downloadWppLoader();
clientApi.setMaid();

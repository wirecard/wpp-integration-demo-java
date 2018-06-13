var clientApi = {
    show: function(id,show) {
        var style = document.getElementById(id).style.display;
        if (show) {
            document.getElementById(id).style.display = '';
        } else {
            document.getElementById(id).style.display = "none"
        }
    },
    integrateWPP:function (data) {
        function getRedirectUrlToWpp(request, succ, fail) {
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
        };
        var self = this;
        // set payment method
        data["payment-method"] = "creditcard"
        getRedirectUrlToWpp(JSON.stringify(data), function (resp) {
            var respObj = JSON.parse(resp)
            if (respObj['payment-redirect-url']) {
                switch (data.mode) {
                    case 'wpp-embedded' :
                        WPP.embeddedPayUrl(respObj['payment-redirect-url'])
                        break;
                    case 'wpp-standalone' :
                        WPP.hostedPayUrl(respObj['payment-redirect-url'])
                        break
                    case 'wpp-seamless-render':
                        WPP.seamlessRender({
                            wrappingDivId: 'seamless-form-target',
                            url: respObj['payment-redirect-url'],
                            onSuccess: function () {
                                console.log('Seamless form rendered.')
                            },
                            onError: function (err) {
                               console.error(err)
                            }
                        })
                        break
                    case 'wpp-seamless-submit':
                        WPP.seamlessSubmit({
                            onSuccess: function (response) {
                                console.log("success")
                            },
                            onError: function (response) {
                                console.error(response)
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
    pay: function (mode) {
        switch (mode) {
            case 'wpp-standalone':
                this.show("submit", false)
                this.integrateWPP({mode: 'wpp-standalone'})
                break
            case 'wpp-embedded':
                this.show("submit", false)
                this.integrateWPP({mode: 'wpp-embedded'})
                break;
            case 'wpp-seamless-render':
                this.show("submit", true)
                this.integrateWPP({mode: 'wpp-seamless-render'})
                break;
            case 'wpp-seamless-submit':
                this.integrateWPP({mode: 'wpp-seamless-submit'})
                break;
        }
    }
}

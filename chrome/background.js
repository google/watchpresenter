//Copyright 2014 Google Inc. All Rights Reserved.
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.


var apiUp = false;
var URLRegexp = /http[s]:\/\/docs\.google\.com.*\/presentation/i;
var URLRegexpNoPresent = /http[s]:\/\/docs\.google\.com.*\/presentation(?!.*present\?)/i;
var checkLaunched = false;
var modalRegistrationTabId;
var REGISTRATION_TIMEOUT = 24 * 3600 * 1000;

function registerCallback(registrationId) {
    console.log("on registerCallback");
    var lastError = chrome.runtime.lastError;
    if (lastError) {
        // When the registration fails, handle the error and retry the
        // registration later.
        console.log("Error while registering: " + lastError);
        return;
    }
    console.log("Sending registration ID...");
    // Send the registration ID to your application server.
    sendRegistrationId(registrationId, function (succeed) {
        // Once the registration ID is received by your server,
        // set the flag such that register will not be invoked
        // next time when the app starts up.
        if (succeed) {
            console.log("Registration successful (" + succeed + "). Saving to storage...");
            chrome.storage.local.set({
                registered: true
            });
        }
        releaseModal(succeed);
    });
}

function sendRegistrationId(regId, callback) {
    console.log("Sending registration ID: " + regId);
    gapi.client.registration.register({
        'regId': regId
    }).execute(
        function (response) {
            console.log("sendRegistrationId response: " + response);
            callback(true);
        }
    );
}

function afterAPIUp() {
    console.log("Checking if already registered");
    chrome.storage.local.get("registered", function (result) {
        // If already registered, bail out.
        if (result["registered"])
            console.log("Looks like already registered, but we'll try again anyway");
        else
            console.log("Not registered yet. Registering...");
        // Up to 100 senders are allowed.
        var senderIds = ["122248338560"];
        chrome.gcm.register(senderIds, registerCallback);
    });
    apiUp = true;
}


function loadGoogleAPI() {
    //load Google's javascript client libraries
    window.gapi_onload = authorize;
    loadScript('https://apis.google.com/js/client.js');
}


function requestAuthTokenInteractive() {
    console.log("Requesting auth token interactively");
    //oauth2 auth
    chrome.tabs.insertCSS({
        file: "watchpresenter.css"
    });
    chrome.tabs.executeScript({
        file: "modal_log-in.js"
    });
}

function releaseModal(success) {
    if (modalRegistrationTabId) {
        chrome.tabs.sendMessage(modalRegistrationTabId, {
            greeting: "finishLoading",
            success: success
        });
        modalRegistrationTabId = null;
    } else {
        console.log("Error: releasing modal but no modalTabId found");
    }
}

function interactiveRequestAuthToken(tabId) {
    if (modalRegistrationTabId) {
        console.log("Already modal registration for tab ID: " + modalRegistrationTabId);
    } else {
        modalRegistrationTabId = tabId;
        chrome.identity.getAuthToken({
                'interactive': true
            },
            function (token) {
                var success = false;
                if (chrome.runtime.lastError) {
                    console.log("Failed to get Auth token on interactive mode (Error)");
                    releaseModal(false);
                } else {
                    if (token) {
                        console.log("Valid token found: " + token);
                        loadGoogleAPI();
                        success = true;
                    } else {
                        console.log("Failed to get Auth token on interactive mode");
                        releaseModal(false);
                    }
                }

            }
        );
    }
}


function checkAuthStatus(tabId) {
    chrome.pageAction.show(tabId);


    chrome.identity.getAuthToken({
            'interactive': false
        },
        function (token) {
            var lastError = chrome.runtime.lastError;
            if (lastError) {
                if (lastError.message) {
                    if (lastError.message.match(/\(-106\)/)) {
                        //This error is caused by lack of connection, just go ahead as usual)
                        token = true;
                        console.log("Could not connect. Won't ask interactively for token");
                    } else {
                        console.log("Unknown error while non-interactively retrieving token: " + lastError.message);
                    }
                }
            }
            if (token) {
                console.log("Valid token found");
                loadGoogleAPI();
            } else {
                console.log("Valid token not found");
                chrome.storage.local.set({
                    registered: false
                });
                requestAuthTokenInteractive();
            }
        }
    );
}

function getLastRegistrationURL(callback) {
    chrome.storage.local.get("lastRegistrationURL", function (result) {
        var lastRegistrationURL = result["lastRegistrationURL"];
        callback(lastRegistrationURL);
    });
}

function removeFragment(url) {
    var indexOfHash = url.indexOf('#')
    if (indexOfHash > 0) {
        url = url.substring(0, indexOfHash);
    }
    return url;
}

function setLastRegistrationURL(url) {
    chrome.storage.local.set({
        lastRegistrationURL: removeFragment(url)
    });
}

function getLastRegistrationTime(callback) {
    chrome.storage.local.get("lastRegistrationTime", function (result) {
        var lastRegistrationTime = result["lastRegistrationTime"];
        callback(lastRegistrationTime);
    });
}

function setLastRegistrationTime() {
    chrome.storage.local.set({
        lastRegistrationTime: (new Date()).getTime()
    });
}

function tryRegistration(tabId, changeInfo, tab) {

    if (!(tab.url.match(/.*#openModal$/))) {
        if (checkLaunched == false) {
            window.setTimeout(function () {
                checkAuthStatus(tabId)
            }, 1000);
        } else {
            console.log("checkAuthStatus already scheduled");
        }

    } else {
        console.log("We are already in openModal");
    }

    setLastRegistrationURL(tab.url);
    setLastRegistrationTime();
}


chrome.tabs.onUpdated.addListener(function (tabId, changeInfo, tab) {
    if (changeInfo.status == "complete" && tab.url && tab.url.match(URLRegexpNoPresent)) {
        console.log("URL match. Checking auth status...");
        getLastRegistrationURL(function (lastRegistrationURL) {
            if (lastRegistrationURL != removeFragment(tab.url)) {
                tryRegistration(tabId, changeInfo, tab);
            } else {
                getLastRegistrationTime(function (lastRegistrationTime) {
                    if (!lastRegistrationTime || ((new Date()).getTime() - lastRegistrationTime > REGISTRATION_TIMEOUT)) {
                        tryRegistration(tabId, changeInfo, tab);
                    }
                });
            }
        });
    } else {
        chrome.pageAction.hide(tabId);
    }
});


chrome.runtime.onMessage.addListener(function (request, sender, sendResponse) {
    console.log(sender.tab ?
        "from a content script:" + sender.tab.url :
        "from the extension");
    if (request.greeting == "authorize") {
        var tabId = null;
        if (sender.tab) {
            interactiveRequestAuthToken(sender.tab.id);
        } else {
            chrome.tabs.query({
                    currentWindow: true,
                    active: true
                },
                function (tabArray) {
                    interactiveRequestAuthToken(tabArray[0].id);
                });
        }
    }

});


chrome.gcm.onMessage.addListener(function (message) {
    console.log("Message received: '" + message + "'");
    chrome.tabs.getSelected(null, function (tab) {
        if (tab.url.match(URLRegexp)) {
            if (message.data) {
                if (message.data.message) {
                    if ("NEXT" == message.data.message) {
                        chrome.tabs.executeScript({
                            file: "slide_switcher.js"
                        });
                    } else if ("PREV" == message.data.message) {
                        chrome.tabs.executeScript({
                            file: "slide_switcher_backwards.js"
                        });
                    } else {
                        console.log("Unknown message received. data.message: " + message.data.message);
                    }
                } else {
                    console.log("Unknown message received. data: " + message.data);
                }
            } else {
                console.log("Unknown message received: " + message);
            }


        }
    });
});




//function init() {
//      var apiName = 'registration'
//      var apiVersion = 'v1'
//      var apiRoot = 'https://watchpresenterpublic.appspot.com/_ah/api';
//      var callback = function() {
//          
//          
//          chrome.identity.getAuthToken({ 'interactive': true }, function(token) {
//              console.log("Token retrieved: " + token);
//              gapi.auth.setToken(token);
//              afterAPIUp();
//           });
//      }
//      gapi.client.load(apiName, apiVersion, callback, apiRoot);
//    }


//var head = document.getElementsByTagName('head')[0];
//var script = document.createElement('script');
//script.type = 'text/javascript';
//script.src = "https://apis.google.com/js/client.js?onload=init";
//head.appendChild(script);







function loadScript(url) {
    var request = new XMLHttpRequest();

    request.onreadystatechange = function () {
        if (request.readyState !== 4) {
            return;
        }

        if (request.status !== 200) {
            return;
        }

        eval(request.responseText);
    };

    request.open('GET', url);
    request.send();
}

function authorize() {
    console.log("on authorize()");
    gapi.auth.authorize({
            client_id: '736639150268-d4i5u6msvjai5okftb50s3tcaqu9booi.apps.googleusercontent.com',
            immediate: true,
            scope: 'https://www.googleapis.com/auth/userinfo.email'
        },
        function (token) {
            if (token.access_token && !token.error) {
                gapi.client.load('registration', 'v1', afterAPIUp, 'https://watchpresenter.appspot.com/_ah/api');
            } else {
                chrome.storage.local.set({
                    registered: false
                });
            }

        }
    );
}

if (apiUp) {
    //load Google's javascript client libraries
    window.gapi_onload = authorize;
    loadScript('https://apis.google.com/js/client.js');
}
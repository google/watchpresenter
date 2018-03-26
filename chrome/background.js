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


var URLRegexp = /http[s]:\/\/docs\.google\.com.*\/presentation/i;
var URLRegexpNoPresent = /http[s]:\/\/docs\.google\.com.*\/presentation(?!.*present\?)/i;
var checkLaunched = false;
var modalRegistrationTabId;
var MIN_SLIDE_PASS_PERIOD = 500;


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
    setRegistrationId(registrationId)
        // Send the registration ID to your application server.
    sendRegistrationId(registrationId, onSendregistrationId);
}


function onSendregistrationId(succeed) {
    // Once the registration ID is received by your server,
    // set the flag such that register will not be invoked
    // next time when the app starts up.
    if (succeed) {
        console.log("Registration successful (" + succeed + "). Saving to storage...");
        setLastRegistrationVersion();
    }
    releaseModal(succeed);
}


function sendRegistrationId(regId, callback) {
    console.log("Sending registration ID to server");
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
    getLastRegistrationVersion(function (lastRegistrationVersion) {
        if (lastRegistrationVersion != getCurrentVersion()) {
            console.log("Not registered yet. Registering...");
            var senderIds = ["122248338560"];
            chrome.gcm.register(senderIds, registerCallback);
        } else {
            console.log("Already registered");
            getRegistrationId(function (registrationId) {
                sendRegistrationId(registrationId, onSendregistrationId);
            });
        }
    });
}


function loadGoogleAPI() {
    //load Google's javascript client libraries
    window.gapi_onload = authorize;
    loadScript('https://apis.google.com/js/client.js');
}


function requestAuthTokenInteractive(tabId) {
    console.log("Requesting auth token interactively");
    //oauth2 auth
    chrome.tabs.insertCSS(tabId, {
        file: "watchpresenter.css"
    });
    chrome.tabs.executeScript(tabId, {
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


function checkAuthStatus(tab, tryInteractive) {
    chrome.pageAction.show(tab.id);


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
            } else if (tryInteractive) {
                console.log("Valid token not found");
                chrome.storage.local.set({
                    registered: false
                });
                requestAuthTokenInteractive(tab.id);
            }
        }
    );
}

function getLastRegistrationVersion(callback) {
    chrome.storage.local.get("lastRegistrationVersion", function (result) {
        var lastRegistrationVersion = result["lastRegistrationVersion"];
        callback(lastRegistrationVersion);
    });
}

function setLastRegistrationVersion() {
    chrome.storage.local.set({
        lastRegistrationVersion: getCurrentVersion()
    });
    chrome.storage.local.set({
        registered: true
    });
}

function getRegistrationId(callback) {
    chrome.storage.local.get("registrationId", function (result) {
        var registrationId = result["registrationId"];
        callback(registrationId);
    });
}

function setRegistrationId(registrationId) {
    chrome.storage.local.set({
        registrationId: registrationId
    });
}

function getCurrentVersion() {
    return chrome.app.getDetails().version;
}


function getLastSlidePassTime(callback) {
    chrome.storage.local.get("lastSlidePassTime", function (result) {
        var lastSlidePassTime = result["lastSlidePassTime"];
        callback(lastSlidePassTime);
    });
}

function setLastSlidePassTime() {
    chrome.storage.local.set({
        lastSlidePassTime: (new Date()).getTime()
    });
}

function tryRegistration(tab, tryInteractive) {
    console.log("Try registration. Interactive: " + tryInteractive);
    if (!(tab.url.match(/.*#openModal$/))) {
        if (checkLaunched == false) {
            window.setTimeout(function () {
                checkAuthStatus(tab, tryInteractive)
            }, 1000);
        } else {
            console.log("checkAuthStatus already scheduled");
        }

    } else {
        console.log("We are already in openModal");
    }

}


function onPresentationPage(tab) {
    if (tab.url && tab.url.match(URLRegexpNoPresent)) {
        console.log("URL match. Checking auth status...");
        tryRegistration(tab, true);
    } else {
        chrome.pageAction.hide(tab.id);
    }
}


chrome.gcm.onMessage.addListener(function (message) {
    console.log("Message received: '" + message + "'");
    chrome.tabs.getSelected(null, function (tab) {
        if (tab.url.match(URLRegexp)) {
            if (message.data) {
                if (message.data.message) {
                    if ("NEXT" == message.data.message) {
                        getLastSlidePassTime(function (lastTime) {
                            if (!lastTime || (lastTime - (new Date()).getTime()) > MIN_SLIDE_PASS_PERIOD) {
                                chrome.tabs.executeScript({
                                    file: "slide_switcher.js"
                                });
                            }
                        });

                    } else if ("PREV" == message.data.message) {
                        getLastSlidePassTime(function (lastTime) {
                            if (!lastTime || (lastTime - (new Date()).getTime()) > MIN_SLIDE_PASS_PERIOD) {
                                chrome.tabs.executeScript({
                                    file: "slide_switcher_backwards.js"
                                });
                            }
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
    console.log("authorizing...");
    chrome.identity.getAuthToken({
            interactive: true 
        },
        function (token) {
            console.log("on authorize callback. token: " + token);
            if (!token.error) {
                console.log("Setting access token...");
                gapi.client.setToken({access_token: token});
                console.log("Loading API...");
                gapi.client.load('registration', 'v1', afterAPIUp, 'https://watchpresenter.appspot.com/_ah/api');
            } else {
                console.log("Invalid token");
                chrome.storage.local.set({
                    registered: false
                });
            }

        }
    );
}


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


chrome.extension.onRequest.addListener(function (request, sender) {
    if (request == "onPresentationPage") {
        onPresentationPage(sender.tab);
    }
});

console.log("Listeners added");

function install_notice() {
    chrome.storage.local.get("registered", function (result) {
        if (result["registered"] == true) {
            console.log("Already registered. Not showing welcome message");
        return;
        } else {
            chrome.tabs.create({url: "welcome.html"});
        }
    })
}
install_notice();
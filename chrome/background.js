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

console.log("WatchPresenter background");

chrome.extension.onRequest.addListener(function (request, sender) {
    if (request == "show_page_action") {
        chrome.pageAction.show(sender.tab.id);
    } else if (request == "hide_page_action") {
        chrome.pageAction.hide(sender.tab.id);
    }
});
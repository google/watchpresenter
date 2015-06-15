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

var loadingModal;

document.addEventListener('DOMContentLoaded', function () {
    updateUI();

    loadingModal = document.getElementById("watchpresenterLoadingDialog");
    // Setup an event listener for the authorize button.
    var authorizeBtn = document.getElementById('authorizeButton');
    authorizeBtn.addEventListener('click', function (e) {
        e.preventDefault();
        loadingModal.showModal();
        chrome.runtime.sendMessage({
            greeting: "authorize"
        });
    });


});

chrome.runtime.onMessage.addListener(
    function (request, sender, sendResponse) {
        console.log(sender.tab ?
            "from a content script:" + sender.tab.url :
            "from the extension");
        if (request.greeting == "finishLoading") {
            if (loadingModal) {
                loadingModal.close();
            }
            updateUI();
        }
    });

function updateUI() {
    chrome.storage.local.get("registered", function (result) {
        if (result["registered"] == true) {
            console.log("registered");
            document.getElementById("authorizedText").style.display = 'inline-block';
            document.getElementById("notAuthorizedText").style.display = 'none';
        } else {
            console.log("not registered");
            document.getElementById("authorizedText").style.display = 'none';
            document.getElementById("notAuthorizedText").style.display = 'inline-block';
        }
    })
}
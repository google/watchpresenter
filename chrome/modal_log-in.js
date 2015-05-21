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

var authorizeModal = document.getElementById("watchpresenterAuthorizeDialog");
var loadingModal = document.getElementById("watchpresenterLoadingDialog");
var successDialog = document.getElementById("watchpresenterAuthSuccessDialog");
var failureDialog = document.getElementById("watchpresenterAuthFailureDialog");
if (!authorizeModal) {
    document.getElementsByTagName("body")[0].insertAdjacentHTML('beforeEnd', '<dialog class="watchpresenter" id="watchpresenterAuthorizeDialog"><div><h1 class="authorizeTitle">Log in needed</h1><p>In order to start using Watchpresenter you first need to log-in</p><p>Watchpresenter uses your Chrome account for log in purposes.</p><a id="watchPresenterAuthorizeDialogDismissButton" class="btn cancelButton">Cancel</a><a id="watchPresenterAuthorizeDialogAuthorizeButton" class="btn">Log in</a></div></dialog>')
    document.getElementsByTagName("body")[0].insertAdjacentHTML('beforeEnd', '<dialog class="watchpresenter" id="watchpresenterLoadingDialog"><img src="chrome-extension://hafndepdmjlnoeieonianffejnpfninb/images/loading.gif" alt="Loading..."></img></dialog>')
    document.getElementsByTagName("body")[0].insertAdjacentHTML('beforeEnd', '<dialog class="watchpresenter" id="watchpresenterAuthSuccessDialog"><div><p>Log in was successful</p><p>You can start using Watchpresenter now!</p><a id="watchPresenterAuthSuccessDialogOKButton" class="btn">OK</a></div></dialog>')
    document.getElementsByTagName("body")[0].insertAdjacentHTML('beforeEnd', '<dialog class="watchpresenter" id="watchpresenterAuthFailureDialog"><div><p>Log in failed. Please try again</p><p>You will not be able to use Watchpresenter until you log in</p><a id="watchPresenterAuthFailureDialogOKButton" class="btn">OK</a></div></dialog>')

    authorizeModal = document.getElementById("watchpresenterAuthorizeDialog");
    loadingModal = document.getElementById("watchpresenterLoadingDialog");
    successDialog = document.getElementById("watchpresenterAuthSuccessDialog");
    failureDialog = document.getElementById("watchpresenterAuthFailureDialog");
    var authorizeBtn = document.getElementById('watchPresenterAuthorizeDialogAuthorizeButton');
    var successOKBtn = document.getElementById('watchPresenterAuthSuccessDialogOKButton');
    var failureOKBtn = document.getElementById('watchPresenterAuthFailureDialogOKButton');

    // Setup an event listener for the authorize button.
    authorizeBtn.addEventListener('click', function (e) {
        e.preventDefault();

        chrome.runtime.sendMessage({
            greeting: "authorize"
        }, function (response) {
            loadingModal.close()
        });
        authorizeModal.close();
        loadingModal.showModal();
    });
    var dismissBtn = document.getElementById('watchPresenterAuthorizeDialogDismissButton');

    // Setup an event listener for the close button.
    dismissBtn.addEventListener('click', function (e) {
        e.preventDefault();

        authorizeModal.close();
    });

    successOKBtn.addEventListener('click', function (e) {
        e.preventDefault();

        successDialog.close();
    });

    failureOKBtn.addEventListener('click', function (e) {
        e.preventDefault();

        failureDialog.close();
    });
}
authorizeModal.showModal();

chrome.runtime.onMessage.addListener(
    function (request, sender, sendResponse) {
        console.log(sender.tab ?
            "from a content script:" + sender.tab.url :
            "from the extension");
        if (request.greeting == "finishLoading") {
            loadingModal.close();
            if (request.success == true) {
                successDialog.showModal();
            } else {
                failureDialog.showModal();
            }
        }
    });
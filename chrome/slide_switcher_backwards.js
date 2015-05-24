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

function eventFire(el, etype) {
    if (el.fireEvent) {
        (el.fireEvent('on' + etype));
    } else {
        var evObj = document.createEvent('Events');
        evObj.initEvent(etype, true, false);
        el.dispatchEvent(evObj);
    }
}


function clickOnElement(el, doc) {
    eventFire(el, "mouseover", doc);
    eventFire(el, "focus", doc);
    eventFire(el, "mousemove", doc);
    eventFire(el, "mousedown", doc);
    eventFire(el, "mouseup", doc);
    var evt = document.createEvent("Events");
    evt.initEvent('mousewheel', true, false);
    evt.wheelDelta = -120;
    el.dispatchEvent(evt);
}

var elementToClickOn = document.evaluate("//*[@class=\"punch-viewer-container\"]", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
var documentToClickOn = document;
if (elementToClickOn) {

} else {
    documentToClickOn = document.evaluate("//*[@class=\"punch-present-iframe\"]", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.contentDocument;

    elementToClickOn = documentToClickOn.evaluate("//*[@class=\"punch-viewer-container\"]", documentToClickOn, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
}

clickOnElement(elementToClickOn, documentToClickOn);

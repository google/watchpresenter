/*
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zuluindia.watchpresenter.backend;

/**
 * This class wraps a message to be sent to the mobile app.
 *
 * It is intended to allow for delivery of warning messages from the server to the mobile
 * app. (e.g.: app update warnings).
 *
 * Created by pablogil on 2/12/15.
 */
public class VersionMessage {

    //It would be nice to use enum and somehow share class definition with the client
    public static final String ACTION_NOTHING = "ACTION_NOTHING";
    public static final String ACTION_RECOMMEND_UPGRADE = "ACTION_RECOMMEND_UPGRADE";
    public static final String ACTION_FORCE_UPGRADE = "ACTION_FORCE_UPGRADE";
    public static final String ACTION_BLOCK = "ACTION_BLOCK";
    public static final String ACTION_SHOW_MESSAGE = "ACTION_SHOW_MESSAGE";

    private String action;
    private String message;
    private String url;

    public VersionMessage(String action, String message, String url){
        this.action = action;
        this.message = message;
        this.url = url;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

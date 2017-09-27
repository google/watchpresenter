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

package com.zuluindia.watchpresenter.common;

/**
 * Created by pablogil on 2/10/15.
 */
public class Constants {

    public static final String PREF_ACCOUNT_NAME = "accountName";
    public static final String PREF_LAST_TUTORIAL_SHOWN = "lastTutorial";
    public static final String PREF_LAST_UPDATES_SHOWN = "lastUpdatesShown";
    public static final String PREF_REGISTERED = "registered";
    public static final String PREF_VIBRATION = "vibrationEnabled";
    public static final String ANDROID_AUDIENCE = "736639150268-egocd3c5l2p2peh0r436fln5dv0ir1m8.apps.googleusercontent.com";
    public static final String LOG_TAG = "WatchPresenter";
    public static final String NEXT_SLIDE_MESSAGE = "NEXT";
    public static final String PREV_SLIDE_MESSAGE = "PREV";
    public static final String WARMUP_MESSAGE = "WARMUP";
    public static final String SETTINGS_NAME = "Watchpresenter";

    public static final String SERVER_URL = "https://testing-dot-watchpresenter.appspot.com/_ah/api/";

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    public static class VersionMessageActions{
        public static final String ACTION_NOTHING = "ACTION_NOTHING";
        public static final String ACTION_RECOMMEND_UPGRADE = "ACTION_RECOMMEND_UPGRADE";
        public static final String ACTION_FORCE_UPGRADE = "ACTION_FORCE_UPGRADE";
        public static final String ACTION_BLOCK = "ACTION_BLOCK";
        public static final String ACTION_SHOW_MESSAGE = "ACTION_SHOW_MESSAGE";
    }

    public static final String START_GESTURE_SERVICE_PATH = "start/GestureService";
    public static final String STOP_GESTURE_SERVICE_PATH = "stop/GestureService";

    public static final String NEXT_SLIDE_GESTURE_DETECTED_PATH = "nextSlide/GestureService";
}

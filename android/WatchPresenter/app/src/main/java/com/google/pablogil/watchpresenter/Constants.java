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

package com.google.pablogil.watchpresenter;

/**
 * Created by pablogil on 2/10/15.
 */
public class Constants {
    public static final String PREF_ACCOUNT_NAME = "accountName";
    public static final String ANDROID_AUDIENCE = "1048948725539-nted56v5uo7li0b4aiet2kg8e5hge9g4.apps.googleusercontent.com";
    public static final String LOG_TAG = "WatchPresenter";
    public static final String NEXT_SLIDE_MESSAGE = "NEXT_SLIDE";
    public static final String PREV_SLIDE_MESSAGE = "PREV_SLIDE";
    public static final String KEEP_ALIVE_MESSAGE = "KEEP_ALIVE";

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    public static class VersionMessageActions{
        public static final String ACTION_NOTHING = "ACTION_NOTHING";
        public static final String ACTION_RECOMMEND_UPGRADE = "ACTION_RECOMMEND_UPGRADE";
        public static final String ACTION_FORCE_UPGRADE = "ACTION_FORCE_UPGRADE";
        public static final String ACTION_BLOCK = "ACTION_BLOCK";
        public static final String ACTION_SHOW_MESSAGE = "ACTION_SHOW_MESSAGE";
    }
}

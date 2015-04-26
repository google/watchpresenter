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

package com.example.pablogil.myapplication.backend;

/**
 * Created by pablogil on 2/10/15.
 */
public class Constants {
    public static final String WEB_CLIENT_ID = "1048948725539-nted56v5uo7li0b4aiet2kg8e5hge9g4.apps.googleusercontent.com";
    public static final String ANDROID_DEBUG_CLIENT_ID = "1048948725539-8i9mppb9kkrpqoiq63m8huihiu8kufev.apps.googleusercontent.com";
    public static final String ANDROID_CLIENT_ID = "1048948725539-ioo446ijrvarl01voq5kfpmci456vadv.apps.googleusercontent.com";
    public static final String CHROME_EXTENSION_ID = "1048948725539-2fvnptbb4vqh2dqsfgnm32c2caqg32h8.apps.googleusercontent.com";
    public static final String ANDROID_AUDIENCE = WEB_CLIENT_ID;
    public static final String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";

    //Maybe there is a way to share these constants in a Google Cloud Endpoints-way
    public static final String NEXT_SLIDE_MESSAGE = "NEXT_SLIDE";
    public static final String KEEP_ALIVE_MESSAGE = "KEEP_ALIVE";
}

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

package com.zuluindia.watchpresenter.messaging;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.zuluindia.watchpresenter.backend.messaging.Messaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.zuluindia.watchpresenter.common.Constants;

/**
 * Created by pablogil on 2/12/15.
 */
public class MessagingService {

    private static Messaging messagingService;

    public static Messaging get(Context context){
        if (messagingService == null) {
            SharedPreferences settings = context.getSharedPreferences(
                    "Watchpresenter", Context.MODE_PRIVATE);
            final String accountName = settings.getString(Constants.PREF_ACCOUNT_NAME, null);
            if(accountName == null){
                Log.i(Constants.LOG_TAG, "Cannot send message. No account name found");
            }
            else {
                GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(context,
                        "server:client_id:" + Constants.ANDROID_AUDIENCE);
                credential.setSelectedAccountName(accountName);
                Messaging.Builder builder = new Messaging.Builder(AndroidHttp.newCompatibleTransport(),
                        new GsonFactory(), credential)
                        .setRootUrl(Constants.SERVER_URL);

                messagingService = builder.build();
            }
        }
        return messagingService;
    }

    public static void reset(){
        messagingService = null;
    }

}

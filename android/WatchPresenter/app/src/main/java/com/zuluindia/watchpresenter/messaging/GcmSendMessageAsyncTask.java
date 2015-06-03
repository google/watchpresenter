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

/**
 * Created by pablogil on 1/16/15.
 */

import android.os.AsyncTask;
import android.util.Log;

import com.zuluindia.watchpresenter.backend.messaging.Messaging;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.zuluindia.watchpresenter.common.Constants;

import java.io.IOException;

public class GcmSendMessageAsyncTask extends AsyncTask<String, Void, String> {
    private Messaging messagingService = null;


    public GcmSendMessageAsyncTask(Messaging messagingService) {
        this.messagingService = messagingService;
    }

    @Override
    protected String doInBackground(String... messages) {
        if(messages.length != 1){
            throw new IllegalArgumentException("You must provide one and only one message " +
                    "to send. You provided " + messages.length);
        }
        String msg = messages[0];
        if(messagingService != null) {

            try {
                Log.d(Constants.LOG_TAG, "Sending message...");
                messagingService.sendMessage(msg).execute();

            } catch (IOException ex) {
                ex.printStackTrace();
                msg = "Error: " + ex.getMessage();
            }
        }
        else{
            Log.e(Constants.LOG_TAG, "Cannot send message, no MessagingService available");
            msg = "Not sent";
        }
        return msg;
    }


    @Override
    protected void onPostExecute(String msg) {
        Log.d(Constants.LOG_TAG, "Message sent: '" + msg + "'");
    }
}
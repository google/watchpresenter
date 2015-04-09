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

package com.google.pablogil.watchpresenter.messaging;

/**
 * Created by pablogil on 1/16/15.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.pablogil.myapplication.backend.messaging.Messaging;
import com.example.pablogil.myapplication.backend.registration.Registration;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GcmSendMessageAsyncTask extends AsyncTask<String, Void, String> {
    private Messaging messagingService = null;
    private GoogleCloudMessaging gcm;
    private GoogleAccountCredential credential;
    private static final String LOG_TAG = "SendMessageTask";


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
                messagingService.sendMessage(msg).execute();

            } catch (IOException ex) {
                ex.printStackTrace();
                msg = "Error: " + ex.getMessage();
            }
        }
        else{
            Log.e(LOG_TAG, "Cannot send message, no MessagingService available");
            msg = "Not sent";
        }
        return msg;
    }


    @Override
    protected void onPostExecute(String msg) {
        Log.d(LOG_TAG, "Message sent: '" + msg + "'");
    }
}
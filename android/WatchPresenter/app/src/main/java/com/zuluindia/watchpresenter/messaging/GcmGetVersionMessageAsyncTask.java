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
import com.zuluindia.watchpresenter.backend.messaging.model.VersionMessage;
import com.zuluindia.watchpresenter.MainActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.zuluindia.watchpresenter.Constants;

import java.io.IOException;

public class GcmGetVersionMessageAsyncTask extends AsyncTask<Integer, Void, VersionMessage> {
    private Messaging messagingService = null;
    private GoogleCloudMessaging gcm;
    private GoogleAccountCredential credential;
    private static final String LOG_TAG = "GetVersionMessage";
    private MainActivity mainActivity;


    public GcmGetVersionMessageAsyncTask(Messaging messagingService, MainActivity mainActivity) {
        this.messagingService = messagingService;
        this.mainActivity = mainActivity;
    }

    @Override
    protected VersionMessage doInBackground(Integer... versionNumbers) {

        int versionNumber = versionNumbers[0];
        VersionMessage versionMessage = null;
        if(messagingService != null) {

            try {
                versionMessage = messagingService.getMessageForVersion(versionNumber).execute();

            } catch (IOException ex) {
                Log.e(LOG_TAG, "Could not retrieve version message", ex);
            }
        }
        else{
            Log.e(LOG_TAG, "Cannot retrieve version message, no MessagingService available");
        }
        return versionMessage;
    }


    @Override
    protected void onPostExecute(VersionMessage msg) {
        if(msg != null) {
            if (Constants.VersionMessageActions.ACTION_NOTHING.equals(msg.getAction()) == false) {
                mainActivity.showSuggestUpdateDialog(msg);
            } else {
                Log.v(LOG_TAG, "No version message to shown to the user");
            }
        }
    }
}
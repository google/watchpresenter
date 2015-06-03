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

import com.zuluindia.watchpresenter.common.Constants;
import com.zuluindia.watchpresenter.MainActivity;
import com.zuluindia.watchpresenter.backend.messaging.Messaging;
import com.zuluindia.watchpresenter.backend.messaging.model.RegisteredResponse;

import java.io.IOException;

public class GcmCheckRegistrationAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private Messaging messagingService = null;
    private MainActivity mainActivity;


    public GcmCheckRegistrationAsyncTask(Messaging messagingService, MainActivity mainActivity) {
        this.messagingService = messagingService;
        this.mainActivity = mainActivity;
    }

    @Override
    protected Boolean doInBackground(Void... nothing) {

        RegisteredResponse response = null;
        boolean result = false;
        if(messagingService != null) {

            try {
                response = messagingService.checkRegistration().execute();
                Log.d(Constants.LOG_TAG, "Registration detected: " + response.getRegistered());
            } catch (IOException ex) {
                Log.e(Constants.LOG_TAG, "Could not check registration", ex);
            }
        }
        else{
            Log.e(Constants.LOG_TAG, "Could not check registration, no MessagingService available");
        }

        if(response != null && response.getRegistered()){
            result = true;
        }

        return result;
    }


    @Override
    protected void onPostExecute(Boolean registered) {
        mainActivity.registrationUpdate(registered);
    }
}
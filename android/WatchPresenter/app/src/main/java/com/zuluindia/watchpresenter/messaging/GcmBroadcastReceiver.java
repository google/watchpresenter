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
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.zuluindia.watchpresenter.Constants;
import com.zuluindia.watchpresenter.MainActivity;


/**
 * Created by pablogil on 1/16/15.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Constants.LOG_TAG, "GCM message received");
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String messageType = gcm.getMessageType(intent);
        Bundle extras = intent.getExtras();
        if (extras != null && !extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Intent i = new Intent(MainActivity.ACTION_REGISTRATION_UPDATE);
                final String message = extras.getString("message");
                Log.d(Constants.LOG_TAG, "New registration message received: " + message);
                boolean registered = Constants.MESSAGE_REGISTERED.equals(message);
                i.putExtra(MainActivity.EXTRA_NEW_REGISTRATION_VALUE, registered);
                context.sendBroadcast(i);
            }
        }

    }
}

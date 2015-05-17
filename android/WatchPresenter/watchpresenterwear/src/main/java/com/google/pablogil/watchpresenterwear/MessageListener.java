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

package com.google.pablogil.watchpresenterwear;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import static com.google.pablogil.mloggerwear.Constants.LOG_EVENT_PATH;
import static com.google.pablogil.mloggerwear.Constants.START_SERVICE_PATH;
import static com.google.pablogil.mloggerwear.Constants.STOP_SERVICE_PATH;


/**
 * Created by pablogil on 4/13/15.
 */
public class MessageListener extends WearableListenerService {

    private static final String TAG = "MLogger";


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onDataChanged: " + messageEvent);
        }

        final String messagePath = messageEvent.getPath();
        if(messagePath != null){
            if(messagePath.equals(START_SERVICE_PATH)){
                Log.d(TAG, "Starting service...");
                Intent intent = new Intent(this, LoggerService.class);
                intent.putExtra(LoggerService.EXTRA_COMMAND, LoggerService.EXTRA_START);
                startService(intent);
            }
            else if(messagePath.equals(STOP_SERVICE_PATH)){
                Log.d(TAG, "Stopping service...");
                Intent intent = new Intent(this, LoggerService.class);
                stopService(intent);
            }
            else if(messagePath.equals(LOG_EVENT_PATH)){
                Log.d(TAG, "Logging event...");
                Intent intent = new Intent(this, LoggerService.class);
                intent.putExtra(LoggerService.EXTRA_COMMAND, LoggerService.EXTRA_LOG_EVENT);
                startService(intent);
            }
            else{
                Log.i(TAG, "Received message with unknown path: " + messagePath);
            }
        }
        else{
            Log.e(TAG, "Message with null path: " + messageEvent);
        }
    }
}
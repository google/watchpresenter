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

package com.zuluindia.watchpresenter.wear;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.zuluindia.watchpresenter.common.Constants;


/**
 * Created by pablogil on 4/13/15.
 */
public class MessageListener extends WearableListenerService {


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (Log.isLoggable(Constants.LOG_TAG, Log.DEBUG)) {
            Log.d(Constants.LOG_TAG, "onDataChanged: " + messageEvent);
        }

        final String messagePath = messageEvent.getPath();
        if(messagePath != null){
            if(messagePath.equals(Constants.START_GESTURE_SERVICE_PATH)){
                Log.d(Constants.LOG_TAG, "Starting service...");
                Intent intent = new Intent(this, GestureService.class);
                intent.putExtra(GestureService.EXTRA_COMMAND, GestureService.EXTRA_START);
                startService(intent);
            }
            else if(messagePath.equals(Constants.STOP_GESTURE_SERVICE_PATH)){
                Log.d(Constants.LOG_TAG, "Stopping service...");
                Intent intent = new Intent(this, GestureService.class);
                stopService(intent);
            }
            else{
                Log.i(Constants.LOG_TAG, "Received message with unknown path: " + messagePath);
            }
        }
        else{
            Log.e(Constants.LOG_TAG, "Message with null path: " + messageEvent);
        }
    }
}
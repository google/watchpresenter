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

package com.example.pablogil.watchpresenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by pablogil on 1/4/15.
 */
public class VolumeKeysReceiver extends BroadcastReceiver {

    private static final long DUPLICATE_TIME = 200;

    private static long lastEvent = 0;
    private static int lastVolume = -1; //By initializing to a negative value we
    //are assuming that the first event ever is a next slide event

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent("com.example.pablogil.watchpresenter.SEND_MESSAGE");
        final long currentEvent = System.currentTimeMillis();
        if(currentEvent - lastEvent > 200) {
            int newVolume =
                    (Integer)intent.getExtras().get("android.media.EXTRA_VOLUME_STREAM_VALUE");
            final String message = (newVolume > lastVolume)?
                    Constants.NEXT_SLIDE_MESSAGE: Constants.PREV_SLIDE_MESSAGE;
            lastVolume = newVolume;
            i.putExtra(Constants.EXTRA_MESSAGE, message);
            context.sendBroadcast(i);
        }
        else{
            Log.d(Constants.LOG_TAG, "Duplicate volume event discarded");
        }
        lastEvent = currentEvent;
    }
}

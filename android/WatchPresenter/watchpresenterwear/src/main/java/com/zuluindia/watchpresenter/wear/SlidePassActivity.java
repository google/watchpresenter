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

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.TextView;

import com.zuluindia.watchpresenter.R;
import com.zuluindia.watchpresenter.common.Constants;
import com.zuluindia.watchpresenter.common.WearMessenger;

public class SlidePassActivity extends WearableActivity {

    private TextView mTextView;
    private WearMessenger wearMessenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wearMessenger = new WearMessenger(this);
        setContentView(R.layout.activity_slide_pass);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
        setAmbientEnabled();
    }


    public void nextSlide(View v){
        sendMessageInBackground(Constants.NEXT_SLIDE_GESTURE_DETECTED_PATH, new long[]{0, 600});

    }

    public void prevSlide(View v){
        sendMessageInBackground(Constants.PREV_SLIDE_GESTURE_DETECTED_PATH, new long[]{0, 100, 50, 100, 50, 100});
    }

    private void sendMessageInBackground(final String message, final long[] vibrationPattern){
        (new Thread(){
            @Override
            public void run() {
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                //-1 - don't repeat
                final int indexInPatternToRepeat = -1;
                vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
                wearMessenger.sendToAll(message);
            }
        }).start();
    }
}

/*
 * Copyright (c) 2022. Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.example.watchpresenter;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

public class MonitorVolumeKeyPress extends Service {

    private static final String TAG = "MonitorVolumeKeyPress";
    private static final String ACTION_VOLUME_KEY_PRESS = "android.media.VOLUME_CHANGED_ACTION";
    private MediaPlayer objPlayer;
    private AudioManager audioManager;

    public void onCreate(){
        super.onCreate();
        Log.d(TAG, "Service Started!");
        objPlayer = MediaPlayer.create(this, com.example.watchpresenter.R.raw.silence);
//        objPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        objPlayer.setLooping(true);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    private BroadcastReceiver volumeKeysReceiver = new BroadcastReceiver() {

        private long lastEvent = 0;

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive");
        }
    };

    public MonitorVolumeKeyPress() {
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        startMonitoring();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE);

        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("1", name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        Notification notification =
                new Notification.Builder(this, "1")
                        .setContentTitle(getText(R.string.notification_title))
                        .setContentText(getText(R.string.notification_message))
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentIntent(pendingIntent)
                        .setTicker(getText(R.string.ticker_text))
                        .build();

// Notification ID cannot be 0.
        startForeground(1, notification);
        return Service.START_STICKY;
    }

    private void startMonitoring(){
        objPlayer.start();
        registerReceiver(volumeKeysReceiver, new IntentFilter(ACTION_VOLUME_KEY_PRESS));
        Log.i(TAG, "Media Player started!");
        if(objPlayer.isLooping() != true){
            Log.w(TAG, "Problem in Playing Audio");
        }
    }

    @Override
    public void onDestroy(){
        Log.d(TAG, "destroying KeyPress monitor service");
        stopMonitoring();
    }

    private void stopMonitoring(){
        objPlayer.stop();
        objPlayer.release();
        unregisterReceiver(volumeKeysReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
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

package com.google.pablogil.watchpresenter;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This service's purpose is to play an inaudible audio in teh background.
 *
 * That way, we can detect volume key's presses with a Receiver.
 */
public class PlayAudio extends Service{
    private static final String LOGCAT = null;
    MediaPlayer objPlayer;
    WifiManager.WifiLock wifiLock = null;
    private ScheduledExecutorService scheduler;

    public void onCreate(){
        super.onCreate();
        Log.d(LOGCAT, "Service Started!");
        objPlayer = MediaPlayer.create(this,R.raw.silence);
        objPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        objPlayer.setLooping(true);
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "WatchPresenterLock");
        wifiLock.acquire();
        scheduler =
                Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        Intent i = new Intent("com.google.pablogil.watchpresenter.SEND_MESSAGE");
                        i.putExtra(Constants.EXTRA_MESSAGE, Constants.KEEP_ALIVE_MESSAGE);
                        sendBroadcast(i);
                    }
                }, 0, 2, TimeUnit.MINUTES);
        objPlayer.start();
        Log.d(LOGCAT, "Media Player started!");
        if(objPlayer.isLooping() != true){
            Log.d(LOGCAT, "Problem in Playing Audio");
        }
        return 1;
    }

    public void onStop(){
        objPlayer.stop();
        wifiLock.release();
        objPlayer.release();
        scheduler.shutdown();
    }

    public void onPause(){
        objPlayer.stop();
        wifiLock.release();
        objPlayer.release();
        scheduler.shutdown();
    }
    public void onDestroy(){
        objPlayer.stop();
        wifiLock.release();
        objPlayer.release();
        scheduler.shutdown();
    }
    @Override
    public IBinder onBind(Intent objIndent) {
        return null;
    }
}
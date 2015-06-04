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

/**
 * Created by pablogil on 4/13/15.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;

import com.zuluindia.watchpresenter.common.Constants;
import com.zuluindia.watchpresenter.wear.gestureDetection.DataProcessor;
import com.zuluindia.watchpresenter.wear.gestureDetection.GestureDetector;
import com.zuluindia.watchpresenter.wear.gestureDetection.encog.EncogDataProcessor;
import com.zuluindia.watchpresenter.common.WearMessenger;

import java.io.IOException;
import java.io.PrintWriter;


public class GestureService extends Service {

    public static final String EXTRA_COMMAND = "COMMAND";
    public static final String EXTRA_START = "START";

    private static final long PERIOD = 50;
    private static final int WINDOW = 10;

    /* Do not send message if two gestures are detected within this time range */
    private static final long MIN_GESTURE_LAPSE = 500;

    private long lastGesture;

    public boolean running;

    private SensorManager sensorManager;

    private GestureDetector gestureDetector;
    private DataProcessor dataProcessor;

    private static abstract class SensorListener implements SensorEventListener{

        protected GestureDetector detector;

        public SensorListener(GestureDetector detector){

            this.detector = detector;
        }

        public void onAccuracyChanged(Sensor arg0, int arg1) {

        }

        public abstract void onSensorChanged(SensorEvent arg0);

    }

    private static class AccelListener extends SensorListener{


        public AccelListener(GestureDetector detector){
            super(detector);
        }


        @Override
        public void onSensorChanged(SensorEvent arg0) {
            detector.newAccel(arg0.values[0], arg0.values[1], arg0.values[2]);
        }

    }

    private static class GyroListener extends SensorListener{


        public GyroListener(GestureDetector detector){
            super(detector);
        }


        @Override
        public void onSensorChanged(SensorEvent arg0) {
            detector.newGyro(arg0.values[0], arg0.values[1], arg0.values[2]);
        }

    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }


    private PrintWriter accelWriter;
    //	private PrintWriter linearAccelWriter;

    private Sensor accelSensor;
    //	private Sensor linearAccelSensor;
    private Sensor gyroSensor;

    private SensorListener accelListener;
    private SensorListener gyroListener;

    private WearMessenger wearMessenger;

    PowerManager pm;
    PowerManager.WakeLock wl;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent == null || EXTRA_START.equals(intent.getStringExtra(EXTRA_COMMAND))){
            startDetection();
            wearMessenger = new WearMessenger(this);
        }
        return START_STICKY;
    }

    private synchronized void startDetection() {
        if (running == false) {
            Log.d(Constants.LOG_TAG, "Starting gesture detection service...");
            if (pm == null) {
                pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            }
            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Watchpresenter - gestures");
            wl.acquire();
            try {

                running = true;


                Log.d(Constants.LOG_TAG, "Loading Encog data processor");
                dataProcessor = new EncogDataProcessor(this);
                dataProcessor.load();
                Log.d(Constants.LOG_TAG, "Data processor loaded");
                gestureDetector = new GestureDetector(dataProcessor, new Runnable() {
                    @Override
                    public void run() {
                        onGestureDetected();
                    }
                },PERIOD,WINDOW);

                accelListener = new AccelListener(gestureDetector);
                gyroListener = new GyroListener(gestureDetector);

                //			SensorListener linearAccelListener = new SensorListener(linearAccelWriter);

                if (sensorManager == null) {
                    sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                    accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                }

                sensorManager.registerListener(accelListener, accelSensor, SensorManager.SENSOR_DELAY_GAME);
                if(gyroSensor != null){
                    sensorManager.registerListener(gyroListener, gyroSensor, SensorManager.SENSOR_DELAY_GAME);
                }
                gestureDetector.start();

            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "Exception while starting service", e);
            }
        }
        else{
            Log.w(Constants.LOG_TAG, "Service already logging. Skipped");
        }
    }



    private void vibrate(){
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] vibrationPattern = {0, 500, 50, 300};
        //-1 - don't repeat
        final int indexInPatternToRepeat = -1;
        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
    }

    @Override
    public void onDestroy() {
        wl.release();
        if(sensorManager != null && accelListener != null) {
            sensorManager.unregisterListener(accelListener);
        }
        if(sensorManager != null && gyroSensor != null){
            sensorManager.unregisterListener(gyroListener);
        }
        if(gestureDetector != null){
            gestureDetector.stop();
        }
        if(dataProcessor != null){
            dataProcessor.shutdown();
        }
        running = false;
        Log.d(Constants.LOG_TAG, "Gesture service is being destroyed.");
    }

    private void onGestureDetected(){
        final long currentTime = System.currentTimeMillis();
        if(currentTime - lastGesture > MIN_GESTURE_LAPSE) {
            lastGesture = currentTime;
            vibrate();
            wearMessenger.sendToAll(Constants.NEXT_SLIDE_GESTURE_DETECTED_PATH);
        }
        else{
            Log.d(Constants.LOG_TAG, "Gesture discarded due to gesture lapse time");
        }
    }

}
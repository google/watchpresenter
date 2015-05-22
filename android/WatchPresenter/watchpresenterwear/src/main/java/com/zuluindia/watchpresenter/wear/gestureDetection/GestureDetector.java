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

package com.zuluindia.watchpresenter.wear.gestureDetection;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by pablogil on 5/17/15.
 */
public class GestureDetector {


    private static final int ACCEL_INPUTS = 3;
    private static final int GYRO_INPUTS = 3;

    private static final int NO_OF_INPUTS = ACCEL_INPUTS + GYRO_INPUTS;

    private DataProcessor processor;
    private Runnable callback;
    private final long period;
    private final int window;
    private Timer timer;

    private double[] lastAccel;
    /* Indicates whether a new accel was received since last evaluation*/
    private boolean newAccel;

    private double[] lastGyro;
    /* Indicates whether a new accel was received since last evaluation*/
    private boolean newGyro;

    private Queue<Double> latestEntries;

    /**
     *
     * @param processor Evaluates whether there is a gesture or not
     * @param callback Method to invoke when gesture is detected
     * @param period Evaluation period
     * @param window Lookback window size
     */
    public GestureDetector(DataProcessor processor, Runnable callback, long period, int window){
        this.processor = processor;
        this.callback = callback;
        this.period = period;
        this.window = window;
        latestEntries = new LinkedList<>();
        lastAccel = new double[ACCEL_INPUTS];
        lastGyro = new double[GYRO_INPUTS];
    }

    /**
     * Start detecting gestures
     */
    public void start(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                double[] latestData = getLatestData();
                if(latestData != null){
                    if(processor.processData(latestData)){
                        callback.run();
                    }
                }
                else{
//                    Log.d(Constants.TAG, "Not enough data to process yet");
                }
            }
        }, period, period);
    }

    /**
     * Stop gesture detection
     */
    public void stop(){
        if(timer != null){
            timer.cancel();
        }
    }

    /**
     * Call this to pass new accel sensor data
     *
     * @param accelX
     * @param accelY
     * @param accelZ
     */
    public synchronized void newAccel(float accelX, float accelY, float accelZ){
        lastAccel[0] = accelX;
        lastAccel[1] = accelY;
        lastAccel[2] = accelZ;
        newAccel = true;
    }

    /**
     * Call this to pass new gyro data
     *
     * @param gyroX
     * @param gyroY
     * @param gyroZ
     */
    public synchronized void newGyro(float gyroX, float gyroY, float gyroZ){
        lastGyro[0] = gyroX;
        lastGyro[1] = gyroY;
        lastGyro[2] = gyroZ;
        newGyro = true;
    }


    /**
     *
     * @return null if there aren't enough readings yet
     */
    public synchronized double[] getLatestData(){
        double[] result = null;
        if(lastAccel != null && lastGyro != null) {
            if (newAccel == false) {
//            Log.w(Constants.TAG, "No new accel readings since last evaluation. Reusing latest one");
            }
            if (newGyro == false) {
//            Log.w(Constants.TAG, "No new gyro readings since last evaluation. Reusing latest one");
            }
            newAccel = false;
            newGyro = false;
            latestEntries.add(lastAccel[0]);
            latestEntries.add(lastAccel[1]);
            latestEntries.add(lastAccel[2]);
            latestEntries.add(lastGyro[0]);
            latestEntries.add(lastGyro[1]);
            latestEntries.add(lastGyro[2]);

            final int size = latestEntries.size();
            if (size >= window * NO_OF_INPUTS) {
                if(size > window * NO_OF_INPUTS){
                    for(int i=0;i<NO_OF_INPUTS;i++){
                        latestEntries.poll();
                    }
                }
                result = new double[NO_OF_INPUTS * window];
                Iterator<Double> iterator = latestEntries.iterator();
                for (int i = 0; i < result.length; i++) {
                    result[i] = iterator.next().doubleValue();
                }
            }
        }
        return result;
    }

}

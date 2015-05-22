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

package com.zuluindia.watchpresenter.wear.gestureDetection.encog;

import android.content.Context;


import com.zuluindia.watchpresenter.R;
import com.zuluindia.watchpresenter.wear.gestureDetection.DataProcessor;

import org.encog.Encog;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.NormalizationHelper;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by pablogil on 5/17/15.
 */
public class EncogDataProcessor implements DataProcessor {

    private Context context;
    MLRegression method;
    NormalizationHelper helper;

    public EncogDataProcessor(Context context){
        this.context = context;
    }


    @Override
    public boolean processData(double[] input) {
//        Log.d(Constants.LOG_TAG, "Processing data");
        String[] inputString = new String[input.length];
        for(int i=0;i<input.length;i++){
            inputString[i] = Double.toString(input[i]);
        }
        MLData inputData = helper.allocateInputVector();

        helper.normalizeInputVector(inputString,inputData.getData(),false);
//        Log.d(Constants.TAG, "About to compute");
        MLData outputData = method.compute(inputData);
//        Log.d(Constants.LOG_TAG, "Computing finished");
        final String output = helper.denormalizeOutputVectorToString(outputData)[0];
//        Log.d(Constants.TAG, "Input: " + Arrays.toString(inputString));
//        Log.d(Constants.TAG, "Output: " + output);
        return output.equals("1");
    }

    @Override
    public void load() throws IOException {
        ObjectInputStream ois = null;
        try{
            ois = new ObjectInputStream(new BufferedInputStream(
                    context.getResources().openRawResource(R.raw.model)));
            method = (MLRegression) ois.readObject();
            helper = (NormalizationHelper) ois.readObject();
        }
        catch (ClassNotFoundException e){
            throw new IOException("Could not read object from stream", e);
        }
        finally {
            if(ois != null){
                ois.close();
            }
        }
    }

    @Override
    public void shutdown() {
        if(method != null || helper != null) {
            Encog.getInstance().shutdown();
        }
    }
}

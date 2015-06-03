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

package com.zuluindia.watchpresenter.tutorial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.zuluindia.watchpresenter.R;

import org.codepond.wizardroid.WizardStep;

/**
 * Created by pablogil on 5/24/15.
 */
public class TutorialStep3 extends WizardStep {

    public TutorialStep3() {
    }
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tutorial_layout, container, false);
        WebView wv = (WebView) v.findViewById(R.id.mainWebView);
        wv.loadUrl("file:///android_asset/tutorial/tutorial3.html");

        return v;
    }

    @Override
    public void onExit(int exitCode) {
        super.onExit(exitCode);
    }
}

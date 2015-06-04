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

import org.codepond.wizardroid.WizardFlow;
import org.codepond.wizardroid.layouts.BasicWizardLayout;

/**
 * Created by pablogil on 5/24/15.
 */

public class TutorialWizard extends BasicWizardLayout {


    public TutorialWizard() {
        super();
    }

    @Override
    public WizardFlow onSetup() {

        return new WizardFlow.Builder()
                .addStep(TutorialStep1.class)
                .addStep(TutorialStep2.class)
                .addStep(TutorialStep3.class)
                .addStep(TutorialStep4.class)
                .addStep(TutorialStep5.class)
                .addStep(TutorialStep6.class)
                .create();
    }

    @Override
    public void onWizardComplete() {
        super.onWizardComplete();
        getActivity().finish();
    }
}
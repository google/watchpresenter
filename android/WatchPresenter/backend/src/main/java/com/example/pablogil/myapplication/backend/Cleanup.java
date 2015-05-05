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

/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.example.pablogil.myapplication.backend;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.log.LogService;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Named;

import static com.example.pablogil.myapplication.backend.OfyService.ofy;

/**
 * Messagint EndPoint
 *
 * This endpoint receives messages from the mobile app and delivers them to the
 * browser.
 */
@Api(name = "cleanup",
        version = "v1",
        namespace = @ApiNamespace(ownerDomain = "backend.myapplication.pablogil.example.com",
                ownerName = "backend.myapplication.pablogil.example.com",
                packagePath = ""),
    scopes = {com.example.pablogil.myapplication.backend.Constants.EMAIL_SCOPE},
    clientIds = {com.example.pablogil.myapplication.backend.Constants.ANDROID_DEBUG_CLIENT_ID,
            com.example.pablogil.myapplication.backend.Constants.ANDROID_DEBUG_CLIENT_ID_OLD,
            com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID,
            com.example.pablogil.myapplication.backend.Constants.WEB_CLIENT_ID,
            com.example.pablogil.myapplication.backend.Constants.ANDROID_CLIENT_ID,
            com.example.pablogil.myapplication.backend.Constants.ANDROID_CLIENT_ID_OLD
//            com.example.pablogil.myapplication.backend.Constants.CHROME_EXTENSION_ID
    },
    audiences = {com.example.pablogil.myapplication.backend.Constants.ANDROID_AUDIENCE})
public class Cleanup {
    private static final Logger log = Logger.getLogger(Cleanup.class.getName());

    private static final long EXPIRY_MILLISECONDS = 10000;


    /**
     *
     */
    @ApiMethod(name = "cleanup")
    public void sendMessage(User user) throws IOException, OAuthRequestException {
        if(user == null){
            throw new OAuthRequestException("Not authorized");
        }

        List<PresenterRecord> oldRecords = ofy().load().
                type(PresenterRecord.class).filter(
                "lastUpdate <", new Date(System.currentTimeMillis() - EXPIRY_MILLISECONDS)).list();
        log.info("Found " + oldRecords.size() + " old entries.");
        ofy().delete().entities(oldRecords).now();
    }

}

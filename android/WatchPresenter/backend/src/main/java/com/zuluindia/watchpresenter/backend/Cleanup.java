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

package com.zuluindia.watchpresenter.backend;

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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.zuluindia.watchpresenter.backend.OfyService.ofy;


/**
 * This class handles clean up of old database entries
 */
public class Cleanup extends HttpServlet{
    private static final Logger log = Logger.getLogger(Cleanup.class.getName());

    private static final long EXPIRY_MILLISECONDS = 63 * 24 * 3600 * 1000; //Cleaning entries older
    // than 63 days

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        try {
            cleanUp();
        } catch (OAuthRequestException e) {
            throw new ServletException("Could not perform cleanup", e);
        }
    }

    /**
     *
     */
    private void cleanUp() throws IOException, OAuthRequestException {

        List<PresenterRecord> oldRecords = ofy().load().
                type(PresenterRecord.class).filter(
                "lastUpdate <", new Date(System.currentTimeMillis() - EXPIRY_MILLISECONDS)).list();
        log.info("Found " + oldRecords.size() + " old entries.");
        ofy().delete().entities(oldRecords).now();
    }

}

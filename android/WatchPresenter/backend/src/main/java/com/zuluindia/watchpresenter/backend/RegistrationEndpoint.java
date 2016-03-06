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

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;

import java.util.logging.Logger;

import javax.inject.Named;

import static com.zuluindia.watchpresenter.backend.OfyService.ofy;

/**
 * Registration EndPoint
 *
 * This endpoint receives and processes registration messages from the browser.
 * Whenever a new browser is registered, the association between the browser's
 * registration ID and user ID (obtained via Endpoint authentication) is
 * persisted. That way, we can later send messages from a given user (securely authenticated
 * on the endpoint) to the appropriate browser(s).
 */
@Api(name = "registration",
        version = "v1",
        namespace = @ApiNamespace(ownerDomain = "backend.watchpresenter.zuluindia.com",
                ownerName = "backend.watchpresenter.zuluindia.com", packagePath = ""),
        scopes = {com.zuluindia.watchpresenter.backend.Constants.EMAIL_SCOPE},
        clientIds = {com.zuluindia.watchpresenter.backend.Constants.ANDROID_DEBUG_CLIENT_ID,
                com.zuluindia.watchpresenter.backend.Constants.ANDROID_CLIENT_ID,
                Constants.ANDROID_DEBUG_CLIENT_ID_PERSONAL,
                com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID,
                com.zuluindia.watchpresenter.backend.Constants.WEB_CLIENT_ID,
                Constants.CHROME_EXTENSION_ID},
        audiences = {com.zuluindia.watchpresenter.backend.Constants.ANDROID_AUDIENCE})
public class RegistrationEndpoint {

    private static final Logger log = Logger.getLogger(RegistrationEndpoint.class.getName());

    /**
     * Register a device to the backend
     *
     * @param regId The Google Cloud Messaging registration Id to add
     */
    @ApiMethod(name = "register")
    public void registerDevice(@Named("regId") String regId, User user) throws OAuthRequestException {
        if(user == null){
            throw new OAuthRequestException("Not authorized");
        }
        final String userId = PresenterRecord.getUserId(user.getEmail());
        log.info("Registration for userId: " + userId);
        PresenterRecord record = ofy().load().
                key(Key.create(PresenterRecord.class, userId)).now();
        if(record == null){
            log.info("Record not found for userId '" + userId + "'. Adding new record");
            record = new PresenterRecord();
            record.setUserId(userId);
        }
        if (record.getRegIds().contains(regId)) {
            log.info("Device " + regId + " already registered, skipping register");
        }
        else {
            record.addRegistrationId(regId);
        }
        record.updateTime();
        ofy().save().entity(record).now();
    }




}

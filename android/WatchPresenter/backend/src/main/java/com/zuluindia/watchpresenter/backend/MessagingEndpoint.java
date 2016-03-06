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
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Named;

import static com.zuluindia.watchpresenter.backend.OfyService.ofy;

/**
 * Messagint EndPoint
 *
 * This endpoint receives messages from the mobile app and delivers them to the
 * browser.
 */
@Api(name = "messaging",
        version = "v1",
        namespace = @ApiNamespace(ownerDomain = "backend.watchpresenter.zuluindia.com",
                ownerName = "backend.watchpresenter.zuluindia.com",
                packagePath = ""),
    scopes = {com.zuluindia.watchpresenter.backend.Constants.EMAIL_SCOPE},
    clientIds = {com.zuluindia.watchpresenter.backend.Constants.ANDROID_DEBUG_CLIENT_ID,
            com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID,
            com.zuluindia.watchpresenter.backend.Constants.WEB_CLIENT_ID,
            com.zuluindia.watchpresenter.backend.Constants.ANDROID_CLIENT_ID,
            com.zuluindia.watchpresenter.backend.Constants.ANDROID_DEBUG_CLIENT_ID_PERSONAL
    },
    audiences = {com.zuluindia.watchpresenter.backend.Constants.ANDROID_AUDIENCE})
public class MessagingEndpoint {
    private static final Logger log = Logger.getLogger(MessagingEndpoint.class.getName());

    /**
     * Api Keys can be obtained from the google cloud console
     */
    private static final String API_KEY = System.getProperty("gcm.api.key");

    /**
     *
     * @param message The message to send
     */
    @ApiMethod(name = "sendMessage")
    public void sendMessage(@Named("message") String message, User user) throws IOException, OAuthRequestException {
        if(user == null){
            throw new OAuthRequestException("Not authorized");
        }
        final String userId = PresenterRecord.getUserId(user.getEmail());
        if(com.zuluindia.watchpresenter.backend.Constants.KEEP_ALIVE_MESSAGE.equals(
                message)){
            log.fine("Keep alive from userId: " + userId);
            //This is just a keep-alive. Nothing to do here...
            return;
        }
        log.info("UserId: " + userId);
        if (message == null || message.trim().length() == 0) {
            log.warning("Not sending message because it is empty");
            return;
        }
        // crop longer messages
        if (message.length() > 1000) {
            message = message.substring(0, 1000) + "[...]";
        }
        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder().collapseKey("WatchPresenter").timeToLive(0).
                addData("message", message).build();
        PresenterRecord presenterRecord =
                ofy().load().key(Key.create(PresenterRecord.class,userId)).now();
        if(presenterRecord != null) {
            Iterator<String> regIdsIterator = presenterRecord.getRegIds().iterator();
            while (regIdsIterator.hasNext()) {
                String regId = regIdsIterator.next();
                Result result = sender.send(msg, regId, 5);
                if (result.getMessageId() != null) {
                    log.info("Message sent to " + regId);
                    String canonicalRegId = result.getCanonicalRegistrationId();
                    if (canonicalRegId != null) {
                        // if the regId changed, we have to update the datastore
                        log.info("Registration Id changed for " + regId + ". Updating to " + canonicalRegId);
                        regIdsIterator.remove();
                        HashSet<String> newSet = new HashSet<String>(presenterRecord.getRegIds());
                        newSet.add(canonicalRegId);
                        presenterRecord.setRegIds(newSet);
                        ofy().save().entity(presenterRecord).now();
                    }
                } else {
                    String error = result.getErrorCodeName();
                    if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                        log.warning("Registration Id " + regId + " no longer registered with GCM, removing from datastore");
                        // if the device is no longer registered with Gcm, remove it from the datastore
                        regIdsIterator.remove();
                        ofy().save().entity(presenterRecord).now();
                    } else {
                        log.warning("Error when sending message : " + error);
                    }
                }
            }
        }
        else{
            log.info("No presenters found for userId: '" + userId + "'");
        }
    }

    /**
     *
     * @param versionNumber Version number for which message should be retrieved
     */
    @ApiMethod(name = "getMessageForVersion")
    public VersionMessage getMessageForVersion(@Named("versionNumber")int versionNumber, User user)
            throws IOException, OAuthRequestException {
        if(user == null){
            throw new OAuthRequestException("Not authorized");
        }
        final String userId = PresenterRecord.getUserId(user.getEmail());
        if(log.isLoggable(Level.FINE)) {
            log.fine("Get message version for userId " +
                    userId + ". Version number: " + versionNumber);
        }
        VersionMessage message = new VersionMessage(
                VersionMessage.ACTION_NOTHING, "", "");
        return message;
    }


    /**
     * Check if the user Id has, at least, one registered device
     *
     */
    @ApiMethod(name = "checkRegistration")
    public RegisteredResponse checkRegistration(User user) throws OAuthRequestException {
        if(user == null){
            throw new OAuthRequestException("Not authorized");
        }
        RegisteredResponse result = new RegisteredResponse();
        final String userId = PresenterRecord.getUserId(user.getEmail());
        log.info("Checking for registration. userId: " + userId);
        PresenterRecord record = ofy().load().
                key(Key.create(PresenterRecord.class, userId)).now();
        if(record != null){
            result.setRegistered(true);
        }
        return result;
    }
}

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

package com.zuluindia.watchpresenter.backend;

import com.google.appengine.repackaged.org.apache.commons.codec.binary.Hex;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * The Objectify object model for device registrations we are persisting
 */
@Entity
@Cache
public class PresenterRecord {

    private Set<String> regIds;

    @Index
    private Date lastUpdate;
    private static final Logger log = Logger.getLogger(PresenterRecord.class.getCanonicalName());
    // you can add more fields...

    private static MessageDigest crypt = null;

    static{
        try {
            crypt = MessageDigest.getInstance("SHA-1");
        }
        catch (NoSuchAlgorithmException e){
            log.severe("Algorithm not found" + e);
        }
    }

    @Id
    private String userId;

    public PresenterRecord() {
        regIds = new HashSet<String>();
    }

    public Set<String> getRegIds() {
        return regIds;
    }

    public void setRegIds(Set<String> regIds) {
        this.regIds = regIds;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void addRegistrationId(String regId){
        this.getRegIds().add(regId);
    }

    public static synchronized String getUserId(String username){
        crypt.reset();
        crypt.update(username.getBytes());
        return Hex.encodeHexString(crypt.digest());
    }

    public void updateTime(){
        lastUpdate = new Date();
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
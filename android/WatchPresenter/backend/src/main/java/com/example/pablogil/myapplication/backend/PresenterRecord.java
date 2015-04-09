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

package com.example.pablogil.myapplication.backend;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * The Objectify object model for device registrations we are persisting
 */
@Entity
@Cache
public class PresenterRecord {

    private Set<String> regIds;
    // you can add more fields...

    @Id
    private String username;

    public PresenterRecord() {
        regIds = new HashSet<String>();
    }

    public Set<String> getRegIds() {
        return regIds;
    }

    public void setRegIds(Set<String> regIds) {
        this.regIds = regIds;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void addRegistrationId(String regId){
        this.getRegIds().add(regId);
    }
}
package com.zuluindia.watchpresenter.backend;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import java.util.logging.Logger;

/**
 * Created by pablo on 5/1/16.
 */
class RegistrationManager {

    private static final Logger log = Logger.getLogger(RegistrationEndpoint.class.getName());

    private Objectify ofy;

    public RegistrationManager(Objectify objectify){
        this.ofy = objectify;
    }

    public void addRegistration(String regId, User user, boolean isController){
        final String userId = PresenterRecord.getUserId(user.getEmail());
        log.info("Registration for userId: '" + userId + "'. isController: " + isController);
        PresenterRecord record = ofy.load().
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
            if(isController == false) {
                record.addRegistrationId(regId);
            }
            else{
                record.addControllerRegId(regId);
            }
        }
        record.updateTime();
        ofy.save().entity(record).now();
    }

}

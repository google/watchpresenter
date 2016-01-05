package com.zuluindia.watchpresenter.backend.persistence;

import com.googlecode.objectify.Key;

import static com.zuluindia.watchpresenter.backend.persistence.OfyService.ofy;

/**
 * Created by pablo on 5/1/16.
 */
public class ObjetifyPersistor<T> implements Persistor<T>{

    private Class<T> type;

    public ObjetifyPersistor(Class<T> type){
        this.type = type;
    }

    @Override
    public T load(String id) {
        return ofy().load().
                key(Key.create(type, id)).now();
    }

    @Override
    public void save(T entity) {

    }
}

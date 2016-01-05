package com.zuluindia.watchpresenter.backend.persistence;

/**
 * Created by pablo on 5/1/16.
 */
public interface Persistor<T> {


    /**
     * Load entity from persistence immediately
     *
     * @param id Id of the entity to be loaded
     * @return Entity or null if not found
     */
    T load(String id);

    /**
     * Persists entity (create or update)
     * @param entity Entity to be persisted
     */
    void save(T entity);

}

package io.github.maxijonson.data;

import java.io.Serializable;

public abstract class DataEntity<T> implements Serializable {
    private static transient final long serialVersionUID = 1L;

    /**
     * Syncs the current entity with the passed entity
     * 
     * @param entity
     */
    public abstract void sync(T entity);
}

package com.universal.srinbade.cache.client;

public interface ICacheClient<T> {

    public T getFromCache(final String cacheKey, final Class<T> clazz);

    public void saveToCache(final String cacheKey, final T data, final Long ttl);

    public void invalidateCache(final String cacheKey);
}

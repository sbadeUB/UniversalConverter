package com.universal.srinbade.cache.client;

import java.io.IOException;
import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.universal.srinbade.cache.InMemoryCache;

public class InMemoryCacheClient<T extends Serializable> implements ICacheClient<T> {
    private final ObjectMapper mapper;
    private final InMemoryCache cache;

    public InMemoryCacheClient() {
        mapper = new ObjectMapper();
        cache = new InMemoryCache();
    }

    @Override
    public T getFromCache(final String cacheKey, final Class<T> clazz) {
        try {
            final String stringValue = cache.getValueFromCache(cacheKey);
            if (stringValue != null) {
                return mapper.readValue(stringValue, clazz);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveToCache(final String cacheKey, final T data, Long ttl) {
        try {
            cache.putValueToCache(cacheKey, mapper.writeValueAsString(data), ttl);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void invalidateCache(final String cacheKey) {
        cache.invalidateCache(cacheKey);
    }

}

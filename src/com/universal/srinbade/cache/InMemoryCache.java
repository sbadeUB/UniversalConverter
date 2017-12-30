package com.universal.srinbade.cache;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InMemoryCache {
    private static String DEFAULT_FILE_NAME = "./cache/temp.file";
    private static Map<String, CacheValue> IN_MEMORY_MAP;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private enum CacheMetadataKey {
        TTL,
        CREATED_TIME,
        LAST_ACCESSED_TIME,
        ACCESS_COUNT
    }

    static {
        try {
            final File f = new File(DEFAULT_FILE_NAME);
            if (f.exists() && f.length() > 0L) {
                IN_MEMORY_MAP = MAPPER.readValue(f, new TypeReference<Map<String, CacheValue>>() { });
            } else {
                f.createNewFile();
                IN_MEMORY_MAP = new HashMap<>();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public String getValueFromCache(final String cacheKey) {
        final CacheValue cacheValue = IN_MEMORY_MAP.get(cacheKey);
        if (cacheValue != null && cacheValue.getMetadata() != null) {
            final Map<String, String> metadata = cacheValue.getMetadata();
            final Long ttl = Long.valueOf(metadata.get(CacheMetadataKey.TTL.name()));
            final Long createdTimeMillis = Long.valueOf(metadata.get(CacheMetadataKey.CREATED_TIME.name()));
            final Long lastAccessedTimeMillis = Long.valueOf(metadata.get(CacheMetadataKey.LAST_ACCESSED_TIME.name()));
            Long accessCount = Long.valueOf(metadata.get(CacheMetadataKey.ACCESS_COUNT.name()));
            if (lastAccessedTimeMillis - createdTimeMillis <= ttl) {
                addOrUpdateCacheEntry(cacheKey, cacheValue.getSerializedValue(),
                                      ttl, createdTimeMillis,
                                      System.currentTimeMillis(), ++accessCount);
                return cacheValue.getSerializedValue();
            } else {
                System.out.println("[DEBUG] TTL is expired for key: " + cacheKey);
                IN_MEMORY_MAP.remove(cacheKey);
            }
        }
        return null;
    }

    public void putValueToCache(final String cacheKey, final String cacheData, final Long ttl) {
        final Long currentTimeMillis = System.currentTimeMillis();
        addOrUpdateCacheEntry(cacheKey, cacheData, ttl, currentTimeMillis, currentTimeMillis, 1L);
    }

    private void addOrUpdateCacheEntry(final String cacheKey,
                                       final String cacheData,
                                       final Long ttl,
                                       final Long createdTimeMillis,
                                       final Long lastAccessedTimeMillis,
                                       final Long accessCount) {
        final Map<String, String> metadata = new HashMap<>();
        metadata.put(CacheMetadataKey.TTL.name(), String.valueOf(ttl));
        metadata.put(CacheMetadataKey.CREATED_TIME.name(), String.valueOf(createdTimeMillis));
        metadata.put(CacheMetadataKey.LAST_ACCESSED_TIME.name(), String.valueOf(lastAccessedTimeMillis));
        metadata.put(CacheMetadataKey.ACCESS_COUNT.name(), String.valueOf(accessCount));
        final CacheValue value = new CacheValue();
        value.setMetadata(metadata);
        value.setSerializedValue(cacheData);
        IN_MEMORY_MAP.put(cacheKey, value);
    }

    public void invalidateCache(final String cacheKey) {
        IN_MEMORY_MAP.remove(cacheKey);
    }

    public static void dumpToFile() {
        final File f = new File(DEFAULT_FILE_NAME);
        try {
            MAPPER.writeValue(f, IN_MEMORY_MAP);
            System.out.println("[DEBUG] Dumped the in-memory cache state.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

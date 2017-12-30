package com.universal.srinbade.cache;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;

@Data
public class CacheValue implements Serializable {
    private static final long serialVersionUID = 6244082661798499634L;
    private String serializedValue;
    private Map<String, String> metadata;
}

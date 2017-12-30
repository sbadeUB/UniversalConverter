package com.universal.srinbade.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import lombok.Data;

@Data
//{"base":"EUR","date":"2017-12-29","rates":{"GBP":0.88723,"USD":1.1993}}
public class CurrencyMappingRecord implements Serializable {
    private static final long serialVersionUID = -4278505472833762677L;
    private String base;
    private Date date;
    private Map<String, Double> rates;
}

package com.universal.srinbade.converter.io;

import java.util.Optional;

import lombok.Data;

@Data
public class CurrencyConverterInput implements Input {
    private String inputCurrencyType;
    private String outputCurrencyType;
    private Double inputCurrencyValue;
    // will call external API, if true
    private Boolean callExternalEndpoint;

    // like 1 input entity = ? output entities
    private Optional<Double> inputToOutputConvVal;
}

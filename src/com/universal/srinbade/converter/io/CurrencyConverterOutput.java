package com.universal.srinbade.converter.io;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrencyConverterOutput implements Output {
    private Double outputCurrencyValue;
}

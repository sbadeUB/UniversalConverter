package com.universal.srinbade.converter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universal.srinbade.cache.client.ICacheClient;
import com.universal.srinbade.cache.client.InMemoryCacheClient;
import com.universal.srinbade.converter.io.CurrencyConverterInput;
import com.universal.srinbade.converter.io.CurrencyConverterOutput;
import com.universal.srinbade.converter.io.Input;
import com.universal.srinbade.converter.io.Output;
import com.universal.srinbade.gateway.HttpClientGateway;
import com.universal.srinbade.model.CurrencyMappingRecord;

public class CurrencyConverter extends AbstractConverter {
    private static final boolean CALL_EXTERNAL_ENDPOINT = true;
    private static final String HTTP_API_FIXER_URL_FORMAT = "http://api.fixer.io/latest?base=%s&symbols=%s";
    private static final String CACHE_KEY_PREFIX = "CURRENCY_CONVERTER_RECORD_V1";
    private static final Long CACHE_TTL = TimeUnit.MINUTES.toMillis(2);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final ICacheClient<CurrencyMappingRecord> cache;
    private final HttpClientGateway httpClientGateway;

    public CurrencyConverter() {
        cache = new InMemoryCacheClient<>();
        httpClientGateway = new HttpClientGateway();
    }

    @Override
    public Output convert(final Input input) {
        final CurrencyConverterInput currInput = (CurrencyConverterInput) input;
        final String outputCurrType = currInput.getOutputCurrencyType();
        final Double inputCurrVal = currInput.getInputCurrencyValue();
        Objects.requireNonNull(currInput.getInputCurrencyType());
        Objects.requireNonNull(outputCurrType);
        Objects.requireNonNull(inputCurrVal);

        Double outputCurrVal;
        if (currInput.getCallExternalEndpoint()) {
            final String cacheKey = getCacheKey(currInput);
            CurrencyMappingRecord cachedValue = cache.getFromCache(cacheKey, CurrencyMappingRecord.class);
            if (cachedValue == null) {
                System.out.println("[DEBUG] Cache Miss for cacheKey: " + cacheKey);
                final Optional<String> jsonResponseOptional = httpClientGateway.sendGet(getCurrencyConverterUrl(currInput));
                if (jsonResponseOptional.isPresent()) {
                    try {
                        cachedValue = OBJECT_MAPPER.readValue(jsonResponseOptional.get(), CurrencyMappingRecord.class);
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    throw new RuntimeException("Found no response from service end-point");
                }
                if (cachedValue != null) {
                    cache.saveToCache(cacheKey, applyReverseTransformation(cachedValue, outputCurrType), CACHE_TTL);
                }
            }
            outputCurrVal = inputCurrVal * cachedValue.getRates().get(outputCurrType);
        } else {
            Objects.requireNonNull(currInput.getInputToOutputConvVal().orElse(null));
            outputCurrVal = inputCurrVal * currInput.getInputToOutputConvVal().get();
            // TODO: might want to store in cache, if not present.
        }

        return CurrencyConverterOutput.builder()
                .outputCurrencyValue(outputCurrVal)
                .build();
    }

    private CurrencyMappingRecord applyReverseTransformation(final CurrencyMappingRecord mappingRecord, final String outputCurrType) {
        final Map<String, Double> rates = mappingRecord.getRates();
        rates.put(mappingRecord.getBase(), (1 / rates.get(outputCurrType)));
        return mappingRecord;
    }

    private String getCacheKey(final CurrencyConverterInput input) {
        final List<String> inputs = Arrays.asList(input.getInputCurrencyType(), input.getOutputCurrencyType());
        inputs.sort(String::compareToIgnoreCase);
        return CACHE_KEY_PREFIX + ":" + String.join(":", inputs);
    }

    private String getCurrencyConverterUrl(final CurrencyConverterInput input) {
        return String.format(HTTP_API_FIXER_URL_FORMAT, input.getInputCurrencyType(), input.getOutputCurrencyType());
    }

    @Override
    public Input getInput(final Scanner scanner) {
        final CurrencyConverterInput input = new CurrencyConverterInput();

        System.out.print("\nEnter your input currency type: ");
        input.setInputCurrencyType(scanner.next());

        System.out.print("Enter your output currency type: ");
        input.setOutputCurrencyType(scanner.next());

        System.out.print("Enter your input currency value: ");
        input.setInputCurrencyValue(scanner.nextDouble());

        input.setCallExternalEndpoint(CALL_EXTERNAL_ENDPOINT);

        /*
        System.out.print("Do you want to get conversion rate online (true/false)? ");
        input.setCallExternalEndpoint(scanner.nextBoolean());

        if (!input.getCallExternalEndpoint()) {
            System.out.print("Enter your input -> output conversion rate: ");
            input.setInputToOutputConvVal(Optional.ofNullable(scanner.nextDouble()));
        }*/
        return input;
    }
}
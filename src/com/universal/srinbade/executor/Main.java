package com.universal.srinbade.executor;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.universal.srinbade.cache.InMemoryCache;
import com.universal.srinbade.converter.AbstractConverter;
import com.universal.srinbade.converter.CurrencyConverter;

public final class Main {
    private static final long CLEANUP_INTERVAL_MILLIS = TimeUnit.SECONDS.toMillis(60);

    public static void main(String[] args) {
        Long cleanUpStartMillis = System.currentTimeMillis();
        final AbstractConverter converter = new CurrencyConverter();
        try (final Scanner scanner = new Scanner(System.in)) {
            do {
                converter.printOutput(converter.convert(converter.getInput(scanner)));
                final Long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - cleanUpStartMillis > CLEANUP_INTERVAL_MILLIS) {
                    performCleanup();
                    cleanUpStartMillis = System.currentTimeMillis();
                }
            } while (true);
        } finally {
            performCleanup();
        }
    }

    public static void performCleanup() {
        InMemoryCache.dumpToFile();
        System.gc();
    }
}

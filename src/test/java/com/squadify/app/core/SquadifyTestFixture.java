package com.squadify.app.core;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.stream.IntStream;

@UtilityClass
public class SquadifyTestFixture {

    public static String someString() {
        return someString(12);
    }

    private static String someString(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }

}

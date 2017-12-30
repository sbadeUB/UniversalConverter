package com.universal.srinbade.converter;

import java.util.Scanner;

import com.universal.srinbade.converter.io.Input;
import com.universal.srinbade.converter.io.Output;

public abstract class AbstractConverter {
    public abstract Input getInput(final Scanner scanner);
    public abstract Output convert(final Input input);

    public void printOutput(final Output output) {
        System.out.println("\n###########OUTPUT############");
        System.out.println(output);
    }
}

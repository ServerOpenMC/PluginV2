package fr.openmc.core.features.chatanimations.contents.mathsQuestions;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class CalculGenerator {

    private final int number1;
    private final int number2;
    private final char operator;
    @Getter
    private final int result;

    public CalculGenerator() {
        this.number1 = ThreadLocalRandom.current().nextInt(1, 101);
        this.number2 = ThreadLocalRandom.current().nextInt(1, 101);

        char[] operators = {'+', '-', '*'};
        this.operator = operators[
                ThreadLocalRandom.current().nextInt(operators.length)
                ];

        this.result = calculate();
    }

    private int calculate() {
        return switch (operator) {
            case '+' -> number1 + number2;
            case '-' -> number1 - number2;
            case '*' -> number1 * number2;
            default -> throw new IllegalStateException("Unknown operator");
        };
    }

    public Component getQuestion() {
        return Component.text(number1 + " " + operator + " " + number2);
    }

}

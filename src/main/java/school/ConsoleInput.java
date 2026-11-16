package school;

import java.util.Locale;
import java.util.Scanner;

final class ConsoleInput {
    private final Scanner scanner;

    ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    String text(String prompt) {
        System.out.print(prompt + ": ");
        if (!scanner.hasNextLine()) {
            throw new EndOfInput();
        }
        return scanner.nextLine().strip();
    }

    String command(String prompt) {
        return text(prompt).toLowerCase(Locale.ROOT);
    }

    int integer(String prompt) {
        return toInteger(text(prompt));
    }

    double decimal(String prompt) {
        try {
            return Double.parseDouble(text(prompt));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Enter a number, for example 85.5");
        }
    }

    static int toInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Enter a whole number");
        }
    }

    static final class EndOfInput extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}

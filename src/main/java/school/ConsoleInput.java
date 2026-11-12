package school;

import java.util.Scanner;

final class ConsoleInput {
    private final Scanner scanner;

    ConsoleInput(Scanner scanner) { this.scanner = scanner; }

    String text(String prompt) {
        System.out.print(prompt + ": ");
        if (!scanner.hasNextLine()) throw new EndOfInput();
        return scanner.nextLine().strip();
    }

    int integer(String prompt) {
        try { return Integer.parseInt(text(prompt)); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Enter a whole number"); }
    }

    double decimal(String prompt) {
        try { return Double.parseDouble(text(prompt)); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Enter a number, for example 85.5"); }
    }

    static final class EndOfInput extends RuntimeException {}
}

package calculator;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Calculator calculator = new Calculator();
        // put your code here
        while (true) {
            String input = scanner.nextLine();
            if (input.isEmpty()) {
                continue;
            }

            if (input.startsWith("/")) {
                if (input.matches("\\s*/exit\\s*")) {
                    System.out.println("Bye!");
                    break;
                } else if (input.matches("\\s*/help\\s*")) {
                    System.out.println("The program calculates the sum of numbers with addition and subtraction");
                } else {
                    System.out.println("Unknown command");
                }
                continue;
            }

            if (!calculator.process(input)) {
                System.out.println("Invalid expression");
            }
        }
        scanner.close();
    }
}

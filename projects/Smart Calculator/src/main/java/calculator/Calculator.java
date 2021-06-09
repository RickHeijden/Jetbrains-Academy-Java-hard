package calculator;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

public class Calculator {

    private final Map<String, BigInteger> variables;

    public Calculator() {
        variables = new HashMap<>();
    }

    public boolean process(String input) {
        if (input.contains("=") || input.matches("\\s*[a-zA-Z]+\\s*")) {
            variable(input);
            return true;
        } else if (input.matches("[\\(*\\w+)\\)*[+/ \\-^]*]*\\(*\\w+\\)*")) {
            calculate(input);
            return true;
        }
        return false;
    }

    public void variable(String input) {
        if (input.contains("=")) {
            setVariable(input);
        } else {
            getVariable(input);
        }
    }

    public void calculate(String input) {
        Pattern invalidMult = Pattern.compile("\\*(\\*)+");
        Pattern invalidDiv = Pattern.compile("/(/)+");
        if (invalidDiv.matcher(input).find() || invalidMult.matcher(input).find()) {
            System.out.println("Invalid expression");
            return;
        }

        input = input.replaceAll("\\++\\s*", " + ");
        input = input.replaceAll("-\\s*(-\\s*-)*\\s*", " - ").replaceAll("(-\\s*-)+\\s*", " + ");
        input = input.replaceAll("\\s*\\*\\s*", " * ").replaceAll("\\s*/\\s*", " / ");
        input = input.replaceAll("\\s*\\(\\s*", " ( ").replaceAll("\\s*\\)\\s*", " ) ");
        input = input.replaceAll("\\s*\\^\\s*", " ^ ").replaceAll("\\s+", " ");

        if (Arrays.stream(input.split(" ")).filter(s -> s.equals("(")).count() !=
                Arrays.stream(input.split(" ")).filter(s -> s.equals(")")).count()) {
            System.out.println("Invalid expression");
            return;
        }

        boolean variablesExist =
                Arrays.stream(input.split(" ")).allMatch(s -> !s.matches("[a-zA-Z]+") || variables.containsKey(s));

        if (variablesExist) {
            String postfix = infixToPostfix(input);
            System.out.println(postfixToAnswer(postfix));
        } else {
            System.out.println("Unknown variable");
        }
    }

    public String infixToPostfix(String input) {
        StringBuilder builder = new StringBuilder();
        Stack<String> stack = new Stack<>();
        String[] expression = input.split(" ");

        for (String part : expression) {
            if (part.matches("\\d+")) {
                builder.append(part).append(" ");
            } else if (part.matches("[-+]")) {
                while (!stack.isEmpty() && stack.peek().matches("[*+/^-]")) {
                    builder.append(stack.pop()).append(" ");
                }
                stack.push(part);
            } else if (part.matches("[*/]")) {
                while (!stack.isEmpty() && stack.peek().matches("[*/^]")) {
                    builder.append(stack.pop()).append(" ");
                }
                stack.push(part);
            } else if (part.equals("^")) {
                while (!stack.isEmpty() && stack.peek().equals("^")) {
                    builder.append(stack.pop()).append(" ");
                }
                stack.push(part);
            } else if (part.equals("(")) {
                stack.push(part);
            } else if (part.equals(")")) {
                while (!stack.peek().equals("(")) {
                    builder.append(stack.pop()).append(" ");
                }
                stack.pop();
            } else {
                builder.append(variables.get(part)).append(" ");
            }
        }

        while (!stack.isEmpty()) {
            builder.append(stack.pop()).append(" ");
        }

        return builder.toString();
    }

    public BigInteger postfixToAnswer(String postfix) {
        Stack<BigInteger> stack = new Stack<>();
        String[] expression = postfix.split(" ");

        for (String part : expression) {
            if (part.matches("[-]*\\d+")) {
                stack.push(new BigInteger(part));
            } else {
                switch (part) {
                case "+": {
                    BigInteger result = stack.pop().add(stack.pop());
                    stack.push(result);
                    break;
                }
                case "-": {
                    BigInteger minus = stack.pop();
                    stack.push(stack.pop().subtract(minus));
                    break;
                }
                case "*": {
                    BigInteger result = stack.pop().multiply(stack.pop());
                    stack.push(result);
                    break;
                }
                case "/":
                    BigInteger div = stack.pop();
                    BigInteger num = stack.pop();
                    stack.push(num.divide(div));
                    break;
                case "^":
                    long exp = stack.pop().longValue();
                    long base = stack.pop().longValue();
                    stack.push(BigInteger.valueOf((long) Math.pow(base, exp)));
                    break;
                }
            }
        }

        return stack.pop();
    }

    public void setVariable(String input) {
        if (input.matches("\\s*[a-zA-Z]+\\s*=\\s*[-]*\\s*(\\d+|[a-zA-Z]+)\\s*")) {
            input = input.replaceAll("-\\s*(-\\s*-)*\\s*", "-").replaceAll("(-\\s*-)+\\s*", "");
            String[] values = input.replaceAll("\\s*", "").trim().split("=");
            if (values[1].matches("[-]*\\d+")) {
                variables.put(values[0], new BigInteger(values[1]));
            } else {
                if (variables.containsKey(values[1])) {
                    variables.put(values[0], variables.get(values[1]));
                } else {
                    System.out.println("Unknown variable");
                }
            }
        } else if (input.matches("[a-zA-Z]+\\s*=.*")) {
            System.out.println("Invalid assignment");
        } else {
            System.out.println("Invalid identifier");
        }
    }

    public void getVariable(String input) {
        input = input.replaceAll("\\s+", "");
        if (variables.containsKey(input)) {
            System.out.println(variables.get(input));
        } else {
            System.out.println("Unknown variable");
        }
    }
}

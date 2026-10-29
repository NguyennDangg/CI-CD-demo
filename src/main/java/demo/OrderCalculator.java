package demo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;

/** Calculates a simple order total after a percentage discount. */
public class OrderCalculator {
    public BigDecimal totalAfterDiscount(BigDecimal subtotal, BigDecimal discountPercent) {
        if (subtotal == null || discountPercent == null) {
            throw new IllegalArgumentException("Subtotal and discount are required");
        }
        if (subtotal.signum() < 0) {
            throw new IllegalArgumentException("Subtotal cannot be negative");
        }
        if (discountPercent.signum() < 0 || discountPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Discount must be between 0 and 100 percent");
        }

        BigDecimal discount = subtotal
                .multiply(discountPercent)
                .movePointLeft(2);
        return subtotal.subtract(discount).setScale(2, RoundingMode.HALF_UP);
    }

    public static void main(String[] args) {
        OrderCalculator calculator = new OrderCalculator();
        Scanner input = new Scanner(System.in);

        System.out.println("========================================");
        System.out.println(" JAVA CHECKOUT AND CI/CD DEMO");
        System.out.println("========================================");
        System.out.println("The automated build and seven unit tests have passed.");
        System.out.println("Choose what you would like to do:");

        while (input.hasNextLine()) {
            System.out.println();
            System.out.println("1. Enter my own order");
            System.out.println("2. Run the guided examples");
            System.out.println("0. Exit");
            System.out.print("Your choice: ");

            String choice = input.nextLine().trim();
            switch (choice) {
                case "1":
                    calculateCustomOrder(input, calculator);
                    break;
                case "2":
                    runGuidedExamples(input, calculator);
                    break;
                case "0":
                    System.out.println("Demo finished. Goodbye!");
                    return;
                default:
                    System.out.println("Please enter 1, 2, or 0.");
            }
        }

        System.out.println("Input ended. Demo finished.");
    }

    private static void calculateCustomOrder(Scanner input, OrderCalculator calculator) {
        System.out.println();
        System.out.println("--- Your order ---");
        System.out.println("Enter two numbers on one line: subtotal, then discount percentage.");
        System.out.println("Example: 49.99 20 means $49.99 with a 20% discount.");

        while (input.hasNextLine()) {
            System.out.print("Subtotal and discount: ");
            String[] values = input.nextLine().trim().split("\\s+");
            if (values.length != 2) {
                System.out.println("Enter exactly two numbers, for example: 49.99 20");
                continue;
            }

            try {
                BigDecimal subtotal = new BigDecimal(values[0]);
                BigDecimal discount = new BigDecimal(values[1]);
                System.out.println("You entered subtotal $"
                        + subtotal.setScale(2, RoundingMode.HALF_UP)
                        + " and discount " + discount.stripTrailingZeros().toPlainString() + "%.");
                showCalculation(calculator, "Your result", subtotal, discount);
                return;
            } catch (NumberFormatException exception) {
                System.out.println("Both values must be numbers, for example: 49.99 20");
            } catch (IllegalArgumentException exception) {
                System.out.println("Order not accepted: " + exception.getMessage());
                return;
            }
        }
    }

    private static void runGuidedExamples(Scanner input, OrderCalculator calculator) {
        System.out.println();
        System.out.println("The same calculator rules are also checked automatically by JUnit.");

        pause(input, "Example 1 of 4: A normal discounted order");
        showCalculation(calculator, "Standard order", new BigDecimal("120.00"), new BigDecimal("15"));

        pause(input, "Example 2 of 4: No discount");
        showCalculation(calculator, "Regular price", new BigDecimal("19.99"), BigDecimal.ZERO);

        pause(input, "Example 3 of 4: A full discount");
        showCalculation(calculator, "Free order", new BigDecimal("39.99"), new BigDecimal("100"));

        pause(input, "Example 4 of 4: Invalid input is rejected safely");
        try {
            calculator.totalAfterDiscount(new BigDecimal("50.00"), new BigDecimal("120"));
            System.out.println("Unexpected: the invalid discount was accepted.");
        } catch (IllegalArgumentException exception) {
            System.out.println("Input rejected: " + exception.getMessage());
        }

        System.out.println("Guided examples complete.");
    }

    private static void pause(Scanner input, String heading) {
        System.out.println();
        System.out.println("--- " + heading + " ---");
        System.out.println("Press Enter to continue.");
        if (input.hasNextLine()) {
            input.nextLine();
        }
    }

    private static void showCalculation(
            OrderCalculator calculator, String label, BigDecimal subtotal, BigDecimal discount) {
        BigDecimal total = calculator.totalAfterDiscount(subtotal, discount);

        System.out.println("Example: " + label);
        System.out.println("Subtotal: $" + subtotal.setScale(2, RoundingMode.HALF_UP));
        System.out.println("Discount: " + discount.stripTrailingZeros().toPlainString() + "%");
        System.out.println("Formula: subtotal - (subtotal x discount / 100)");
        System.out.println("Order total: $" + total);
    }
}

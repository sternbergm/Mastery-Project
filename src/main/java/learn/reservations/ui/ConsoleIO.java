package learn.reservations.ui;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

@Component
public class ConsoleIO implements TextIO {

    private static final String INVALID_NUMBER = "Invalid entry, please enter a valid number";
    private static final String NUMBER_OUT_OF_RANGE = "Invalid entry, please enter a number between %s and %s";

    private static final String PRICE_OUT_OF_RANGE = "Invalid entry, please enter a price greater than 0";
    private static final String REQUIRED = "Value is required and must be valid";
    private static final String INVALID_DATE = "Enter a date in yyyy-mm-dd format";
    private final Scanner scanner = new Scanner(System.in);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Override
    public void print(String message) {
        System.out.print(message);
    }

    @Override
    public void println(String message) {
        System.out.println(message);
    }

    @Override
    public String readString(String prompt) {
        println(prompt);
        return scanner.nextLine();
    }

    @Override
    public String readRequiredString(String prompt) {
        while (true) {
            String result = readString(prompt);
            if (!result.isBlank() && !result.contains(",")) {
                return result;
            }
            println(REQUIRED);
        }
    }

    private int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readRequiredString(prompt));
            } catch (NumberFormatException ex) {
                println(INVALID_NUMBER);
            }
        }
    }

    private int updateInt(String prompt, int oldValue) {
        while (true) {
            String input = readString(String.format(prompt, oldValue));
            if(input.equals("")) return oldValue;
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                println(INVALID_NUMBER);
            }
        }
    }

    @Override
    public int readInt(String prompt, int min, int max) {
        do {
            int number = readInt(prompt);
            if (number>=min && number<= max) {
                return number;
            }
            println(String.format(NUMBER_OUT_OF_RANGE, min, max));
        } while (true);
    }

    @Override
    public boolean readBoolean(String prompt) {
        do {
            String input = readRequiredString(prompt);
            if(input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes")){
                return true;
            } else if(input.equalsIgnoreCase("n") || input.equalsIgnoreCase("no")){
                return false;
            }
            println("Please enter one of the following options: ( y / yes / n / no )");
        } while (true);
    }

    @Override
    public LocalDate readLocalDate(String prompt) {
        do {
            String input = readRequiredString(prompt);
            try {
                return LocalDate.parse(input, formatter);
            } catch (DateTimeParseException e) {
                println(INVALID_DATE);
            }
        } while (true);
    }

    @Override
    public LocalDate updateLocalDate(String prompt, LocalDate date) {
        do {
            String input = readString(prompt);
            try {
                if(input.equalsIgnoreCase("")) return date;
                return LocalDate.parse(input, formatter);
            } catch (DateTimeParseException e) {
                println(INVALID_DATE);
            }
        } while (true);
    }

    @Override
    public BigDecimal readPrice(String prompt) {
        do {
            try {
                BigDecimal number = BigDecimal.valueOf(Double.parseDouble(readRequiredString(prompt))).setScale(2, RoundingMode.HALF_UP);
                if (number.compareTo(BigDecimal.ZERO)>0) {
                    return number;
                }
            } catch (NumberFormatException e) {
                println(INVALID_NUMBER);
            }
            println(PRICE_OUT_OF_RANGE);
        } while (true);
    }

    @Override
    public String updateRequiredString(String prompt, String oldValue) {
        while (true) {
            String result = readString(String.format(prompt, oldValue));
            if(result.equals("")) return oldValue;
            if (!result.contains(",")) {
                return result;
            }
            println(REQUIRED);
        }
    }

    @Override
    public int updateInt(String prompt, int min, int max, int oldValue) {
        do {
            int number = updateInt(prompt, oldValue);
            if (number>=min && number<= max) {
                return number;
            }
            println(String.format(NUMBER_OUT_OF_RANGE, min, max));
        } while (true);
    }

    @Override
    public BigDecimal updatePrice(String prompt, BigDecimal oldValue) {
        do {
            String input = readString(String.format(prompt, oldValue));
            if(input.equals("")) return oldValue;
            try {
                BigDecimal number = BigDecimal.valueOf(Double.parseDouble(input)).setScale(2, RoundingMode.HALF_UP);
                if (number.compareTo(BigDecimal.ZERO)>0) {
                    return number;
                }
            } catch (NumberFormatException e) {
                println(INVALID_NUMBER);
            }
            println(PRICE_OUT_OF_RANGE);
        } while (true);
    }
}

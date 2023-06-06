package learn.reservations.ui;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TextIO {
    void print(String message);
    void println(String message);
    String readString(String prompt);
    String readRequiredString(String prompt);
    int readInt(String prompt, int min, int max);
    boolean readBoolean(String prompt);
    LocalDate readLocalDate(String prompt);

    LocalDate updateLocalDate(String prompt, LocalDate date);

    BigDecimal readPrice(String s);

    String updateRequiredString(String prompt, String oldValue);

    int updateInt(String prompt, int min, int max, int oldValue);

    BigDecimal updatePrice(String prompt, BigDecimal oldValue);
}

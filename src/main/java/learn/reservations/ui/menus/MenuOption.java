package learn.reservations.ui.menus;

import java.util.Arrays;

public enum MenuOption{

    EXIT(0, "Exit"),
    VIEW_INFORMATION(1, "View Information"),
    DO_RESERVATION(2, "Reservation Menu"),
    DO_HOST(3, "Host Menu"),
    DO_GUEST(4, "Guest Menu");

    private int value;
    private String message;

    private MenuOption(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public static MenuOption fromValue(int value) {
        return Arrays.stream(MenuOption.values()).filter(option -> option.getValue() == value).findFirst().orElse(EXIT);
    }



    public int getValue() {
        return value;
    }


    public String getMessage() {
        return message;
    }
}

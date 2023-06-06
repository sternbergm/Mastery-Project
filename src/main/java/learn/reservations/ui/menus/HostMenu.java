package learn.reservations.ui.menus;

import java.util.Arrays;

public enum HostMenu{

    EXIT(0, "Exit"),
    VIEW_HOSTS(1,"View Host list"),
    ADD_HOST(2, "Add a new Host"),
    UPDATE_HOST(3, "Modify a Host"),
    DELETE_HOST(4, "Remove a Host");

    private int value;
    private String message;

    private HostMenu(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public static HostMenu fromValue(int value) {
        return Arrays.stream(HostMenu.values()).filter(option -> option.getValue() == value).findFirst().orElse(EXIT);
    }



    public int getValue() {
        return value;
    }


    public String getMessage() {
        return message;
    }
}

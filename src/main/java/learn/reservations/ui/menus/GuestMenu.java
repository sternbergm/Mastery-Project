package learn.reservations.ui.menus;

import java.util.Arrays;

public enum GuestMenu{

    EXIT(0, "Exit"),
    VIEW_GUESTS(1, "View Guest list"),
    ADD_GUEST(2, "Add a new Guest"),
    UPDATE_GUEST(3, "Modify a Guest"),
    DELETE_GUEST(4, "Remove a Guest");

    private int value;
    private String message;

    private GuestMenu(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public static GuestMenu fromValue(int value) {
        return Arrays.stream(GuestMenu.values()).filter(option -> option.getValue() == value).findFirst().orElse(EXIT);
    }



    public int getValue() {
        return value;
    }


    public String getMessage() {
        return message;
    }
}

package learn.reservations.ui.menus;

import java.util.Arrays;

public enum ViewMenu{

    EXIT(0, "Exit"),
    VIEW_RESERVATIONS_BY_HOST(1, "View Reservations By host"),
    VIEW_RESERVATION_BY_GUEST(2, "View reservations by Guest"),
    VIEW_RESERVATION_BY_STATE(3, "View Reservation by state"),
    VIEW_RESERVATION_BY_CITY(4, "View Reservation by city"),
    VIEW_RESERVATION_BY_ADDRESS(5, "View Reservation by Address");

    private int value;
    private String message;

    private ViewMenu(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public static ViewMenu fromValue(int value){
        return Arrays.stream(ViewMenu.values()).filter(option -> option.getValue() == value).findFirst().orElse(EXIT);
    }


    public int getValue() {
        return value;
    }


    public String getMessage() {
        return message;
    }
}

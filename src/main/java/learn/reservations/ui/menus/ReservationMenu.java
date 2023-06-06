package learn.reservations.ui.menus;

import java.util.Arrays;

public enum ReservationMenu {
        EXIT(0, "Exit"),
        ADD_RESERVATION(1, "Add a new reservation"),
        UPDATE_RESERVATION(2, "Modify a reservation"),
        DELETE_RESERVATION(3, "Cancel a reservation");


private int value;
private String message;

private ReservationMenu(int value, String message) {
        this.value = value;
        this.message = message;
        }

public static ReservationMenu fromValue(int value) {
        return Arrays.stream(ReservationMenu.values()).filter(option -> option.getValue() == value).findFirst().orElse(EXIT);
        }



public int getValue() {
        return value;
        }


public String getMessage() {
        return message;
        }
        }

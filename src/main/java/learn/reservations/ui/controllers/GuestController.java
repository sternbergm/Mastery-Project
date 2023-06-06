package learn.reservations.ui.controllers;

import learn.reservations.dal.DALException;
import learn.reservations.domains.PersonService;
import learn.reservations.domains.ReservationService;
import learn.reservations.domains.Result;
import learn.reservations.models.Guest;
import learn.reservations.ui.View;
import learn.reservations.ui.menus.GuestMenu;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GuestController {

    private View view;
    private PersonService<Guest> guestService;
    private ReservationService reservationService;
    private ControllerHelper helper;

    public GuestController(View view, PersonService<Guest> guestService, ReservationService reservationService, ControllerHelper helper) {
        this.view = view;
        this.guestService = guestService;
        this.reservationService = reservationService;
        this.helper = helper;
    }

    public void runGuestMenu() throws DALException {
        GuestMenu option = view.selectGuestMenu();
        switch (option) {
            case VIEW_GUESTS -> viewGuestsByLastName();
            case ADD_GUEST -> addGuest();
            case UPDATE_GUEST -> updateGuest();
            case DELETE_GUEST -> deleteGuest();
        }
    }

    private void viewGuestsByLastName() throws DALException {
        view.displayHeader("View guests by last name");
        String lastNamePrefix = view.getLastNamePrefix();
        List<Guest> guests = guestService.findByLastNamePrefix(lastNamePrefix);
        view.displayHeader("Guests");
        if(guests.size()==0){
            view.displayMessage("No guests under that last name");
            view.enterToContinue();
            return;
        }
        guests.stream().limit(50).forEach(g -> view.displayMessage(g.toString()));
        view.enterToContinue();
    }

    private void addGuest() throws DALException {
        view.displayHeader("Add a new guest");

        Guest guest = view.makeGuest();
        if(guest == null) {
            view.displayMessage("Guest was discarded");
            view.enterToContinue();
            return;
        }
        Result<Guest> result = guestService.add(guest);
        handleGuestResult(result, "Guest %s successfully added");
        view.enterToContinue();
    }

    private void updateGuest() throws DALException {
        view.displayHeader("Modify a Guest");
        Guest guest = helper.getGuest();
        if(guest == null) {
            view.displayMessage("Action aborted");
            view.enterToContinue();
            return;
        }

        guest = view.modifyGuest(guest);
        if(guest == null) {
            view.displayMessage("Changes to guest discarded");
            view.enterToContinue();
            return;
        }

        Result<Guest> result = guestService.update(guest);
        handleGuestResult(result, "Guest %s was updated");
        view.enterToContinue();
    }

    private void deleteGuest() throws DALException {
        view.displayHeader("Remove a Guest");
        Guest guest = helper.getGuest();
        if(guest == null) {
            view.displayMessage("Action aborted");
            view.enterToContinue();
            return;
        }
        int numberOfReservations = reservationService.getReservationsByGuest(guest.getId()).size();
        String message = String.format("Are you sure you want to delete this guest? you will also delete all reservations made under their name (yes/y/no/n)\nNumber of reservations: %d", numberOfReservations);
        if(view.makeYesNoChoice(message)){
            guestService.delete(guest);
            reservationService.deleteReservationsByGuest(guest.getId());
            view.displayMessage("Guest was deleted");
            view.enterToContinue();
        } else {
            view.displayMessage("Action aborted");
            view.enterToContinue();
        }
    }

    private void handleGuestResult(Result<Guest> result, String s) {
        if(result.isSuccessful()){
            String successMessage = String.format(s, result.getPayload().getId());
            view.displayStatus(true, List.of(successMessage));
        }
        else {
            view.displayStatus(false, result.getMessages());
        }
    }
}

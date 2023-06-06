package learn.reservations.ui.controllers;

import learn.reservations.dal.DALException;
import learn.reservations.domains.PersonService;
import learn.reservations.domains.ReservationService;
import learn.reservations.domains.Result;
import learn.reservations.models.Guest;
import learn.reservations.models.Host;
import learn.reservations.models.Reservation;
import learn.reservations.ui.View;
import learn.reservations.ui.menus.ReservationMenu;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReservationController {

    private View view;
    private PersonService<Host> hostService;
    private PersonService<Guest> guestService;
    private ReservationService reservationService;
    private ControllerHelper helper;

    public ReservationController(View view, PersonService<Host> hostService, PersonService<Guest> guestService, ReservationService reservationService, ControllerHelper helper) {
        this.view = view;
        this.hostService = hostService;
        this.guestService = guestService;
        this.reservationService = reservationService;
        this.helper = helper;
    }

    public void runReservationMenu() throws DALException {
        ReservationMenu option = view.selectReservationMenuOption();
        switch (option) {
            case ADD_RESERVATION -> addReservation();
            case UPDATE_RESERVATION -> updateReservation();
            case DELETE_RESERVATION -> deleteReservation();
        }
    }

    private void addReservation() throws DALException {
        view.displayHeader("Add a reservation");
        Host host = helper.getHost();
        if(host == null) {
            view.displayMessage("Action cancelled");
            view.enterToContinue();
            return;
        }
        Guest guest = helper.getGuest();
        if(guest == null) {
            view.displayMessage("Action cancelled");
            view.enterToContinue();
            return;
        }

        List<Reservation> reservations = reservationService.getReservationsByHost(host.getId());
        Reservation reservation = view.makeReservation(host, guest, reservations);
        if(reservation == null){
            view.displayMessage("Reservation was discarded.");
            view.enterToContinue();
            return;
        }
        Result<Reservation> result = reservationService.makeReservation(host.getId(), reservation);
        handleResult(result, "Reservation %s was created");
        view.enterToContinue();
    }



    private void updateReservation() throws DALException {
        view.displayHeader("Update a reservation");
        Host host = helper.getHost();
        if(host == null) {
            view.displayMessage("Action cancelled");
            view.enterToContinue();
            return;
        }

        List<Reservation> reservations = reservationService.getReservationsByHost(host.getId());

        if (helper.validateReservations(reservations)){
            view.enterToContinue();
            return;
        }

        Reservation reservation = helper.getFutureReservation(reservations);
        if (reservation == null) {
            view.enterToContinue();
            return;
        }

        Reservation newReservation = view.updateReservation(reservation, reservations, host);

        if(newReservation == null){
            view.displayMessage("Reservation update was discarded.");
            return;
        }
        Result<Reservation> result = reservationService.updateReservation(host.getId(), reservation.getId(), newReservation);
        handleResult(result, "Reservation %s was updated");
        view.enterToContinue();
    }

    private void deleteReservation() throws DALException {
        view.displayHeader("Cancel a reservation");
        Host host = helper.getHost();
        if(host == null) {
            view.displayMessage("Action cancelled");
            view.enterToContinue();
            return;
        }

        List<Reservation> reservations = reservationService.getReservationsByHost(host.getId());
        if (helper.validateReservations(reservations)){
            view.enterToContinue();
            return;
        }

        Reservation reservation = helper.getFutureReservation(reservations);
        if (reservation == null) {
            view.enterToContinue();
            return;
        }

        if(view.makeYesNoChoice("Are you sure you want to cancel the following reservation? \n"+reservation)){
            reservationService.deleteReservation(host.getId(), reservation.getId());
            view.displayMessage("Reservation was cancelled");
            view.enterToContinue();
        } else{
            view.displayMessage("Cancellation aborted");
            view.enterToContinue();
        }
    }

    private void handleResult(Result<Reservation> result, String s) {
        if(result.isSuccessful()){
            String successMessage = String.format(s, result.getPayload().getId());
            view.displayStatus(true, List.of(successMessage));
        }
        else {
            view.displayStatus(false, result.getMessages());
        }
    }
}

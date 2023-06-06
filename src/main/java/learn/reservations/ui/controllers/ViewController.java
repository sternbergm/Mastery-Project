package learn.reservations.ui.controllers;

import learn.reservations.dal.DALException;
import learn.reservations.domains.PersonService;
import learn.reservations.domains.ReservationService;
import learn.reservations.models.Guest;
import learn.reservations.models.Host;
import learn.reservations.models.Reservation;
import learn.reservations.ui.View;
import learn.reservations.ui.controllers.ControllerHelper;
import learn.reservations.ui.menus.ViewMenu;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ViewController {

    private View view;
    private PersonService<Host> hostService;
    private PersonService<Guest> guestService;
    private ReservationService reservationService;
    private ControllerHelper helper;

    public ViewController(View view, PersonService<Host> hostService, PersonService<Guest> guestService, ReservationService reservationService, ControllerHelper helper) {
        this.view = view;
        this.hostService = hostService;
        this.guestService = guestService;
        this.reservationService = reservationService;
        this.helper = helper;
    }

    public void runViewMenu() throws DALException{
        ViewMenu option =  view.selectViewMenuOption();
        switch (option) {
            case VIEW_RESERVATIONS_BY_HOST -> viewReservationByHost();
            case VIEW_RESERVATION_BY_GUEST -> viewReservationByGuest();
            case VIEW_RESERVATION_BY_STATE -> viewByState();
            case VIEW_RESERVATION_BY_CITY -> viewByCity();
            case VIEW_RESERVATION_BY_ADDRESS -> viewByAddress();
        }
    }

    private void viewByAddress() throws DALException {
        view.displayMessage("View reservations by Address");
        String state = view.getInfo();
        List<Host> HostsPerAddress = hostService.findByAddress(state);
        List<Reservation> reservationsByAddress = reservationService.getReservationsByHosts(HostsPerAddress);
        if(reservationsByAddress.size() == 0 || reservationsByAddress == null) {
            view.displayMessage("No reservations in this Address");
            view.enterToContinue();
            return;
        }
        List<Guest> guests = guestService.getAll();
        view.displayReservationsByHostWithGuests(reservationsByAddress, HostsPerAddress, guests);
        view.enterToContinue();
    }

    private void viewByCity() throws DALException {
        view.displayMessage("View reservations by City");
        String state = view.getInfo();
        List<Host> hostsPerCity = hostService.findByCity(state);
        List<Reservation> reservationsByCity = reservationService.getReservationsByHosts(hostsPerCity);
        if(reservationsByCity.size() == 0 || reservationsByCity == null) {
            view.displayMessage("No reservations in this City");
            view.enterToContinue();
            return;
        }
        List<Guest> guests = guestService.getAll();
        view.displayReservationsByHostWithGuests(reservationsByCity, hostsPerCity, guests);
        view.enterToContinue();
    }

    private void viewByState() throws DALException {
        view.displayMessage("View reservations by state");
        String state = view.getInfo();
        List<Host> hostsPerState = hostService.findByState(state);
        List<Reservation> reservationsByState = reservationService.getReservationsByHosts(hostsPerState);
        if(reservationsByState.size() == 0 || reservationsByState == null) {
            view.displayMessage("No reservations in this state");
            view.enterToContinue();
            return;
        }
        List<Guest> guests = guestService.getAll();
        view.displayReservationsByHostWithGuests(reservationsByState, hostsPerState, guests);
        view.enterToContinue();
    }


    private void viewReservationByHost() throws DALException {
        view.displayHeader("Reservations by host");
        Host host = helper.getHost();
        if (host == null) {
            view.displayMessage("Action cancelled");
            view.enterToContinue();
            return;
        }

        List<Reservation> reservations = reservationService.getReservationsByHost(host.getId());
        if (helper.validateReservations(reservations)) {
            view.enterToContinue();
            return;
        }
        List<Guest> guests = guestService.getAll();
        view.displayReservationsWithGuests(reservations, guests);
        view.enterToContinue();
    }

    private void viewReservationByGuest() throws DALException {
        view.displayHeader("Reservations by guest");
        Guest guest = helper.getGuest();
        if(guest == null) {
            view.displayMessage("Action cancelled");
            view.enterToContinue();
            return;
        }

        List<Reservation> reservations = reservationService.getReservationsByGuest(guest.getId());
        if (helper.validateReservations(reservations)) return;
        List<Host> hosts = hostService.getAll();
        view.displayReservationsByHost(reservations, hosts);
        view.enterToContinue();
    }
}

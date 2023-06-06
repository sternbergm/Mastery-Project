package learn.reservations.ui.controllers;

import learn.reservations.dal.DALException;
import learn.reservations.domains.PersonService;
import learn.reservations.domains.ReservationService;
import learn.reservations.domains.Result;
import learn.reservations.models.Guest;
import learn.reservations.models.Host;
import learn.reservations.models.Reservation;
import learn.reservations.ui.View;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ControllerHelper {

    private View view;
    private PersonService<Host> hostService;
    private PersonService<Guest> guestService;

    public ControllerHelper(View view, PersonService<Host> hostService, PersonService<Guest> guestService) {
        this.view = view;
        this.hostService = hostService;
        this.guestService = guestService;
    }

    public Reservation getFutureReservation(List<Reservation> reservations) {
        Reservation reservation = view.getFutureReservation(reservations);

        if(reservation == null) return null;
        return reservation;
    }

    public Host getHost() throws DALException {
        view.displayMessage("Choose a host: ");
        String email = view.getEmail();
        Host host = hostService.findByEmail(email);
        if(host == null) {
            view.displayMessage("No host was found under that email.");
            if(view.makeYesNoChoice("Do you wish to search for a host through their last name? (yes / y / no / n)")){
                String lastNamePrefix = view.getLastNamePrefix();
                List<Host> hosts = hostService.findByLastNamePrefix(lastNamePrefix);
                host = view.chooseHost(hosts);
                if(host != null) view.displayMessage(host.toString());
                return host;
            }else return null;
        }
        view.displayMessage(host.toString());
        return host;
    }

    public Guest getGuest() throws DALException {
        view.displayMessage("Choose a guest: ");
        String email = view.getEmail();
        Guest guest = guestService.findByEmail(email);
        if(guest == null) {
            view.displayMessage("No guest was found under that email.");
            if(view.makeYesNoChoice("Do you wish to search for a host through their last name? (yes / y / no / n)")){
                String lastNamePrefix = view.getLastNamePrefix();
                List<Guest> guests = guestService.findByLastNamePrefix(lastNamePrefix);
                guest = view.chooseGuest(guests);
                if(guest!= null) view.displayMessage(guest.toString());
                return guest;
            }else return null;
        }
        view.displayMessage(guest.toString());
        return guest;
    }

    public boolean validateReservations(List<Reservation> reservations) {
        if(reservations == null || reservations.size()==0) {
            view.displayMessage("This person doesn't have any reservations");
            return true;
        }
        return false;
    }
}

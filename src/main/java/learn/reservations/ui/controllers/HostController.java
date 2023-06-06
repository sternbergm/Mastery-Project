package learn.reservations.ui.controllers;

import learn.reservations.dal.DALException;
import learn.reservations.domains.PersonService;
import learn.reservations.domains.ReservationService;
import learn.reservations.domains.Result;
import learn.reservations.models.Host;
import learn.reservations.ui.View;
import learn.reservations.ui.menus.HostMenu;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HostController {

    private View view;
    private PersonService<Host> hostService;
    private ReservationService reservationService;
    private ControllerHelper helper;

    public HostController(View view, PersonService<Host> hostService, ReservationService reservationService, ControllerHelper helper) {
        this.view = view;
        this.hostService = hostService;
        this.reservationService = reservationService;
        this.helper = helper;
    }

    public void runHostMenu() throws DALException {
        HostMenu option = view.selectHostMenu();
        switch (option) {
            case VIEW_HOSTS -> viewHostsByLastName();
            case ADD_HOST -> addHost();
            case UPDATE_HOST -> updateHost();
            case DELETE_HOST -> deleteHost();
        }
    }

    private void viewHostsByLastName() throws DALException {
        view.displayHeader("View hosts by last name");
        String lastNamePrefix = view.getLastNamePrefix();
        List<Host> hosts = hostService.findByLastNamePrefix(lastNamePrefix);
        view.displayHeader("Hosts");
        if(hosts.size()==0){
            view.displayMessage("No hosts under that last name");
            view.enterToContinue();
            return;
        }
        hosts.stream().limit(50).forEach(g -> view.displayMessage(g.toString()));
        view.enterToContinue();
    }

    private void addHost() throws DALException {
        view.displayHeader("Add a new host");

        Host host = view.makeHost();
        if(host == null) {
            view.displayMessage("Host was discarded");
            view.enterToContinue();
            return;
        }
        Result<Host> result = hostService.add(host);
        handleHostResult(result, "Host %s successfully added");
        view.enterToContinue();
    }

    private void updateHost() throws DALException {
        view.displayHeader("Modify a Host");
        Host host = helper.getHost();
        if(host == null) {
            view.displayMessage("Action aborted");
            view.enterToContinue();
            return;
        }

        host = view.modifyHost(host);
        if(host == null) {
            view.displayMessage("Changes to host discarded");
            view.enterToContinue();
            return;
        }

        Result<Host> result = hostService.update(host);
        handleHostResult(result, "Host %s was updated");
        view.enterToContinue();
    }

    private void deleteHost() throws DALException {
        view.displayHeader("Remove a Host");
        Host host = helper.getHost();
        if(host == null) {
            view.displayMessage("Action aborted");
            view.enterToContinue();
            return;
        }
        int numberOfReservations = reservationService.getReservationsByHost(host.getId()).size();
        String message = String.format("Are you sure you want to delete this host? you will also delete all reservations made under their name (yes/y/no/n)\nNumber of reservations: %d", numberOfReservations);
        if(view.makeYesNoChoice(message)){
            hostService.delete(host);
            reservationService.deleteReservationByHost(host.getId());
            view.displayMessage("Host was deleted");
            view.enterToContinue();
        } else {
            view.displayMessage("Action aborted");
            view.enterToContinue();
        }
    }

    private void handleHostResult(Result<Host> result, String s) {
        if(result.isSuccessful()){
            String successMessage = String.format(s, result.getPayload().getLastName());
            view.displayStatus(true, List.of(successMessage));
        }
        else {
            view.displayStatus(false, result.getMessages());
        }
    }
}

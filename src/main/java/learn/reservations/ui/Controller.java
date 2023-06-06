package learn.reservations.ui;

import learn.reservations.dal.DALException;
import learn.reservations.ui.controllers.GuestController;
import learn.reservations.ui.controllers.HostController;
import learn.reservations.ui.controllers.ReservationController;
import learn.reservations.ui.controllers.ViewController;
import learn.reservations.ui.menus.MenuOption;
import org.springframework.stereotype.Component;

@Component
public class Controller {

    private View view;
    private ReservationController reservationController;
    private HostController hostController;
    private GuestController guestController;
    private ViewController viewController;
    public Controller(View view,
                      ReservationController reservationController,
                      HostController hostController,
                      GuestController guestController,
                      ViewController viewController){
        this.view = view;
        this.reservationController = reservationController;
        this.hostController = hostController;
        this.guestController = guestController;
        this.viewController = viewController;
    }
    public void run(){
        view.displayHeader("Welcome to the Don't Wreck my House software");
        try {
            runLoop();
        } catch (DALException ex) {
            view.displayException(ex);
        }
        view.displayHeader("See you soon!");
    }
    private void runLoop() throws DALException {
        MenuOption option;
        do {
            option = view.selectMenuOption();
            switch (option) {
                case VIEW_INFORMATION -> viewController.runViewMenu();
                case DO_RESERVATION -> reservationController.runReservationMenu();
                case DO_HOST -> hostController.runHostMenu();
                case DO_GUEST -> guestController.runGuestMenu();
            }
        } while (option != MenuOption.EXIT);
    }


}

package learn.reservations.ui;

import learn.reservations.dal.DALException;
import learn.reservations.models.Guest;
import learn.reservations.models.Host;
import learn.reservations.models.Reservation;
import learn.reservations.ui.menus.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class View {

    private TextIO io;

    public View(TextIO io) {
        this.io = io;
    }

    public MenuOption selectMenuOption() {
        displayHeader("Main Directory");
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (MenuOption option : MenuOption.values()) {
            io.print(String.format("%s. %s\n", option.getValue(), option.getMessage()));
            min = Math.min(min, option.getValue());
            max = Math.max(max, option.getValue());
        }

        String message = String.format("Select [%s-%s]: ", min, max);
        return MenuOption.fromValue(io.readInt(message, min, max));
    }

    public ViewMenu selectViewMenuOption() {
        displayHeader("Information Directory");
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (ViewMenu option : ViewMenu.values()) {
            io.print(String.format("%s. %s\n", option.getValue(), option.getMessage()));
            min = Math.min(min, option.getValue());
            max = Math.max(max, option.getValue());
        }

        String message = String.format("Select [%s-%s]: ", min, max);
        return ViewMenu.fromValue(io.readInt(message, min, max));
    }

    public ReservationMenu selectReservationMenuOption() {
        displayHeader("Reservations Menu");
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (ReservationMenu option : ReservationMenu.values()) {
            io.print(String.format("%s. %s\n", option.getValue(), option.getMessage()));
            min = Math.min(min, option.getValue());
            max = Math.max(max, option.getValue());
        }

        String message = String.format("Select [%s-%s]: ", min, max);
        return ReservationMenu.fromValue(io.readInt(message, min, max));
    }

    public HostMenu selectHostMenu() {
        displayHeader("Host Menu");
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (HostMenu option : HostMenu.values()) {
            io.print(String.format("%s. %s\n", option.getValue(), option.getMessage()));
            min = Math.min(min, option.getValue());
            max = Math.max(max, option.getValue());
        }

        String message = String.format("Select [%s-%s]: ", min, max);
        return HostMenu.fromValue(io.readInt(message, min, max));
    }

    public GuestMenu selectGuestMenu() {
        displayHeader("Guest Menu");
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (GuestMenu option : GuestMenu.values()) {
            io.print(String.format("%s. %s\n", option.getValue(), option.getMessage()));
            min = Math.min(min, option.getValue());
            max = Math.max(max, option.getValue());
        }

        String message = String.format("Select [%s-%s]: ", min, max);
        return GuestMenu.fromValue(io.readInt(message, min, max));
    }

    public void displayHeader(String s) {
        StringBuilder separator = new StringBuilder();
        separator.append("-".repeat(s.length()));
        io.println(separator.toString());
        io.println(s);
        io.println(separator.toString());
    }

    public void displayException(DALException ex) {
        io.println("Critical error at file level, contact IT");
        io.println(ex.getMessage());
    }

    public String getEmail() {
        return io.readString("What is this person's email?");
    }


    public void displayReservationsWithGuests(List<Reservation> reservations, List<Guest> guests) {
        displayHeader("Reservations");
        reservations
                .stream()
                .sorted(Comparator.comparing(Reservation::getStartDate))
                .forEach(res -> {
            String output = res.toString();
            String guest = guests.stream().filter(g -> g.getId() == res.getGuestId()).findFirst().orElse(null).toString();
            output += " "+guest;
            io.println(output);
        });
    }

    public void displayReservationsByHost(List<Reservation> reservations, List<Host> hosts) {
        displayHeader("Reservations grouped by host");
        Map<String, List<Reservation>> reservationsByHostId = reservations.stream().collect(Collectors.groupingBy(Reservation::getHostId));
        reservationsByHostId.forEach((hostId, resByHost) -> {
            Host host = hosts.stream().filter(h -> h.getId().equals(hostId)).findFirst().orElse(null);
            displayMessage(host.toString());
            displayReservations(resByHost);
        });
    }

    public void displayReservationsByHostWithGuests(List<Reservation> reservations, List<Host> hosts, List<Guest> guests) {
        displayHeader("Reservations grouped by host");
        Map<String, List<Reservation>> reservationsByHostId = reservations.stream().collect(Collectors.groupingBy(Reservation::getHostId));
        reservationsByHostId.forEach((hostId, resByHost) -> {
            Host host = hosts.stream().filter(h -> h.getId().equals(hostId)).findFirst().orElse(null);
            displayHeader(host.toString());
            displayReservationsWithGuests(resByHost, guests);
        });
    }

    public void displayMessage(String s) {
        io.println(s);
    }

    public void enterToContinue() {
        io.readString("Press enter to continue.");
    }

    public Reservation makeReservation(Host host, Guest guest, List<Reservation> reservations) {
        Reservation reservation = new Reservation();
        reservation.setGuestId(guest.getId());
        reservation.setHostId(host.getId());
        if (reservations != null) {
            reservations = reservations.stream().filter(res -> res.getStartDate().isAfter(LocalDate.now())).collect(Collectors.toList());
            displayReservations(reservations);
        }
        if (reservations == null || reservations.size()==0) {
            io.println("There are no future reservations for this host");
        }
        reservation.setStartDate(getDate(LocalDate.now(), "What is the start date of the reservation? (yyyy-mm-dd)"));
        reservation.setEndDate(getDate(reservation.getStartDate(), "What is the end date of the reservation? (yyyy-mm-dd)"));
        reservation.calculateTotal(host.getStandard_rate(), host.getWeekend_rate());
        io.println(String.format("dates: %s - %s, total: %.2f", reservation.getStartDate(), reservation.getEndDate(), reservation.getTotal()));
        if(io.readBoolean("Do you confirm these details are correct?")){
            return reservation;
        }
        return null;
    }

    public Reservation updateReservation(Reservation reservation, List<Reservation> reservations,Host host) {
        Reservation newReservation = new Reservation();
        newReservation.setGuestId(reservation.getGuestId());
        newReservation.setHostId(reservation.getHostId());
        newReservation.setId(reservation.getId());
        if (reservations != null) {
            reservations = reservations.stream().filter(res -> res.getStartDate().isAfter(LocalDate.now())).collect(Collectors.toList());
            reservations = reservations.stream().filter(res -> !res.equals(reservation)).collect(Collectors.toList());
            displayReservations(reservations);
        }
        if (reservations == null || reservations.size()==0) {
            io.println("There are no future reservations for this host");
        }
        newReservation.setStartDate(getUpdatedDate(LocalDate.now(), "What is the start date of the reservation? (old date: %s)", reservation.getStartDate()));
        newReservation.setEndDate(getUpdatedDate(newReservation.getStartDate(), "What is the end date of the reservation? (old date: %s)", reservation.getEndDate()));
        newReservation.calculateTotal(host.getStandard_rate(), host.getWeekend_rate());
        io.println(newReservation.toString());
        if(io.readBoolean("Do you confirm these details are correct?")){
            return newReservation;
        }
        return null;
    }

    private LocalDate getDate(LocalDate minDate, String prompt) {
        do{
            LocalDate date = io.readLocalDate(prompt);
            if(date.isAfter(minDate)) return date;
            io.println(String.format("Please enter a valid date, greater than %s", minDate));
        } while (true);
    }

    private LocalDate getUpdatedDate(LocalDate minDate, String prompt, LocalDate oldDate) {
        do{
            LocalDate date = io.updateLocalDate(String.format(prompt, oldDate), oldDate);
            if(date.isAfter(minDate)) return date;
            io.println(String.format("Please enter a valid date, greater than %s", minDate));
        } while (true);
    }

    private void displayReservations(List<Reservation> reservations) {
        displayHeader("Reservations");
        reservations
                .stream()
                .sorted(Comparator.comparing(Reservation::getStartDate))
                .forEach(res -> {
            String output = res.toString();
            io.println(output);
        });
    }

    public void displayStatus(boolean success, List<String> messages) {
        if(success){
            displayHeader("Successful");
        }else displayHeader("Error");
        messages.forEach(io::println);
    }

    public boolean makeYesNoChoice(String s) {
        return io.readBoolean(s);
    }

    public String getLastNamePrefix() {
        return io.readRequiredString("Last name starts with: ").toLowerCase();
    }

    public Host chooseHost(List<Host> hosts) {
        if(hosts.size() == 0){
            io.println("No hosts found");
            return null;
        }

        int index = 1;
        for (Host host : hosts.stream().limit(25).collect(Collectors.toList())) {
            io.println(String.format("%d: %s", index++, host.toString()));
        }
        index--;

        if (hosts.size() > 25) {
            io.println("More than 25 hosts found. Showing first 25. Please refine your search.");
        }
        io.println("0: Exit");
        String message = String.format("Select a host by their index [0-%s]: ", index);

        index = io.readInt(message, 0, index);
        if (index <= 0) {
            return null;
        }
        return hosts.get(index - 1);
    }

    public Guest chooseGuest(List<Guest> guests) {
        if(guests.size() == 0){
            io.println("No guests found");
            return null;
        }

        int index = 1;
        for (Guest guest : guests.stream().limit(25).collect(Collectors.toList())) {
            io.println(String.format("%d: %s", index++, guest.toString()));
        }
        index--;

        if (guests.size() > 25) {
            io.println("More than 25 guests found. Showing first 25. Please refine your search.");
        }
        io.println("0: Exit");
        String message = String.format("Select a guest by their index [0-%s]: ", index);

        index = io.readInt(message, 0, index);
        if (index <= 0) {
            return null;
        }
        return guests.get(index - 1);
    }

    public Reservation getFutureReservation(List<Reservation> reservations) {
        if (reservations != null) {
            reservations = reservations.stream().filter(res -> res.getStartDate().isAfter(LocalDate.now())).collect(Collectors.toList());
        }
        if (reservations == null || reservations.size()==0) {
            io.println("There are no future reservations for this host");
            return null;
        }

        int index = 1;
        for(Reservation reservation : reservations.stream().toList()){
            io.println(String.format("%d: %s", index++, reservation.toString()));
        }
        index--;

        io.println("0: Exit");
        String message = String.format("Select a reservation by their index [0-%s]: ", index);

        index = io.readInt(message, 0, index);
        if (index <= 0) {
            return null;
        }
        return reservations.get(index - 1);
    }


    public Host makeHost() {
        Host host = new Host();
        host.setLastName(io.readRequiredString("Enter last name"));
        host.setEmail(io.readRequiredString("Enter email"));
        host.setPhone(io.readRequiredString("Enter phone number (xxx) xxxxxxx"));
        host.setState(io.readRequiredString("Enter state"));
        host.setCity(io.readRequiredString("Enter city"));
        host.setAddress(io.readRequiredString("Enter address"));
        host.setPostal_code(io.readInt("Enter Postal code", 0, 99999));
        host.setStandard_rate(io.readPrice("Enter the standard daily rate for the property"));
        host.setWeekend_rate(io.readPrice("Enter the weekend daily rate for the property"));

        io.println(host.toString());
        if(io.readBoolean("Are you sure you want to add this host? (yes/no/y/n)")){
            return host;
        }
        return null;
    }

    public Guest makeGuest() {
        Guest guest = new Guest();
        guest.setFirstName(io.readRequiredString("Enter first name"));
        guest.setLastName(io.readRequiredString("Enter last name"));
        guest.setEmail(io.readRequiredString("Enter email"));
        guest.setPhone(io.readRequiredString("Enter phone number (xxx) xxxxxxx"));
        guest.setState(io.readRequiredString("Enter state"));

        io.println(guest.toString());
        if(io.readBoolean("Are you sure you want to add this guest? (yes/no/y/n)")){
            return guest;
        }
        return null;
    }

    public Host modifyHost(Host host) {
        Host newHost = new Host();
        newHost.setLastName(io.updateRequiredString("Enter last name (old value %s)", host.getLastName()));
        newHost.setEmail(io.updateRequiredString("Enter email (old value %s)", host.getEmail()));
        newHost.setPhone(io.updateRequiredString("Enter phone number (xxx) xxxxxxx (old value %s)", host.getPhone()));
        newHost.setState(io.updateRequiredString("Enter state (old value %s)", host.getState()));
        newHost.setCity(io.updateRequiredString("Enter city (old value %s)", host.getCity()));
        newHost.setAddress(io.updateRequiredString("Enter address (old value %s)", host.getAddress()));
        newHost.setPostal_code(io.updateInt("Enter Postal code (old value %s)", 0, 99999, host.getPostal_code()));
        newHost.setStandard_rate(io.updatePrice("Enter the standard daily rate for the property (old value %s)", host.getStandard_rate()));
        newHost.setWeekend_rate(io.updatePrice("Enter the weekend daily rate for the property (old value %s)", host.getWeekend_rate()));
        newHost.setId(host.getId());
        io.println(newHost.toString());
        if(io.readBoolean("Are you sure you want to keep these modifications? (yes/no/y/n)")){
            return newHost;
        }
        return null;
    }

    public Guest modifyGuest(Guest guest) {
        Guest newGuest = new Guest();
        newGuest.setFirstName(io.updateRequiredString("Enter first name (old value %s)", guest.getFirstName()));
        newGuest.setLastName(io.updateRequiredString("Enter last name (old value %s)", guest.getLastName()));
        newGuest.setEmail(io.updateRequiredString("Enter email (old value %s)", guest.getEmail()));
        newGuest.setPhone(io.updateRequiredString("Enter phone number (xxx) xxxxxxx (old value %s)", guest.getPhone()));
        newGuest.setState(io.updateRequiredString("Enter state (old value %s)", guest.getState()));
        newGuest.setId(guest.getId());
        io.println(newGuest.toString());
        if(io.readBoolean("Are you sure you want to keep these modifications? (yes/no/y/n)")){
            return newGuest;
        }
        return null;
    }

    public String getInfo() {
        return io.readRequiredString("Enter the state you wish to search");
    }


}

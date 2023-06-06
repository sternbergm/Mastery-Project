package learn.reservations.domains;

import learn.reservations.dal.DALException;
import learn.reservations.models.Host;
import learn.reservations.models.Reservation;

import java.util.List;

public interface ReservationService {
    List<Reservation> getReservationsByHost(String hostId) throws DALException;

    List<Reservation> getReservationsByGuest(int guestId) throws DALException;

    Result<Reservation> makeReservation(String hostId, Reservation reservation) throws DALException;

    void deleteReservationsByGuest(int guestId) throws DALException;

    void deleteReservationByHost(String hostId) throws DALException;

    void validateReservationDates(Result<Reservation> result, Reservation reservation, String hostId) throws DALException;

    Result<Reservation> updateReservation(String hostId, int id, Reservation reservation) throws DALException;
    void deleteReservation(String hostId, int id) throws DALException;

    List<Reservation> getReservationsByHosts(List<Host> hostsPerState);
}

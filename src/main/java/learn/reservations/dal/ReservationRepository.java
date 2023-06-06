package learn.reservations.dal;

import learn.reservations.models.Reservation;

import java.util.List;

public interface ReservationRepository{


    List<Reservation> readByHostId(String hostId) throws DALException;

    List<Reservation> readByGuestId(int guestId) throws DALException;
    Reservation create(String hostId, Reservation reservation) throws DALException;
    void update(String hostId, int id, Reservation reservation) throws DALException;
    void delete(String hostId, int id) throws DALException;

}

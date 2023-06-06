package learn.reservations.dal;

import learn.reservations.models.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReservationMapperTest {

    ObjectMapper<Reservation> mapper;
    String reservationString;
    Reservation reservationObject;

    @BeforeEach
    void setUp() {
        mapper = new ReservationMapper();

        reservationString = "2,2021-09-10,2021-09-16,136,1300.00";
        reservationObject = new Reservation();
        reservationObject.setId(2);
        reservationObject.setStartDate(LocalDate.parse("2021-09-10"));
        reservationObject.setEndDate(LocalDate.parse("2021-09-16"));
        reservationObject.setGuestId(136);
        reservationObject.setTotal(BigDecimal.valueOf(1300));
    }

    @Test
    void serialize() {
        //assign
        String actual = "";
        //
        actual = mapper.serialize(reservationObject);
        //
        assertEquals(reservationString, actual);
    }

    @Test
    void deserialize() {
        //
        Reservation actual = new Reservation();
        //
        actual = mapper.deserialize(reservationString);
        //
        assertEquals(reservationObject, actual);
    }
}
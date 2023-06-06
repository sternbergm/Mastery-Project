package learn.reservations.dal;

import learn.reservations.models.Guest;
import learn.reservations.models.Reservation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservationRepositoryImplTest {

    ReservationRepository repository;
    String hostId;

    List<Reservation> original;
    @BeforeEach
    void setUp() {
        ObjectMapper<Reservation> mapper = new ReservationMapper();
        repository = new ReservationRepositoryImpl("data/test_data/test_reservations", mapper);
        hostId = "2e72f86c-b8fe-4265-b4f1-304dea8762db";
        original = safeRead();
    }

    @AfterEach
    void tearDown(){
        Path testPath = Paths.get("data/test_data/test_reservations", hostId+".csv");
        Path seedPath = Paths.get("data/test_data", "seed-2e72f86c.csv");
        try {
            Files.copy(seedPath, testPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("Error copying");
        }
    }

    @Test
    void testReadByHostId() {
        //
        int expectedSize = 13;
        List<Reservation> reservations = new ArrayList<>();
        //
        reservations = safeRead();
        //
        assertEquals(expectedSize, reservations.size());
        reservations.forEach(Assertions::assertNotNull);
        reservations.forEach(res -> assertEquals(hostId, res.getHostId()));
    }


    @Test
    void testCreate(){
        Reservation reservation = new Reservation();
        reservation.setStartDate(LocalDate.parse("2023-09-10"));
        reservation.setEndDate(LocalDate.parse("2023-09-16"));
        reservation.setGuestId(136);
        reservation.setHostId("2e72f86c-b8fe-4265-b4f1-304dea8762db");
        reservation.setTotal(BigDecimal.valueOf(1300));
        //
        try {
            repository.create(hostId, reservation);
        } catch (DALException e) {
            System.out.println("error with create method");
        }
        //
        List<Reservation> actual = safeRead();
        assertEquals(14, actual.size());
        actual.forEach(res -> assertEquals(hostId, res.getHostId()));
    }

    @Test
    void update() {
        Reservation reservation = new Reservation();
        reservation.setStartDate(LocalDate.parse("2023-09-10"));
        reservation.setEndDate(LocalDate.parse("2023-09-16"));
        reservation.setGuestId(136);
        reservation.setId(1);
        reservation.setHostId("2e72f86c-b8fe-4265-b4f1-304dea8762db");
        reservation.setTotal(BigDecimal.valueOf(1300));
        //
        try{
            repository.update(hostId, 1, reservation);
        } catch (DALException e) {
            System.out.println("Error updating");
        }
        //
        List<Reservation> actual = safeRead();
        assertEquals(reservation, actual.get(0));
        assertEquals(original.size(), actual.size());
    }



    @Test
    void testDelete() {
        List<Reservation> reservation = safeRead();
        //
        try {
            repository.delete(hostId, 1);
        } catch (DALException e) {
            System.out.println("error deleting");
        }
        //
        List<Reservation> actual = safeRead();
        assertNotEquals(reservation, actual);
        assertEquals(12, actual.size());
    }

    @Test
    void testDeleteWrongId() {
        List<Reservation> reservation = original;
        //
        try {
            repository.delete(hostId, 15);
        } catch (DALException e) {
            System.out.println("error deleting");
        }
        //
        List<Reservation> actual = safeRead();
        assertEquals(reservation, actual);
        assertEquals(13, actual.size());
    }

    private List<Reservation> safeRead() {
        List<Reservation> actual = new ArrayList<>();
        try {
            actual = repository.readByHostId(hostId);
        } catch (DALException e) {
            System.out.println("error reading");
        }
        return actual;
    }

    @Test
    void testReadByGuestId() {
        int guestId = 1001;
        List<Reservation> actual = new ArrayList<>();
        //
        try {
            actual = repository.readByGuestId(guestId);
        } catch (DALException e) {
            System.out.println("Error reading by guest");
        }
        //
        assertEquals(1, actual.size());
        assertEquals(guestId, actual.get(0).getGuestId());

    }
}
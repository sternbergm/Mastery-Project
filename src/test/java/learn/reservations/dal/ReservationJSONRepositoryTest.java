package learn.reservations.dal;

import com.fasterxml.jackson.databind.ObjectMapper;
import learn.reservations.models.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static org.junit.jupiter.api.Assertions.*;

class ReservationJSONRepositoryTest {

    ReservationRepository repository;
    ObjectMapper mapper;

    List<Reservation> reservations;
    String hostId;

    Reservation original;
    Reservation testReservation;
    @BeforeEach
    void setUp(){
        String fileName = "./data/test_data/reservations.json";
        mapper = new ObjectMapper().registerModule(new JavaTimeModule());;
        repository = new ReservationJSONRepository(fileName);
        ReservationMapper resMapper = new ReservationMapper();
        original = resMapper.deserialize("11,2023-12-05,2023-12-10,735,1100.00");
        testReservation = resMapper.deserialize("12,2023-12-20,2023-12-22,735,11000.00");
        reservations = Arrays.stream(new Reservation[]{original}).toList();
        hostId = "2e72f86c-b8fe-4265-b4f1-304dea8762db";
        original.setHostId(hostId);
        testReservation.setHostId(hostId);
        Map<String, List<Reservation>> map = new HashMap<>();
        map.put(hostId, reservations);
        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException ex) {
            System.out.println("Unable to create file");
        }
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, map);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    @Test
    void readByHostId() {
        //
        List<Reservation> actual = null;
        //
        actual = safeRead();
        //
        assertNotNull(actual);
        assertEquals(reservations, actual);
        assertEquals(original, actual.get(0));
    }

    private List<Reservation> safeRead() {
        try {
            return repository.readByHostId(hostId);
        } catch (DALException e) {
            System.out.println("Error reading by host");
        }
        return null;
    }

    @Test
    void testCreate() {
        Reservation actual = null;
        //
        try {
            actual = repository.create(hostId, testReservation);
        } catch (DALException e) {
            System.out.println("Error adding to repository");
        }
        //
        List<Reservation> actualList = safeRead();
        assertEquals(2, actualList.size());
        assertNotEquals(reservations, actualList);
        assertEquals(testReservation, actual);
        assertEquals(actual.toString(), actualList.get(1).toString());
    }

    @Test
    void testUpdate() {
        testReservation.setId(11);
        //
        try {
            repository.update(hostId, 11, testReservation);
        } catch (DALException e) {
            System.out.println("Error adding to repository");
        }
        //
        List<Reservation> actualList = safeRead();
        assertEquals(1, actualList.size());
        assertEquals(testReservation.toString(), actualList.get(0).toString());
    }

    @Test
    void testUpdateWrongId() {
        testReservation.setId(11);
        //
        try {
            repository.update(hostId, 15, testReservation);
        } catch (DALException e) {
            System.out.println("Error adding to repository");
        }
        //
        List<Reservation> actualList = safeRead();
        assertEquals(1, actualList.size());
        assertNotEquals(testReservation.toString(), actualList.get(0).toString());
    }

    @Test
    void testDelete() {
        try {
            repository.create(hostId, testReservation);
        } catch (DALException e) {
            System.out.println("error adding");
        }
        //
        try {
            repository.delete(hostId, 12);
        } catch (DALException e) {
            System.out.println("Error adding to repository");
        }
        //
        List<Reservation> actualList = safeRead();
        assertEquals(1, actualList.size());
        assertEquals(original, actualList.get(0));
    }

    @Test
    void testDeleteAFullHost() {
        try {
            repository.create("ExampleHost", testReservation);
        } catch (DALException e) {
            System.out.println("error adding");
        }
        //
        try {
            repository.delete("ExampleHost", 1);
        } catch (DALException e) {
            System.out.println("Error adding to repository");
        }
        //
        List<Reservation> actualList = null;
        try {
            actualList = repository.readByHostId("ExampleHost");
        } catch (Exception e) {
            System.out.println("Error reading empty host");
        }
        assertNotNull(actualList);
        assertEquals(0, actualList.size());
    }

    @Test
    void testGetByGuest() {
        //
        int guestId = 735;
        List<Reservation> actual = new ArrayList<>();
        //
        try {
            actual = repository.readByGuestId(guestId);
        } catch (DALException e) {
            System.out.println("Error reading by Guest");
        }
        //
        assertEquals(1, actual.size());
        assertEquals(guestId, actual.get(0).getGuestId());

    }

    @Test
    void testGetByGuestDifferentHost() {
        //
        int guestId = 735;
        testReservation.setHostId("testHost");
        testReservation.setGuestId(guestId);
        List<Reservation> actual = new ArrayList<>();
        try {
            repository.create("testHost", testReservation);
        } catch (DALException e) {
            System.out.println("Error creating new host");
        }
        //
        try {
            actual = repository.readByGuestId(guestId);
        } catch (DALException e) {
            System.out.println("Error reading by Guest");
        }
        //
        assertEquals(2, actual.size());
        actual.forEach(res -> assertEquals(guestId, res.getGuestId()));

    }

    @Test
    void testGetByGuestDifferentHostExtraGuest() {
        //
        int guestId = 737;
        testReservation.setHostId("testHost");
        testReservation.setGuestId(guestId);
        try {
            repository.create("testHost", testReservation);
        } catch (DALException e) {
            System.out.println("Error creating new host");
        }
        guestId = 735;
        testReservation.setHostId("testHost");
        testReservation.setGuestId(guestId);
        List<Reservation> actual = new ArrayList<>();
        try {
            repository.create("testHost", testReservation);
        } catch (DALException e) {
            System.out.println("Error creating new host");
        }
        //
        try {
            actual = repository.readByGuestId(guestId);
        } catch (DALException e) {
            System.out.println("Error reading by Guest");
        }
        //
        assertEquals(2, actual.size());
        int finalGuestId = guestId;
        actual.forEach(res -> assertEquals(finalGuestId, res.getGuestId()));

    }
}
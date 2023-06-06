package learn.reservations.domains;

import learn.reservations.dal.*;
import learn.reservations.models.Guest;
import learn.reservations.models.Host;
import learn.reservations.models.Reservation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceImplTest {

    ReservationRepository repository;
    ObjectMapper<Reservation> mapper;
    ReservationServiceImpl service;

    String hostId = "2e72f86c-b8fe-4265-b4f1-304dea8762db";

    List<Reservation> original;

    @BeforeEach
    void setUp() {
        mapper = new ReservationMapper();
        repository = new ReservationRepositoryImpl("data/test_data/test_reservations", mapper);
        ObjectMapper<Guest> guestMapper = new GuestMapper();
        Repository<Guest> guestRepo = new GuestRepository("data/test_data/test_guests.csv", guestMapper, "guest_id,first_name,last_name,email,phone,state");
        ObjectMapper<Host> hostMapper = new HostMapper();
        Repository<Host> hostRepo = new HostRepository("data/test_data/test_hosts.csv", hostMapper, "id,last_name,email,phone,address,city,state,postal_code,standard_rate,weekend_rate");

        service = new ReservationServiceImpl(repository, guestRepo, hostRepo);

        original = safeRead();
    }

    private List<Reservation> safeRead() {
        try {
            return repository.readByHostId(hostId);
        } catch (DALException e) {
            System.out.println("Error reading");
        }
        return null;
    }

    @AfterEach
    void tearDown() {
        Path testPath = Paths.get("data/test_data/test_reservations", hostId+".csv");
        Path seedPath = Paths.get("data/test_data", "seed-2e72f86c.csv");
        try {
            Files.copy(seedPath, testPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("Error copying");
        }
    }

    @Test
    void getReservationsByHost() {
        List<Reservation> expected = new ArrayList<>();
        List<Reservation> actual = new ArrayList<>();
        //
        try {
            expected = safeRead();
            actual = service.getReservationsByHost(hostId);
        } catch (DALException e) {
            System.out.println("Error reading");
        }
        //
        assertEquals(expected, actual);
    }

    @Test
    void testMakeReservation() {
        Reservation testReservation = mapper.deserialize("25,2023-12-05,2023-12-10,735,1100.00");
        testReservation.setHostId(hostId);
        Result<Reservation> result = new Result<>();
        //
        try{
            result = service.makeReservation(hostId, testReservation);
        } catch (DALException e) {
            System.out.println("Error making reservation");
        }
        //
        List<Reservation> expected = safeRead();
        assertTrue(result.isSuccessful());
        assertEquals(14, expected.size());
    }

    @Test
    void testMakeReservationNoHostId() {
        Reservation testReservation = mapper.deserialize("25,2023-12-05,2023-12-10,735,1100.00");
        Result<Reservation> result = new Result<>();
        //
        try{
            result = service.makeReservation(hostId, testReservation);
        } catch (DALException e) {
            System.out.println("Error making reservation");
        }
        //
        List<Reservation> expected = safeRead();
        assertFalse(result.isSuccessful());
        assertEquals(13, expected.size());
        assertNull(result.getPayload());
        assertEquals("Reservation does not have valid host Id", result.getMessages().get(0));
    }

    @Test
    void testMakeReservationNonExistentHostId() {
        Reservation testReservation = mapper.deserialize("25,2023-12-05,2023-12-10,735,1100.00");
        testReservation.setHostId("MyHost");
        Result<Reservation> result = new Result<>();
        //
        try{
            result = service.makeReservation(hostId, testReservation);
        } catch (DALException e) {
            System.out.println("Error making reservation");
        }
        //
        List<Reservation> expected = safeRead();
        assertFalse(result.isSuccessful());
        assertEquals(13, expected.size());
        assertNull(result.getPayload());
        assertEquals("Error finding Host in host list", result.getMessages().get(0));
    }

    @Test
    void testMakeReservationInvalidHostId() {
        Reservation testReservation = mapper.deserialize("25,2023-12-05,2023-12-10,735,1100.00");
        testReservation.setHostId("66457e70-96ff-4086-b62c-2d7280844bce");
        Result<Reservation> result = new Result<>();
        //
        try{
            result = service.makeReservation(hostId, testReservation);
        } catch (DALException e) {
            System.out.println("Error making reservation");
        }
        //
        List<Reservation> expected = safeRead();
        assertFalse(result.isSuccessful());
        assertEquals(13, expected.size());
        assertNull(result.getPayload());
        assertEquals("Host Id provided does not match reservation host ID", result.getMessages().get(0));
    }

    @Test
    void testMakeReservationNonExistentGuestId() {
        Reservation testReservation = mapper.deserialize("25,2023-12-05,2023-12-10,13555,1100.00");
        testReservation.setHostId(hostId);
        Result<Reservation> result = new Result<>();
        //
        try{
            result = service.makeReservation(hostId, testReservation);
        } catch (DALException e) {
            System.out.println("Error making reservation");
        }
        //
        List<Reservation> expected = safeRead();
        assertFalse(result.isSuccessful());
        assertEquals(13, expected.size());
        assertNull(result.getPayload());
        assertEquals("Error finding Guest in guest list", result.getMessages().get(0));
    }

    @Test
    void testUpdateReservationValid() {
        Reservation testReservation = mapper.deserialize("11,2023-12-05,2023-12-10,735,1100.00");
        testReservation.setHostId(hostId);
        int reservationId = 11;
        Result<Reservation> result = new Result<>();
        //
        try{
            result = service.updateReservation(hostId, reservationId, testReservation);
        } catch (DALException e) {
            System.out.println("Error updating");
        }
        //
        assertTrue(result.isSuccessful());
        assertEquals(testReservation, result.getPayload());
        List<Reservation> actual = safeRead();
        assertEquals(13, actual.size());
    }

    @Test
    void testUpdateReservationValidWithOverlappingDates() {
        Reservation testReservation = mapper.deserialize("1,2023-10-05,2023-12-10,735,1100.00");
        testReservation.setHostId(hostId);
        int reservationId = 1;
        Result<Reservation> result = new Result<>();
        //
        try{
            result = service.updateReservation(hostId, reservationId, testReservation);
        } catch (DALException e) {
            System.out.println("Error updating");
        }
        //
        assertTrue(result.isSuccessful());
        assertEquals(testReservation, result.getPayload());
        List<Reservation> actual = safeRead();
        assertEquals(13, actual.size());
    }

    @Test
    void testUpdateReservationInvalidIdError() {
        Reservation testReservation = mapper.deserialize("12,2023-12-05,2023-12-10,735,1100.00");
        testReservation.setHostId(hostId);
        int reservationId = 11;
        Result<Reservation> result = new Result<>();
        //
        try{
            result = service.updateReservation(hostId, reservationId, testReservation);
        } catch (DALException e) {
            System.out.println("Error updating");
        }
        //
        assertFalse(result.isSuccessful());
        assertNull(result.getPayload());
        List<Reservation> actual = safeRead();
        assertEquals(original, actual);
        assertEquals("Reservation Id provided does not match the provided Id to update", result.getMessages().get(0));
    }

    @Test
    void testUpdateReservationInvalidHostIdError() {
        Reservation testReservation = mapper.deserialize("11,2023-12-05,2023-12-10,735,1100.00");
        testReservation.setHostId(hostId+"hi");
        int reservationId = 11;
        Result<Reservation> result = new Result<>();
        //
        try{
            result = service.updateReservation(hostId, reservationId, testReservation);
        } catch (DALException e) {
            System.out.println("Error updating");
        }
        //
        assertFalse(result.isSuccessful());
        assertNull(result.getPayload());
        List<Reservation> actual = safeRead();
        assertEquals(original, actual);
        assertEquals("Error finding Host in host list", result.getMessages().get(0));
    }


    @Test
    void testDeleteValidReservation() {
        int reservationId = 1;
        //
        try{
            service.deleteReservation(hostId, reservationId);
        } catch (DALException e) {
            System.out.println("Error deleting");
        }
        //
        List<Reservation> actual = safeRead();
        assertNotEquals(original, actual);
        assertEquals(12, actual.size());
        assertFalse(actual.stream().anyMatch(r -> r.getId()==(reservationId)));
    }
    @Test
    void testDeletePastReservation() {
        int reservationId = 11;
        //
        try{
            service.deleteReservation(hostId, reservationId);
        } catch (DALException e) {
            System.out.println("Error deleting");
        }
        //
        List<Reservation> actual = safeRead();
        assertEquals(original, actual);
        assertTrue(actual.stream().anyMatch(r -> r.getId()==(reservationId)));
    }

    @Test
    void testValidateReservationDatesWithValidDate() {
        Reservation testReservation = mapper.deserialize("14,2023-12-05,2023-12-10,735,1100.00");
        Result<Reservation> result = new Result<>();
        //
        try {
            service.validateReservationDates(result, testReservation, hostId);
        } catch (DALException e) {
            System.out.println("Error reading");
        }
        //
        assertTrue(result.isSuccessful());
    }

    @Test
    void testValidateReservationDatesWithInvalidDateStartsBeforeNow() {
        Reservation testReservation = mapper.deserialize("14,2022-12-05,2023-12-10,735,1100.00");
        Result<Reservation> result = new Result<>();
        //
        try {
            service.validateReservationDates(result, testReservation, hostId);
        } catch (DALException e) {
            System.out.println("Error reading");
        }
        //
        assertFalse(result.isSuccessful());
        assertEquals("Error with dates, please review", result.getMessages().get(0));
    }

    @Test
    void testValidateReservationDatesWithInvalidDateEndBeforeStart() {
        Reservation testReservation = mapper.deserialize("14,2023-12-05,2023-11-10,735,1100.00");
        Result<Reservation> result = new Result<>();
        //
        try {
            service.validateReservationDates(result, testReservation, hostId);
        } catch (DALException e) {
            System.out.println("Error reading");
        }
        //
        assertFalse(result.isSuccessful());
        assertEquals("Error with dates, please review", result.getMessages().get(0));
    }

    @Test
    void testValidateReservationDatesWithInvalidDateOverlap() {
        Reservation testReservation = mapper.deserialize("14,2023-06-05,2023-12-10,735,1100.00");
        Result<Reservation> result = new Result<>();
        //
        try {
            service.validateReservationDates(result, testReservation, hostId);
        } catch (DALException e) {
            System.out.println("Error reading");
        }
        //
        assertFalse(result.isSuccessful());
        assertEquals("Error with dates, there is an existing reservation in between both of these dates", result.getMessages().get(0));
    }

    @Test
    void testValidateReservationDatesWithInvalidDateStartDateOverlap() {
        Reservation testReservation = mapper.deserialize("14,2023-10-11,2023-12-10,735,1100.00");
        Result<Reservation> result = new Result<>();
        //
        try {
            service.validateReservationDates(result, testReservation, hostId);
        } catch (DALException e) {
            System.out.println("Error reading");
        }
        //
        assertFalse(result.isSuccessful());
        assertEquals("Error with start date, overlap with another reservation", result.getMessages().get(0));
    }

    @Test
    void testValidateReservationDatesWithInvalidDateEndDateOverlap() {
        Reservation testReservation = mapper.deserialize("14,2023-07-11,2023-10-10,735,1100.00");
        Result<Reservation> result = new Result<>();
        //
        try {
            service.validateReservationDates(result, testReservation, hostId);
        } catch (DALException e) {
            System.out.println("Error reading");
        }
        //
        assertFalse(result.isSuccessful());
        assertEquals("Error with end date, overlap with another reservation", result.getMessages().get(0));
    }

    @Test
    void testGetReservationByGuestId() {
        int guestId = 1001;
        List<Reservation> actual = new ArrayList<>();
        //
        try {
            actual = service.getReservationsByGuest(guestId);
        } catch (DALException e) {
            System.out.println("Error reading by guest");
        }
        //
        assertEquals(1, actual.size());
        assertEquals(guestId, actual.get(0).getGuestId());
    }

    @Test
    void testDeleteByGuestId() {
        int guestId = 1001;
        //
        try {
            service.deleteReservationsByGuest(guestId);
        } catch (DALException e) {
            System.out.println("Error deleting by guest");
        }
        //
        List<Reservation> actual = new ArrayList<>();
        try {
             actual = service.getReservationsByGuest(guestId);
        } catch (DALException e) {
            System.out.println("error reading by guest");
        }
        assertEquals(0, actual.size());
        List<Reservation> byHost = safeRead();
        assertNotEquals(original, byHost);
    }

    @Test
    void testDeleteByHostId() {
        //
        try {
            service.deleteReservationByHost(hostId);
        } catch (DALException e) {
            System.out.println("Error deleting by guest");
        }
        //
        List<Reservation> actual = safeRead();
        assertEquals(0, actual.size());
        assertNotEquals(original, actual);
    }

}
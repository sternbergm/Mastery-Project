package learn.reservations.dal;

import learn.reservations.models.Guest;
import learn.reservations.models.Host;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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

class GuestRepositoryTest {

    Repository<Guest> repository;
    
    List<Guest> original;


    ObjectMapper<Guest> mapper;

    @BeforeEach
    void setUp() {
        mapper = new GuestMapper();
        repository = new GuestRepository("data/test_data/test_guests.csv", mapper, "guest_id,first_name,last_name,email,phone,state");
        original = safeRead();
    }

    private List<Guest> safeRead() {
        try {
            return repository.readAll();
        } catch (DALException e) {
            System.out.println("Error reading");
        }
        return null;
    }

    @AfterEach
    void tearDown() {
        Path testPath = Paths.get("data/test_data/test_guests.csv");
        Path seedPath = Paths.get("data/test_data/seed_guests.csv");
        try {
            Files.copy(seedPath, testPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("Error copying");
        }
    }


    @Test
    void testReadAll() {
        //Assign
        List<Guest> guests = new ArrayList<>();
        int expected = 1000;
        //Act
        guests = safeRead();
        //Assert
        assertEquals(expected, guests.size());
        guests.forEach(Assertions::assertNotNull);
    }

    @Test
    void testCreate() {
        Guest newGuest = mapper.deserialize("0,James,Bond,James@Bond.gov.au,(915) 5895326,TX");
        Guest actualGuest = new Guest();
        //
        try {
            actualGuest = repository.create(newGuest);
        } catch (DALException e) {
            System.out.println("Error creating");
        }
        //
        List<Guest> actual = safeRead();
        assertNotEquals(original, actual);
        assertEquals(1001, actual.size());
        assertEquals(newGuest, actualGuest);
        assertEquals(1001, actualGuest.getId());
        assertTrue(actualGuest.getId()>0);
    }

    @Test
    void testUpdate() {
        Guest newGuest = mapper.deserialize("-1,James,Bond,James@Bond.gov.au,(915) 5895326,TX");
        newGuest.setId(original.get(0).getId());
        //
        try {
            repository.update(newGuest);
        } catch (DALException e) {
            System.out.println("Error updating");
        }
        //
        List<Guest> actual = safeRead();
        assertEquals(1000, actual.size());
        assertEquals(newGuest, actual.get(0));
    }

    @Test
    void testUpdateWrongId() {
        Guest newGuest = mapper.deserialize("-10,James,Bond,James@Bond.gov.au,(915) 5895326,TX");
        //
        try {
            repository.update(newGuest);
        } catch (DALException e) {
            System.out.println("Error updating");
        }
        //
        List<Guest> actual = safeRead();
        assertEquals(1000, actual.size());
        assertNotEquals(newGuest, actual.get(0));
    }

    @Test
    void testDelete() {
        Guest guestToDelete = original.get(0);
        //
        try {
            repository.delete(guestToDelete);
        } catch (DALException e) {
            System.out.println("Error deleting");
        }
        //
        List<Guest> actual = safeRead();
        assertEquals(999, actual.size());
        assertNotEquals(guestToDelete, actual.get(0));
        assertNotEquals(original, actual);
    }

    @Test
    void testDeleteObjectNotInList() {
        Guest guestToDelete = new Guest();
        //
        try {
            repository.delete(guestToDelete);
        } catch (DALException e) {
            System.out.println("Error deleting");
        }
        //
        List<Guest> actual = safeRead();
        assertEquals(1000, actual.size());
        assertEquals(original, actual);
    }



}
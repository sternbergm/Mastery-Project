package learn.reservations.domains;

import learn.reservations.dal.*;
import learn.reservations.models.Guest;
import learn.reservations.models.Guest;
import learn.reservations.models.Host;
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

class GuestServiceTest {

    PersonService<Guest> service;
    ObjectMapper<Guest> mapper;

    Repository<Guest> repository;
    List<Guest> original;

    @BeforeEach
    void setUp() {
        mapper = new GuestMapper();
        repository = new GuestRepository("data/test_data/test_guests.csv", mapper, "guest_id,first_name,last_name,email,phone,state");
        service = new GuestService(repository);
        original = safeRead();
    }

    private List<Guest> safeRead(){
        try {
            return service.getAll();
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
    void testFindByEmail() {
        Guest expected = mapper.deserialize("16,Christiana,Oluwatoyin,coluwatoyinf@hubpages.com,(765) 6082986,IN");
        String testEmail = expected.getEmail();
        Guest actual = new Guest();
        //
        try {
            actual = service.findByEmail(testEmail);
        } catch (DALException e) {
            System.out.println("error reading");
        }
        //
        assertEquals(expected, actual);
    }

    @Test
    void getAll() {
        List<Guest> expected = new ArrayList<>();
        List<Guest> actual = new ArrayList<>();
        //
        try {
            expected = repository.readAll();
            actual = safeRead();
        } catch (DALException e) {
            System.out.println("Error reading");
        }
        //
        assertEquals(expected, actual);
    }

    @Test
    void testAddValid() {
        Guest guest = mapper.deserialize("16,Christiana,Oluwatoyin,newEmail@newEmail.com,(765) 6082986,IN");
        Result<Guest> result = new Result<>();
        //
        try {
            result = service.add(guest);
        } catch (DALException e) {
            System.out.println("Error adding");
        }
        //
        List<Guest> actual = safeRead();
        assertNotEquals(original, actual);
        assertEquals(1001, actual.size());
        assertTrue(result.isSuccessful());
        assertEquals(guest, result.getPayload());

    }

    @Test
    void testAddDuplicate() {
        Guest guest = mapper.deserialize("16,Christiana,Oluwatoyin,coluwatoyinf@hubpages.com,(765) 6082986,IN");
        Result<Guest> result = new Result<>();
        //
        try {
            result = service.add(guest);
        } catch (DALException e) {
            System.out.println("Error adding");
        }
        //
        List<Guest> actual = safeRead();
        assertEquals(original, actual);
        assertEquals(1000, actual.size());
        assertFalse(result.isSuccessful());
        assertNull(result.getPayload());
        result.getMessages().forEach(System.out::println);
    }

    @Test
    void testUpdateValid() {
        Guest guest = mapper.deserialize("16,Christiana,Oluwatoyin,newEmail@newEmail.com,(765) 6082986,IN");
        Result<Guest> result = new Result<>();
        //
        try {
            result = service.update(guest);
        } catch (DALException e) {
            System.out.println("Error updating");
        }
        //
        List<Guest> actual = safeRead();
        assertEquals(original, actual);
        assertEquals(1000, actual.size());
        assertTrue(result.isSuccessful());
        assertEquals(guest, result.getPayload());

    }

    @Test
    void testUpdateDuplicateWrongEmail() {
        Guest guest = mapper.deserialize("16,Christiana,Oluwatoyin,aoverellk@w3.org,(765) 6082986,IN");
        Result<Guest> result = new Result<>();
        //
        try {
            result = service.update(guest);
        } catch (DALException e) {
            System.out.println("Error updating");
        }
        //
        List<Guest> actual = safeRead();
        assertEquals(original, actual);
        assertEquals(1000, actual.size());
        assertFalse(result.isSuccessful());
        assertNull(result.getPayload());
    }

    @Test
    void testDelete() {
        Guest host = mapper.deserialize("16,Christiana,Oluwatoyin,aoverellk@w3.org,(765) 6082986,IN");
        //
        try {
            service.delete(host);
        } catch (DALException e) {
            System.out.println("Error deleting");
        }
        //
        List<Guest> actual = safeRead();
        assertNotEquals(original, actual);
        assertEquals(999, actual.size());
    }
}
package learn.reservations.domains;

import learn.reservations.dal.*;
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

class HostServiceTest {
    PersonService<Host> service;
    ObjectMapper<Host> mapper;

    Repository<Host> repository;

    List<Host> original;

    @BeforeEach
    void setUp() {
        mapper = new HostMapper();
        repository = new HostRepository("data/test_data/test_hosts.csv", mapper, "id,last_name,email,phone,address,city,state,postal_code,standard_rate,weekend_rate");
        service = new HostService(repository);
        original = safeRead();
    }

    private List<Host> safeRead(){
        try {
            return service.getAll();
        } catch (DALException e) {
            System.out.println("Error reading");
        }
        return null;
    }

    @AfterEach
    void tearDown() {
        Path testPath = Paths.get("data/test_data/test_hosts.csv");
        Path seedPath = Paths.get("data/test_data/seed_hosts.csv");
        try {
            Files.copy(seedPath, testPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("Error copying");
        }
    }


    @Test
    void findByEmail() {
        Host expected = mapper.deserialize("fc92b5f3-3e59-4fea-a818-c00aa4c0d9ba,Putman,jputmandh@trellian.com,(432) 1135676,52553 Roxbury Parkway,Midland,TX,79710,351,438.75");
        String testEmail = expected.getEmail();
        Host actual = new Host();
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
        List<Host> expected = new ArrayList<>();
        List<Host> actual = new ArrayList<>();
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
        Host host = mapper.deserialize(",Putman,newEmail@newEmail.com,(432) 1135676,52553 Roxbury Parkway,Midland,TX,79710,351,438.75");
        Result<Host> result = new Result<>();
        //
        try {
            result = service.add(host);
        } catch (DALException e) {
            System.out.println("Error adding");
        }
        //
        List<Host> actual = safeRead();
        assertNotEquals(original, actual);
        assertEquals(1001, actual.size());
        assertTrue(result.isSuccessful());
        assertEquals(host, result.getPayload());

    }

    @Test
    void testAddDuplicate() {
        Host host = mapper.deserialize(",Putman,jputmandh@trellian.com,(432) 1135676,52553 Roxbury Parkway,Midland,TX,79710,351,438.75");
        Result<Host> result = new Result<>();
        //
        try {
            result = service.add(host);
        } catch (DALException e) {
            System.out.println("Error adding");
        }
        //
        List<Host> actual = safeRead();
        assertEquals(original, actual);
        assertEquals(1000, actual.size());
        assertFalse(result.isSuccessful());
        assertNull(result.getPayload());
    }

    @Test
    void testUpdateValid() {
        Host host = mapper.deserialize(",Putman,newEmail@newEmail.com,(432) 1135676,52553 Roxbury Parkway,Midland,TX,79710,351,438.75");
        host.setId("fc92b5f3-3e59-4fea-a818-c00aa4c0d9ba");
        Result<Host> result = new Result<>();
        //
        try {
            result = service.update(host);
        } catch (DALException e) {
            System.out.println("Error updating");
        }
        //
        List<Host> actual = safeRead();
        assertEquals(original, actual);
        assertEquals(1000, actual.size());
        assertTrue(result.isSuccessful());
        assertEquals(host ,result.getPayload());
    }

    @Test
    void testUpdateDuplicateInvalidEmail() {
        Host host = mapper.deserialize(",Putman,egrogerg@altervista.org,(432) 1135676,52553 Roxbury Parkway,Midland,TX,79710,351,438.75");
        host.setId("fc92b5f3-3e59-4fea-a818-c00aa4c0d9ba");
        Result<Host> result = new Result<>();
        //
        try {
            result = service.update(host);
        } catch (DALException e) {
            System.out.println("Error updating");
        }
        //
        List<Host> actual = safeRead();
        assertEquals(original, actual);
        assertEquals(1000, actual.size());
        assertFalse(result.isSuccessful());
        assertNull(result.getPayload());
    }

    @Test
    void testDelete() {
        Host host = mapper.deserialize("fc92b5f3-3e59-4fea-a818-c00aa4c0d9ba,Putman,jputmandh@trellian.com,(432) 1135676,52553 Roxbury Parkway,Midland,TX,79710,351,438.75");
        //
        try {
            service.delete(host);
        } catch (DALException e) {
            System.out.println("Error deleting");
        }
        //
        List<Host> actual = safeRead();
        assertNotEquals(original, actual);
        assertEquals(999, actual.size());
    }

    @Test
    void findByState() {
        Host host = mapper.deserialize("fc92b5f3-3e59-4fea-a818-c00aa4c0d9ba,Putman,jputmandh@trellian.com,(432) 1135676,52553 Roxbury Parkway,Midland,TX,79710,351,438.75");
        List<Host> hostsInTexas = new ArrayList<>();
        //
        try {
            hostsInTexas = service.findByState("tx");
        } catch (DALException e) {
            System.out.println("Error reading by state");
        }
        //
        assertTrue(hostsInTexas.contains(host));
    }

    @Test
    void findByCity() {
        Host host = mapper.deserialize("fc92b5f3-3e59-4fea-a818-c00aa4c0d9ba,Putman,jputmandh@trellian.com,(432) 1135676,52553 Roxbury Parkway,Midland,TX,79710,351,438.75");
        List<Host> hostsInMidland = new ArrayList<>();
        //
        try {
            hostsInMidland = service.findByCity("midland");
        } catch (DALException e) {
            System.out.println("Error reading by city");
        }
        //
        assertTrue(hostsInMidland.contains(host));
    }

    @Test
    void findByAddress() {
        Host host = mapper.deserialize("fc92b5f3-3e59-4fea-a818-c00aa4c0d9ba,Putman,jputmandh@trellian.com,(432) 1135676,52553 Roxbury Parkway,Midland,TX,79710,351,438.75");
        List<Host> hostsInRoxbury = new ArrayList<>();
        //
        try {
            hostsInRoxbury = service.findByAddress("52553 Roxbury Parkway");
        } catch (DALException e) {
            System.out.println("Error reading by city");
        }
        //
        assertTrue(hostsInRoxbury.contains(host));
    }
}
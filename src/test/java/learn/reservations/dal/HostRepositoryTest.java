package learn.reservations.dal;

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

class HostRepositoryTest {

    Repository<Host> repository;

    List<Host> original;
    ObjectMapper<Host> mapper;

    @BeforeEach
    void setUp() {
        mapper = new HostMapper();
        repository = new HostRepository("./data/test_data/test_hosts.csv", mapper, "id,last_name,email,phone,address,city,state,postal_code,standard_rate,weekend_rate");
        original = safeRead();
    }

    private List<Host> safeRead() {
        try {
            return repository.readAll();
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
    void testReadAll() {
        //Assign
        List<Host> hosts = new ArrayList<>();
        int expected = 1000;
        //Act
        hosts = safeRead();
        //Assert
        assertEquals(expected, hosts.size());
        hosts.forEach(Assertions::assertNotNull);
    }



    @Test
    void testCreate() {
        Host newHost = mapper.deserialize("example,Bond,Bond@JamesBond.com,(225) 9506172,817 Hoard Parkway,Baton Rouge,LA,70815,210,262.50");
        Host actualHost = new Host();
        //
        try {
            actualHost = repository.create(newHost);
        } catch (DALException e) {
            System.out.println("Error creating");
        }
        //
        List<Host> actual = safeRead();
        assertNotEquals(original, actual);
        assertEquals(1001, actual.size());
        assertEquals(newHost, actualHost);
        assertNotEquals("example", actualHost.getId());
    }

    @Test
    void testUpdate() {
        Host newHost = mapper.deserialize(",Bond,Bond@JamesBond.com,(225) 9506172,817 Hoard Parkway,Baton Rouge,LA,70815,210,262.50");
        newHost.setId(original.get(0).getId());
        //
        try {
            repository.update(newHost);
        } catch (DALException e) {
            System.out.println("Error updating");
        }
        //
        List<Host> actual = safeRead();
        assertEquals(1000, actual.size());
        assertEquals(newHost, actual.get(0));
        assertEquals(actual.get(0).toString(), newHost.toString());
    }

    @Test
    void testUpdateWrongId() {
        Host newHost = mapper.deserialize("-10,Bond,Bond@JamesBond.com,(225) 9506172,817 Hoard Parkway,Baton Rouge,LA,70815,210,262.50");
        //
        try {
            repository.update(newHost);
        } catch (DALException e) {
            System.out.println("Error updating");
        }
        //
        List<Host> actual = safeRead();
        assertEquals(1000, actual.size());
        assertNotEquals(newHost, actual.get(0));
    }

    @Test
    void testDelete() {
        Host hostToDelete = original.get(0);
        //
        try {
            repository.delete(hostToDelete);
        } catch (DALException e) {
            System.out.println("Error deleting");
        }
        //
        List<Host> actual = safeRead();
        assertEquals(999, actual.size());
        assertNotEquals(hostToDelete, actual.get(0));
        assertNotEquals(original, actual);
    }

    @Test
    void testDeleteNonExistingHost() {
        Host hostToDelete = new Host();
        //
        try {
            repository.delete(hostToDelete);
        } catch (DALException e) {
            System.out.println("Error deleting");
        }
        //
        List<Host> actual = safeRead();
        assertEquals(1000, actual.size());
        assertEquals(original, actual);
    }
}
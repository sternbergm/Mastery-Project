package learn.reservations.dal;

import learn.reservations.models.Guest;
import learn.reservations.models.Host;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GuestMapperTest {

    String guestString;
    Guest guestObject;

    ObjectMapper<Guest> mapper;

    @BeforeEach
    void setUp() {
        guestString = "14,Donall,Fenelow,dfenelowd@google.nl,(313) 6600939,MI";
        guestObject = new Guest();
        mapper = new GuestMapper();
    }

    @Test
    void testDeserialize() {
        String[] objValues = guestString.split(",");
        guestObject.setId(Integer.parseInt(objValues[0]));
        guestObject.setFirstName(objValues[1]);
        guestObject.setLastName(objValues[2]);
        guestObject.setEmail(objValues[3]);
        guestObject.setPhone(objValues[4]);
        guestObject.setState(objValues[5]);
        Guest testObject;
        //
        testObject = mapper.deserialize(guestString);
        //assert
        assertEquals(guestObject, testObject);

    }

    @Test
    void testSerialize() {
        Guest guest = mapper.deserialize(guestString);
        //
        String actual = mapper.serialize(guest);
        //
        assertEquals(guestString, actual);
    }
}
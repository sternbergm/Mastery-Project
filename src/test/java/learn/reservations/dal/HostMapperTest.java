package learn.reservations.dal;

import learn.reservations.models.Host;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;

class HostMapperTest {

    String hostString;
    Host hostObject;

    ObjectMapper<Host> mapper;

    @BeforeEach
    void setUp() {
        hostString = "3edda6bc-ab95-49a8-8962-d50b53f84b15,Yearnes,eyearnes0@sfgate.com,(806) 1783815,3 Nova Trail,Amarillo,TX,79182,340.00,425.00";
        hostObject = new Host();
        mapper = new HostMapper();
    }

    @Test
    void testDeserialize() {
        //assign
        String[] objValues = hostString.split(",");
        hostObject.setId(objValues[0]);
        hostObject.setLastName(objValues[1]);
        hostObject.setEmail(objValues[2]);
        hostObject.setPhone(objValues[3]);
        hostObject.setAddress(objValues[4]);
        hostObject.setCity(objValues[5]);
        hostObject.setState(objValues[6]);
        hostObject.setPostal_code(Integer.parseInt(objValues[7]));
        hostObject.setStandard_rate(BigDecimal.valueOf(Double.parseDouble(objValues[8])));
        hostObject.setWeekend_rate(BigDecimal.valueOf(Double.parseDouble(objValues[9])));
        Host testObject;
        //Act
        testObject = mapper.deserialize(hostString);
        //
        assertEquals(hostObject, testObject);
    }

    @Test
    void testSerialize() {
        hostObject = mapper.deserialize(hostString);
        //
        String expected = mapper.serialize(hostObject);
        //
        assertEquals(hostString, expected);
    }
}
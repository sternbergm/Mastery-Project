package learn.reservations.dal;

import learn.reservations.models.Host;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class HostMapper implements ObjectMapper<Host> {

    @Override
    public String serialize(Host host) {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%.2f,%.2f",
                host.getId(),
                host.getLastName(),
                host.getEmail(),
                host.getPhone(),
                host.getAddress(),
                host.getCity(),
                host.getState(),
                host.getPostal_code(),
                host.getStandard_rate(),
                host.getWeekend_rate());
    }

    @Override
    public Host deserialize(String obj) {
        Host result  = new Host();
        String[] objValues = obj.split(",");
        result.setId(objValues[0]);
        result.setLastName(objValues[1]);
        result.setEmail(objValues[2]);
        result.setPhone(objValues[3]);
        result.setAddress(objValues[4]);
        result.setCity(objValues[5]);
        result.setState(objValues[6]);
        result.setPostal_code(Integer.parseInt(objValues[7]));
        result.setStandard_rate(BigDecimal.valueOf(Double.parseDouble(objValues[8])));
        result.setWeekend_rate(BigDecimal.valueOf(Double.parseDouble(objValues[9])));

        return result;
    }
}

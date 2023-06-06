package learn.reservations.dal;

import learn.reservations.models.Guest;
import org.springframework.stereotype.Component;

@Component
public class GuestMapper implements ObjectMapper<Guest> {
    @Override
    public String serialize(Guest guest) {
        return String.format("%s,%s,%s,%s,%s,%s",
                guest.getId(),
                guest.getFirstName(),
                guest.getLastName(),
                guest.getEmail(),
                guest.getPhone(),
                guest.getState());
    }

    @Override
    public Guest deserialize(String obj) {
        String[] objValues = obj.split(",");
        Guest result = new Guest();
        result.setId(Integer.parseInt(objValues[0]));
        result.setFirstName(objValues[1]);
        result.setLastName(objValues[2]);
        result.setEmail(objValues[3]);
        result.setPhone(objValues[4]);
        result.setState(objValues[5]);
        return result;
    }

}
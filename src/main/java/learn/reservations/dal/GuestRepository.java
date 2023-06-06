package learn.reservations.dal;

import learn.reservations.models.Guest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GuestRepository extends PersonRepository<Guest> {

    public GuestRepository(@Value("${guestsFilePath}") String fileName, ObjectMapper<Guest> mapper, @Value("guest_id,first_name,last_name,email,phone,state") String header) {
        this.fileName = fileName;
        this.mapper = mapper;
        this.header = header;
    }

    @Override
    void setId(Guest guest, List<Guest> guests) {
        int nextId = guests.stream().mapToInt(Guest::getId).filter(i -> i>=0).max().orElse(0);
        nextId++;
        guest.setId(nextId);
    }
}

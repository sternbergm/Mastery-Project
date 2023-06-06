package learn.reservations.dal;

import learn.reservations.models.Host;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HostRepository extends PersonRepository<Host> {

    public HostRepository(@Value("${hostsFilePath}") String fileName, ObjectMapper<Host> mapper, @Value("id,last_name,email,phone,address,city,state,postal_code,standard_rate,weekend_rate") String header) {
        this.fileName = fileName;
        this.mapper = mapper;
        this.header = header;
    }

    @Override
    void setId(Host object, List<Host> objects) {
        object.setId(java.util.UUID.randomUUID().toString());
    }
}

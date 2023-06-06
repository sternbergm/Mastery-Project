package learn.reservations.dal;

import com.fasterxml.jackson.core.type.TypeReference;
import learn.reservations.models.Reservation;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("ReservationRepositoryJSON")
public class ReservationJSONRepository implements ReservationRepository {

    private String fileName;

    private ObjectMapper mapper;


    public ReservationJSONRepository(@Value("${jsonReservationFilePath}") String fileName) {
        this.fileName = fileName;
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        try {
            File file = new File(fileName);
            file.createNewFile();
        } catch (IOException ex) {
            System.out.println("Unable to create new file");
        }
    }

    private Map<String, List<Reservation>> readAll() throws DALException {
        Map<String, List<Reservation>> map = new HashMap<>();
        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException ex) {
            throw new DALException("Unable to create file");
        }
        try {
            map = mapper.readValue(file, new TypeReference<Map<String, List<Reservation>>>() {});
        } catch (IOException e) {
            throw new DALException("Error reading from JSON");
        }
        return map;
    }

    @Override
    public List<Reservation> readByHostId(String hostId) throws DALException {
        List<Reservation> reservations = readAll().get(hostId);
        if(reservations == null) return new ArrayList<>();
        else return reservations;
    }

    private void writeHostId(String hostId, List<Reservation> reservations) throws DALException {
        Map<String, List<Reservation>> map = readAll();
        if(reservations.size() == 0){
            map.remove(hostId);
            writeAll(map);
        }else {
            List<Reservation> oldReservations = map.get(hostId);
            oldReservations = reservations;
            map.put(hostId, oldReservations);
            writeAll(map);
        }
    }

    private void writeAll(Map<String, List<Reservation>> map) throws DALException {
        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException ex) {
            throw new DALException("Unable to create file");
        }
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, map);
        } catch (IOException e) {
            throw new DALException("Error writing to JSON");
        }
    }

    @Override
    public List<Reservation> readByGuestId(int guestId) throws DALException {
        Map<String, List<Reservation>> map = readAll();
        List<Reservation> reservations = new ArrayList<>();
        map.forEach((k,v) -> reservations.addAll(v.stream().filter(res -> res.getGuestId()==guestId).toList()));
        return reservations;
    }

    @Override
    public Reservation create(String hostId, Reservation reservation) throws DALException {
        List<Reservation> reservations = readByHostId(hostId);
        int nextId = reservations.stream().mapToInt(Reservation::getId).filter(i -> i>=0).max().orElse(0);
        nextId++;
        reservation.setId(nextId);
        reservations.add(reservation);
        writeHostId(hostId, reservations);
        return reservation;
    }

    @Override
    public void update(String hostId, int id, Reservation reservation) throws DALException {
        List<Reservation> reservations = readByHostId(hostId);
        IntStream.range(0, reservations.size()).filter(i -> reservations.get(i).getId()==(id)).findFirst().ifPresent(i -> reservations.set(i, reservation));
        writeHostId(hostId, reservations);
    }

    @Override
    public void delete(String hostId, int id) throws DALException {
        List<Reservation> reservations = readByHostId(hostId);
        IntStream.range(0, reservations.size()).filter(i -> reservations.get(i).getId()==(id)).findFirst().ifPresent(reservations::remove);
        writeHostId(hostId, reservations);
    }
}

package learn.reservations.dal;

import learn.reservations.models.Reservation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
@Qualifier("ReservationRepositoryFile")
public class ReservationRepositoryImpl implements ReservationRepository {

    private String directory;
    private List<Reservation> reservations;

    private ObjectMapper<Reservation> mapper;


    public ReservationRepositoryImpl(@Value("${reservationsDirectoryPath}") String directory, ObjectMapper<Reservation> mapper) {
        this.directory = directory;
        this.reservations = new ArrayList<>();
        this.mapper = mapper;
    }

    @Override
    public List<Reservation> readByHostId(String hostId) throws DALException {
        List<Reservation> result = new ArrayList<>();
        Path filePath = Path.of(directory, hostId+".csv");
        try {
            String content = Files.readString(filePath);
            List<String> serializedObjects = new ArrayList<>(Arrays.stream(content.split("\n")).toList());
            serializedObjects.remove(0);
            result = serializedObjects.stream().filter(s -> !s.equalsIgnoreCase("")).map(mapper::deserialize).collect(Collectors.toList());
            List<Reservation> finalResult = result;
            IntStream.range(0, result.size()).forEach(i -> finalResult.get(i).setHostId(hostId));
        } catch (IOException e) {
            return new ArrayList<>();
        }
        return result;
    }

    @Override
    public List<Reservation> readByGuestId(int guestId){
        List<String> fileNames = Arrays.stream(Objects.requireNonNull(new File(directory).list())).toList();
        reservations = new ArrayList<>();
        fileNames.forEach(fileName -> {
            String hostId = fileName.split("\\.")[0];
            try {
                reservations.addAll(readByHostId(hostId).stream().filter(res -> res.getGuestId()==guestId).toList());
            } catch (DALException e) {
                //none
            }
        });
        return reservations;
    }

    @Override
    public Reservation create(String hostId, Reservation reservation) throws DALException {
        reservations = readByHostId(hostId);
        int nextId = reservations.stream().mapToInt(Reservation::getId).filter(i -> i>=0).max().orElse(0);
        nextId++;
        reservation.setId(nextId);
        reservations.add(reservation);
        writeAll(hostId, reservations);
        return reservation;
    }

    @Override
    public void update(String hostId, int id, Reservation reservation) throws DALException {
        reservations = readByHostId(hostId);
        IntStream.range(0, reservations.size()).filter(i -> reservations.get(i).getId()==(id)).findFirst().ifPresent(i -> reservations.set(i, reservation));
        writeAll(hostId, reservations);
    }

    @Override
    public void delete(String hostId, int id) throws DALException {
        reservations = readByHostId(hostId);
        IntStream.range(0, reservations.size()).filter(i -> reservations.get(i).getId()==(id)).findFirst().ifPresent(reservations::remove);
        writeAll(hostId, reservations);
    }


    private void writeAll(String hostId, List<Reservation> reservations) throws DALException {
        File file = new File(directory, hostId+".csv");

        if(reservations.isEmpty()){
                file.delete();
                return;
        }

        try {
            file.createNewFile();
        } catch (IOException ex) {
            throw new DALException("Unable to create file");
        }

        try (PrintWriter writer = new PrintWriter(file)) {
            PrintWriter finalWriter = writer;
            finalWriter.print("id,start_date,end_date,guest_id,total" + "\n");
            reservations.forEach(res -> finalWriter.print(mapper.serialize(res) + "\n"));
        } catch (FileNotFoundException e) {
            throw new DALException("Error writing to file");
        }
    }
}

package learn.reservations.dal;

import learn.reservations.models.Reservation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class ReservationMapper implements ObjectMapper<Reservation> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String serialize(Reservation reservation) {
        return String.format("%s,%s,%s,%d,%.2f",
                reservation.getId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getGuestId(),
                reservation.getTotal());
    }

    @Override
    public Reservation deserialize(String obj) {
        String[] objValues = obj.split(",");
        Reservation reservation = new Reservation();
        reservation.setId(Integer.parseInt(objValues[0]));
        reservation.setStartDate(LocalDate.parse(objValues[1], FORMATTER));
        reservation.setEndDate(LocalDate.parse(objValues[2], FORMATTER));
        reservation.setGuestId(Integer.parseInt(objValues[3]));
        reservation.setTotal(BigDecimal.valueOf(Double.parseDouble(objValues[4])));

        return  reservation;
    }
}

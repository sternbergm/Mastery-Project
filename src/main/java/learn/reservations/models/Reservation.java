package learn.reservations.models;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Reservation {
    private int id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String hostId;
    private int guestId;
    private BigDecimal total;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id) && Objects.equals(hostId, that.hostId) && Objects.equals(guestId, that.guestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, hostId, guestId);
    }

    @Override
    public String toString() {
        return "id: %d, dates:  %s - %s, total: $%.2f".formatted(id, startDate, endDate, total);
    }


    public void calculateTotal(BigDecimal weekdayRate, BigDecimal weekendRate) {
        total = BigDecimal.ZERO;
        if(startDate == null || endDate == null) return;
        LocalDate date = startDate;
        List<DayOfWeek> weekdays = new ArrayList<>();
        weekdays.add(DayOfWeek.MONDAY);
        weekdays.add(DayOfWeek.TUESDAY);
        weekdays.add(DayOfWeek.WEDNESDAY);
        weekdays.add(DayOfWeek.THURSDAY);
        weekdays.add(DayOfWeek.FRIDAY);
        do{
            if(weekdays.contains(date.getDayOfWeek())){
                total = total.add(weekdayRate);
            }else{
                total = total.add(weekendRate);
            }
            date = date.plusDays(1);
        }while(!date.isEqual(endDate));
    }
}

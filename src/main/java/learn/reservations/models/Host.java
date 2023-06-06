package learn.reservations.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Host extends Person {

    private String id;

    private String address;
    private String city;
    private int postal_code;
    private BigDecimal standard_rate;
    private BigDecimal weekend_rate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(int postal_code) {
        this.postal_code = postal_code;
    }

    public BigDecimal getStandard_rate() {
        return standard_rate;
    }

    public void setStandard_rate(BigDecimal standard_rate) {
        this.standard_rate = standard_rate;
    }

    public BigDecimal getWeekend_rate() {
        return weekend_rate;
    }

    public void setWeekend_rate(BigDecimal weekend_rate) {
        this.weekend_rate = weekend_rate;
    }

    @Override
    public String toString() {
        return "Host: " + lastName +
                ", address: " + address +
                ", city: " + city +
                ", state: " + state +
                ", email: " + email +
                ", phone: " + phone +
                ", standard rate: $" + standard_rate.setScale(2, RoundingMode.HALF_UP) +
                ", weekend rate: $" + weekend_rate.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Host host = (Host) o;
        return Objects.equals(id, host.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

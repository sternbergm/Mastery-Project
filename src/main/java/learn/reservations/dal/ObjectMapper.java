package learn.reservations.dal;

public interface ObjectMapper <Obj> {

    String serialize(Obj obj);

    Obj deserialize(String obj);
}

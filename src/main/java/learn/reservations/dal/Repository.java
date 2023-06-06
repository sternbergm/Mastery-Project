package learn.reservations.dal;

import java.util.List;

public interface Repository<Obj> {
    List<Obj> readAll() throws DALException;

    Obj create(Obj object) throws DALException;

    void update(Obj object) throws DALException;

    void delete(Obj object) throws DALException;
}

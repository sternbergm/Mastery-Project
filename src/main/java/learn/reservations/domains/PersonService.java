package learn.reservations.domains;

import learn.reservations.dal.DALException;

import java.util.List;

public interface PersonService<Obj> {
    Obj findByEmail(String email) throws DALException;
    List<Obj> getAll() throws DALException;

    List<Obj> findByLastNamePrefix(String lastNamePrefix) throws DALException;

    Result<Obj> add(Obj object) throws DALException;

    Result<Obj> update(Obj object) throws DALException;

    void delete(Obj object) throws DALException;

    List<Obj> findByState(String state) throws DALException;

    List<Obj> findByCity(String city) throws DALException;

    List<Obj> findByAddress(String address) throws DALException;
}

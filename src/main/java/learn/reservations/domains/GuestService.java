package learn.reservations.domains;

import learn.reservations.dal.DALException;
import learn.reservations.dal.Repository;
import learn.reservations.models.Guest;
import learn.reservations.models.Host;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuestService implements PersonService<Guest> {

    Repository<Guest> repository;

    public GuestService(Repository<Guest> repository) {
        this.repository = repository;
    }

    @Override
    public Guest findByEmail(String email) throws DALException {
        List<Guest> guests = repository.readAll();
        return guests.stream().filter(g -> g.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);
    }

    @Override
    public List<Guest> getAll() throws DALException {
        return repository.readAll();
    }

    @Override
    public List<Guest> findByLastNamePrefix(String prefix) throws DALException {
        return repository.readAll().stream()
                .filter(i -> i.getLastName().toLowerCase().startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Result<Guest> add(Guest guest) throws DALException {
        Result<Guest> result = new Result<>();
        validateFields(result, guest);
        if(!result.isSuccessful()) return result;

        List<Guest> guests = getAll();
        validateDuplicate(result, guest, guests);
        if(!result.isSuccessful()) return result;

        result.setPayload(repository.create(guest));
        return result;
    }

    @Override
    public Result<Guest> update(Guest guest) throws DALException {
        Result<Guest> result = new Result<>();
        validateFields(result, guest);
        if(!result.isSuccessful()) return result;

        List<Guest> guests = getAll();
        if(guests.stream().noneMatch(g -> g.getId()==guest.getId())){
            result.addMessage("Error with guest Id, no guest in list was found with that Id");
            return result;
        }

        guests = guests.stream().filter(g -> g.getId()!=guest.getId()).collect(Collectors.toList());
        validateDuplicate(result, guest, guests);
        if(!result.isSuccessful()) return result;

        repository.update(guest);
        result.setPayload(guest);

        return result;
    }

    @Override
    public void delete(Guest guest) throws DALException{
        repository.delete(guest);
    }

    @Override
    public List<Guest> findByState(String state) throws DALException {
        return getAll().stream().filter(g -> g.getState().equals(state)).collect(Collectors.toList());
    }

    @Override
    public List<Guest> findByCity(String city) throws DALException {
        return new ArrayList<>();
    }

    @Override
    public List<Guest> findByAddress(String address) throws DALException {
        return new ArrayList<>();
    }

    private void validateDuplicate(Result<Guest> result, Guest guest, List<Guest> guests) {
        if(guests.stream().anyMatch(g-> g.getEmail().equals(guest.getEmail()))){
            result.addMessage("This guest is already on the list, can't have two guests with the same email");
        }
    }

    private void validateFields(Result<Guest> result, Guest guest) {
        if(guest == null){
            result.addMessage("Nothing to save, guest is null");
            return;
        }

        if(guest.getFirstName() == null || guest.getFirstName().equals("")){
            result.addMessage("First name cannot be empty");
        }
        if(guest.getLastName() == null || guest.getLastName().equals("")){
            result.addMessage("Last name cannot be empty");
        }

        if(guest.getState() == null || guest.getState().equals("")){
            result.addMessage("state cannot be empty");
        }

        if(guest.getEmail() == null || guest.getEmail().equals("")){
            result.addMessage("Email cannot be empty");
        }

        if(guest.getPhone() == null || guest.getPhone().equals("")){
            result.addMessage("Phone number cannot be empty");
        }
    }
}

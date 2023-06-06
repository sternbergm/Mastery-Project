package learn.reservations.domains;

import learn.reservations.dal.DALException;
import learn.reservations.dal.Repository;
import learn.reservations.models.Guest;
import learn.reservations.models.Host;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HostService implements PersonService<Host> {

    Repository<Host> repository;

    public HostService(Repository<Host> repository) {
        this.repository = repository;
    }

    @Override
    public Host findByEmail(String email) throws DALException {
        List<Host> hosts = repository.readAll();
        return hosts.stream().filter(h -> h.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);
    }

    @Override
    public List<Host> getAll() throws DALException {
        return repository.readAll();
    }

    @Override
    public List<Host> findByLastNamePrefix(String prefix) throws DALException {
        return repository.readAll().stream()
                .filter(i -> i.getLastName().toLowerCase().startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Result<Host> add(Host host) throws DALException {
        Result<Host> result = new Result<>();
        validateFields(result, host);
        if(!result.isSuccessful()) return result;

        List<Host> hosts = getAll();
        validateDuplicate(result, host, hosts);
        if(!result.isSuccessful()) return result;

        result.setPayload(repository.create(host));
        return result;
    }

    @Override
    public Result<Host> update(Host host) throws DALException{
        Result<Host> result = new Result<>();
        validateFields(result, host);
        if(!result.isSuccessful()) return result;



        List<Host> hosts = getAll();
        if(hosts.stream().noneMatch(h -> h.getId().equals(host.getId()))){
            result.addMessage("Error with host Id, no host in list was found with that Id");
            return result;
        }

        hosts = hosts.stream().filter(h -> !h.getId().equals(host.getId())).collect(Collectors.toList());
        validateDuplicate(result, host, hosts);
        if(!result.isSuccessful()) return result;



        repository.update(host);
        result.setPayload(host);

        return result;

    }

    @Override
    public void delete(Host host) throws DALException{
        repository.delete(host);
    }

    @Override
    public List<Host> findByState(String state) throws DALException {
        return getAll().stream().filter(h -> h.getState().equalsIgnoreCase(state)).collect(Collectors.toList());
    }

    @Override
    public List<Host> findByCity(String city) throws DALException {
        return getAll().stream().filter(h -> h.getCity().equalsIgnoreCase(city)).collect(Collectors.toList());
    }

    @Override
    public List<Host> findByAddress(String address) throws DALException {
        return getAll().stream().filter(h -> h.getAddress().equalsIgnoreCase(address)).collect(Collectors.toList());
    }

    private void validateDuplicate(Result<Host> result, Host host, List<Host> hosts) {
        if(hosts.stream().anyMatch(g-> g.getEmail().equals(host.getEmail()))){
            result.addMessage("This host is already on the list, can't have two hosts with the same email");
        }
    }

    private void validateFields(Result<Host> result, Host host) {
        if(host == null){
            result.addMessage("Nothing to save, host is null");
            return;
        }
        if(host.getLastName() == null || host.getLastName().equals("")){
            result.addMessage("Last name cannot be empty");
        }

        if(host.getState() == null || host.getState().equals("")){
            result.addMessage("State cannot be empty");
        }

        if(host.getEmail() == null || host.getEmail().equals("")){
            result.addMessage("Email cannot be empty");
        }

        if(host.getPhone() == null || host.getPhone().equals("")){
            result.addMessage("Phone number cannot be empty");
        }

        if(host.getAddress() == null || host.getAddress().equals("")){
            result.addMessage("Address cannot be empty");
        }

        if(host.getCity() == null || host.getCity().equals("")){
            result.addMessage("City cannot be empty");
        }

        if(host.getStandard_rate() == null || host.getStandard_rate().compareTo(BigDecimal.ZERO)<0){
            result.addMessage("Standard rate cannot be empty or less than 0");
        }

        if(host.getWeekend_rate() == null || host.getWeekend_rate().compareTo(BigDecimal.ZERO)<0){
            result.addMessage("Weekend rate cannot be empty or less than 0");
        }
    }
}
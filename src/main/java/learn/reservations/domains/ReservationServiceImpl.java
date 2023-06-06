package learn.reservations.domains;

import learn.reservations.dal.DALException;
import learn.reservations.dal.Repository;
import learn.reservations.dal.ReservationRepository;
import learn.reservations.models.Guest;
import learn.reservations.models.Host;
import learn.reservations.models.Reservation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class ReservationServiceImpl implements ReservationService {

    ReservationRepository repository;
    Repository<Guest> guestRepository;
    Repository<Host> hostRepository;

    public ReservationServiceImpl(@Qualifier("ReservationRepositoryJSON") ReservationRepository repository, Repository<Guest> guestRepository, Repository<Host> hostRepository) {
        this.repository = repository;
        this.guestRepository = guestRepository;
        this.hostRepository = hostRepository;
    }

    @Override
    public List<Reservation> getReservationsByHost(String hostId) throws DALException {
        return repository.readByHostId(hostId);
    }

    @Override
    public List<Reservation> getReservationsByGuest(int guestId) throws DALException {
        return repository.readByGuestId(guestId);
    }

    @Override
    public Result<Reservation> makeReservation(String hostId, Reservation reservation) throws DALException {
        Result<Reservation> result = new Result<>();
        validateFields(result, reservation);
        if(!result.isSuccessful()) return result;
        validateChildrenExist(result, reservation);
        if(!result.isSuccessful()) return result;
        if(!reservation.getHostId().equals(hostId)){
            result.addMessage("Host Id provided does not match reservation host ID");
            return result;
        }

        validateReservationDates(result, reservation, hostId);
        if(!result.isSuccessful()) return result;

        result.setPayload(repository.create(hostId, reservation));
        return result;
    }

    @Override
    public Result<Reservation> updateReservation(String hostId, int id, Reservation reservation) throws DALException {

        Result<Reservation> result = new Result<>();
        validateFields(result, reservation);
        if(!result.isSuccessful()) return result;
        validateChildrenExist(result, reservation);
        if(!result.isSuccessful()) return result;
        if(!reservation.getHostId().equals(hostId)){
            result.addMessage("Host Id provided does not match reservation host ID");
            return result;
        }
        if(reservation.getId()!=(id)){
            result.addMessage("Reservation Id provided does not match the provided Id to update");
            return result;
        }

        List<Reservation> reservations = repository.readByHostId(hostId);
        IntStream.range(0, reservations.size()).filter(i -> reservations.get(i).getId()==(id)).findFirst().ifPresent(reservations::remove);
        validateReservationDatesInList(result, reservation, reservations);
        if(!result.isSuccessful()) return result;

        repository.update(hostId, id, reservation);
        result.setPayload(reservation);
        return result;
    }

    @Override
    public void deleteReservation(String hostId, int id) throws DALException {
        List<Reservation> reservations = repository.readByHostId(hostId);
        if(reservations.stream().filter(r -> r.getStartDate().isAfter(LocalDate.now())).anyMatch(r -> r.getId()==(id))){
            repository.delete(hostId, id);
        }
    }

    @Override
    public List<Reservation> getReservationsByHosts(List<Host> hostsPerState) {
        List<Reservation> result = new ArrayList<>();
        hostsPerState.stream().map(Host::getId).forEach(id -> {
            try {
                result.addAll(getReservationsByHost(id));
            } catch (DALException e) {
                //
            }
        });

        return result;
    }

    @Override
    public void deleteReservationsByGuest(int guestId) throws DALException {
        List<Reservation> reservations = getReservationsByGuest(guestId);
        reservations
                .forEach(res -> {
                    try {
                        repository.delete(res.getHostId(), res.getId());
                    } catch (DALException e) {
                        //
                    }
                });
    }

    @Override
    public void deleteReservationByHost(String hostId) throws DALException {
        List<Reservation> reservations = repository.readByHostId(hostId);
        reservations.forEach(res -> {
            try {
                repository.delete(hostId, res.getId());
            } catch (DALException e) {
                //
            }
        });
    }


    @Override
    public void validateReservationDates(Result<Reservation> result, Reservation reservation, String hostId) throws DALException{
        List<Reservation> reservations = repository.readByHostId(hostId);
        validateReservationDatesInList(result, reservation, reservations);
    }


    private void validateReservationDatesInList(Result<Reservation> result, Reservation reservation, List<Reservation> reservations){
        if(!reservation.getStartDate().isAfter(LocalDate.now()) || !reservation.getEndDate().isAfter(reservation.getStartDate())){
            result.addMessage("Error with dates, please review");
            return;
        }

        List<List<LocalDate>> futureReservationDates = reservations.stream().filter(r -> r.getStartDate().isAfter(LocalDate.now())).map(i -> {
            List<LocalDate> datePair = new ArrayList<>();
            datePair.add(i.getStartDate());
            datePair.add(i.getEndDate());
            return datePair;
        }).toList();

        LocalDate startDate = reservation.getStartDate();
        LocalDate endDate = reservation.getEndDate();
        for (List<LocalDate> dates : futureReservationDates) {
            if(startDate.compareTo(dates.get(0))>=0 && startDate.compareTo(dates.get(1))<=0){
                result.addMessage("Error with start date, overlap with another reservation");
                return;
            }
            if(endDate.compareTo(dates.get(0))>=0 && endDate.compareTo(dates.get(1))<=0){
                result.addMessage("Error with end date, overlap with another reservation");
                return;
            }
            if(startDate.isBefore(dates.get(0)) && endDate.isAfter(dates.get(1))){
                result.addMessage("Error with dates, there is an existing reservation in between both of these dates");
            }
        }
    }

    private void validateChildrenExist(Result<Reservation> result, Reservation reservation) throws DALException {
        if(guestRepository.readAll()
                .stream()
                .noneMatch(g -> g.getId() == reservation.getGuestId())){
            result.addMessage("Error finding Guest in guest list");
        }
        if(hostRepository.readAll()
                .stream()
                .noneMatch(h -> h.getId().equals(reservation.getHostId()))){
            result.addMessage("Error finding Host in host list");
        }
    }

    private void validateFields(Result<Reservation> result, Reservation reservation) {
        if(reservation == null){
            result.addMessage("Nothing to save, reservation is null");
            return;
        }

        if(reservation.getHostId() == null || reservation.getHostId().equalsIgnoreCase("")){
            result.addMessage("Reservation does not have valid host Id");
            return;
        }

        if(reservation.getTotal() == null || reservation.getTotal().compareTo(BigDecimal.ZERO) <=0){
            result.addMessage("Reservation total cost is incorrect");
        }

    }


}

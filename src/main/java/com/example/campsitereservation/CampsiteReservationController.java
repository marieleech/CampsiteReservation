package com.example.campsitereservation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.campsitereservation.entities.DateRangeDto;
import com.example.campsitereservation.entities.Reservation;
import com.example.campsitereservation.entities.ReservationDto;
import com.example.campsitereservation.errors.InvalidReservationDates;
import com.example.campsitereservation.errors.InvalidReservationInput;
import com.example.campsitereservation.service.CampsiteReservationService;

@RestController
public class CampsiteReservationController {

    private final CampsiteReservationService campsiteReservationService;

    @Autowired
    public CampsiteReservationController(CampsiteReservationService campsiteReservationService) {
        this.campsiteReservationService = campsiteReservationService;
    }

    @PostMapping("/reservations")
    public ResponseEntity<String> createReservation(@RequestBody ReservationDto reservationDto) {
        Reservation reservation = new Reservation();
        try {
            if (checkReservation(reservationDto.getCheckInDate(), reservationDto.getCheckOutDate())) {
                reservation = saveReservation(reservationDto);
            }
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("These reservation dates are not available. Please choose another date.");
        } catch (InvalidReservationDates e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (InvalidReservationInput e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body(String.format("Reservation successful! Here is your booking reference number: %s", reservation.getBookingReference()));
    }

    @PutMapping("/reservations/{bookingReference}")
    public ResponseEntity<?> updateReservation(@PathVariable String bookingReference, @RequestBody ReservationDto reservationDto) {
        Optional<Reservation> existingReservationOpt = campsiteReservationService.findFirstByBookingReference(bookingReference);

        if (!existingReservationOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Reservation with this booking reference does not exist.");
        }

        try {
            updateReservation(reservationDto, existingReservationOpt.get());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("These reservation dates are not available. Please choose another date.");
        } catch (InvalidReservationDates e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (InvalidReservationInput e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body("Update of reservation successful!");
    }

    @DeleteMapping("/reservations/{bookingReference}")
    public ResponseEntity<String> cancelReservation(@PathVariable String bookingReference) {
        if (!campsiteReservationService.findFirstByBookingReference(bookingReference).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        campsiteReservationService.deleteReservationByBookingReference(bookingReference);
        return ResponseEntity.ok().body("Your reservation has successfully been deleted.");
    }

    @GetMapping("/reservations")
    public ResponseEntity<String> findAllAvailableReservations(@RequestBody(required=false) DateRangeDto dateRangeDto) {
        List<LocalDate> dates = new ArrayList<>();
        try {
            if (Objects.isNull(dateRangeDto)) {
                dates = getAvailableDates(LocalDate.now(), LocalDate.now().plusMonths(1L));
            } else {
                dates = getAvailableDates(dateRangeDto.getCheckInDate(), dateRangeDto.getCheckOutDate());
            }

            if (dates.isEmpty()) {
                ResponseEntity.ok().body("There are no availabilities in your given time range.");
            }
        } catch (RuntimeException e) {
            ResponseEntity.internalServerError().body("Unable to fetch available dates. Please try again.");
        }
        return ResponseEntity.ok().body(String.format("The available dates within your time range are: %s", dates.toString()));
    }

    private boolean checkReservation(LocalDate checkInDate, LocalDate checkOutDate) throws InvalidReservationDates, InvalidReservationInput {
        if(Objects.isNull(checkInDate) || Objects.isNull(checkOutDate)) {
            throw new InvalidReservationInput("Reservation dates cannot be null. Please provide reservation dates.");
        }

        Long timeAhead = ChronoUnit.DAYS.between(LocalDate.now(), checkInDate);

        if (timeAhead < 1 || timeAhead > 31 || !checkReservationDuration(checkInDate, checkOutDate)) {
            throw new InvalidReservationDates("Please book no less than one day in advance and no more than one month in advance.");
        } else {
            return true;
        }
    }

    private boolean checkReservationDuration(LocalDate checkInDate, LocalDate checkOutDate) throws InvalidReservationDates {
        Long duration = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

        if (duration > 3 || duration < 1) {
            throw new InvalidReservationDates("The duration of this reservation is invalid. Please choose a duration that is maximum three days long, and no less than 1 day long.");
        } else {
            return true;
        }
    }

    private Reservation saveReservation(ReservationDto reservationDto) throws InvalidReservationInput {
        List<LocalDate> dates = getDates(reservationDto.getCheckInDate(), reservationDto.getCheckOutDate());
        List<Reservation> reservations = new ArrayList<>();
        String bookingReference = UUID.randomUUID().toString();
        validateNameAndEmail(reservationDto.getFirstName(), reservationDto.getLastName(), reservationDto.getEmail());

        for (LocalDate date : dates) {
            Reservation reservation = new Reservation();
            reservation.setBookingReference(bookingReference);
            reservation.setFirstName(reservationDto.getFirstName());
            reservation.setLastName(reservationDto.getLastName());
            reservation.setEmail(reservationDto.getEmail());
            reservation.setReservationDate(date);
            reservations.add(reservation);
        }

        campsiteReservationService.saveAllReservations(reservations);
        return reservations.get(0);
    }

    private void updateReservation(ReservationDto reservationDto, Reservation existingReservation) throws InvalidReservationInput, InvalidReservationDates {
        if (newDatesProvided(reservationDto)) {
            if (checkReservation(reservationDto.getCheckInDate(), reservationDto.getCheckOutDate())) {
                campsiteReservationService.updateReservationDates(existingReservation.getBookingReference(), getDates(reservationDto.getCheckInDate(), reservationDto.getCheckOutDate()));
            };
        }

        if (newUserInfoProvided(reservationDto)) {
            Reservation updatedUserInfoReservation = updateUserInfo(existingReservation, reservationDto);
            campsiteReservationService.updateReservationUserInfo(updatedUserInfoReservation.getLastName(), updatedUserInfoReservation.getFirstName(), updatedUserInfoReservation.getEmail(), updatedUserInfoReservation.getBookingReference());
        }
    }

    private boolean newDatesProvided(ReservationDto reservationDto) {
        return !Objects.isNull(reservationDto.getCheckInDate()) && !Objects.isNull(reservationDto.getCheckOutDate());
    }

    private boolean newUserInfoProvided(ReservationDto reservationDto) {
        return !Objects.isNull(reservationDto.getLastName()) || !Objects.isNull(reservationDto.getFirstName()) || !Objects.isNull(reservationDto.getEmail());
    }

    private List<LocalDate> getDates(LocalDate checkIn, LocalDate checkOut) {
        long daysBetween = ChronoUnit.DAYS.between(checkIn, checkOut);
        List<LocalDate> totalDates =
                LongStream.iterate(0, i -> i+1)
                        .limit(daysBetween).mapToObj(i->checkIn.plusDays(i))
                        .collect(Collectors.toList());
        return totalDates;
    }

    private boolean validateNameAndEmail(String firstName, String lastName, String email) throws InvalidReservationInput {
        if(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            throw new InvalidReservationInput("Your name and/or email cannot be blank. Please provide this information.");
        }
        return true;
    }

    private Reservation updateUserInfo(Reservation reservation, ReservationDto reservationDto) {
        if (!reservationDto.getEmail().isEmpty()) {
            reservation.setEmail(reservationDto.getEmail());
        }
        if (!reservationDto.getFirstName().isEmpty()){
            reservation.setFirstName(reservationDto.getFirstName());
        }
        if (!reservationDto.getLastName().isEmpty()){
            reservation.setLastName(reservationDto.getLastName());
        }

        return reservation;
    }

    private List<LocalDate> getAvailableDates(LocalDate start, LocalDate end) {
        List<LocalDate> dates = getDates(start, end);
        List<LocalDate> bookedDates = campsiteReservationService.findAvailableReservations(dates);
        dates.removeAll(bookedDates);
        return dates;
    }
}

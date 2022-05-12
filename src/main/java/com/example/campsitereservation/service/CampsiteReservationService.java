package com.example.campsitereservation.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.campsitereservation.CampsiteReservationRepository;
import com.example.campsitereservation.entities.Reservation;

@Service
public class CampsiteReservationService {

	private final CampsiteReservationRepository repository;

	@Autowired
	public CampsiteReservationService(CampsiteReservationRepository campsiteReservationRepository) {
		this.repository = campsiteReservationRepository;
	}

	@Transactional
	public void updateReservationDates(String bookingReference, List<LocalDate> newDates) {
		List<Reservation> existingReservations = findByBookingReference(bookingReference);
		List<LocalDate> existingDates = existingReservations.stream().map(reservation -> reservation.getReservationDate()).collect(Collectors.toList());
		List<LocalDate> unwantedDates = new ArrayList<>(existingDates);
		List<LocalDate> newDatesToAdd = new ArrayList<>(newDates);
		unwantedDates.removeAll(newDates);
		newDatesToAdd.removeAll(existingDates);
		unwantedDates.forEach(date -> deleteReservationByReservationDate(date));
		List<Reservation> reservations = new ArrayList<>();
		newDatesToAdd.forEach(date -> {
			Reservation reservation = new Reservation();
			reservation.setBookingReference(bookingReference);
			reservation.setFirstName(existingReservations.get(0).getFirstName());
			reservation.setLastName(existingReservations.get(0).getLastName());
			reservation.setEmail(existingReservations.get(0).getEmail());
			reservation.setReservationDate(date);
			reservations.add(reservation);
		});
		repository.saveAll(reservations);
	}

	@Transactional
	public List<LocalDate> findAvailableReservations(List<LocalDate> reservationDates){
		return repository.findAvailableReservations(reservationDates);
	}

	@Transactional
	public void updateReservationUserInfo(String lastName, String firstName, String email, String bookingReference){
		repository.updateReservationUserInfo(lastName, firstName, email, bookingReference);
	}

	@Transactional
	public void deleteReservationByBookingReference(String bookingReference) {
		repository.deleteReservationByBookingReference(bookingReference);
	}

	public Optional<Reservation> findFirstByBookingReference(String bookingReference) {
		return repository.findFirstByBookingReference(bookingReference);
	}

	public List<Reservation> findByBookingReference(String bookingReference) {
		return repository.findByBookingReference(bookingReference);
	}

	public void deleteReservationByReservationDate(LocalDate date) {
		repository.deleteReservationByReservationDate(date);
	}

	public void saveAllReservations(List<Reservation> reservations) {
		repository.saveAll(reservations);
	}

	public List<Reservation> findAll() {
		return repository.findAll();
	}

}

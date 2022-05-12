package com.example.campsitereservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.campsitereservation.entities.Reservation;

public interface CampsiteReservationRepository extends JpaRepository<Reservation, Long> {
	Optional<Reservation> findFirstByBookingReference(String bookingReference);

	@Modifying
	@Query("update Reservation r set r.lastName=?1, r.firstName=?2, r.email=?3 where r.bookingReference=?4")
	void updateReservationUserInfo(String lastName, String firstName, String email, String bookingReference);

	@Modifying
	@Query("delete from Reservation r where r.bookingReference=?1")
	void deleteReservationByBookingReference(String bookingReference);

	@Query(value = "select reservationDate from Reservation where reservationDate in :reservationDates")
	List<LocalDate> findAvailableReservations(List<LocalDate> reservationDates);

	List<Reservation> findByBookingReference(String bookingReference);

	void deleteReservationByReservationDate(LocalDate date);
}

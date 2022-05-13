package com.example.campsitereservation.entities;


import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.example.campsitereservation.beans.ReservationBean;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue
    private Long id;

    private String bookingReference;
    private LocalDate reservationDate;
    private String email;
    private String firstName;
    private String lastName;

    public ReservationBean getReservationBean() {
        return new ReservationBean(this.id, this.bookingReference, this.reservationDate, this.email, this.firstName, this.lastName);
    }
}

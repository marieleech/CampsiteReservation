package com.example.campsitereservation;

import java.time.LocalDate;

import javax.swing.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.example.campsitereservation.entities.DateRangeDto;
import com.example.campsitereservation.entities.Reservation;
import com.example.campsitereservation.entities.ReservationDto;

@SpringBootTest(classes = CampsiteReservationApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class CampsiteReservationApplicationIT {

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    private CampsiteReservationRepository campsiteReservationRepository;

    @BeforeEach
    @Transactional
    void beforeEach() {
        campsiteReservationRepository.deleteAll();
    }

    public HttpEntity<ReservationDto> buildCreateReservationRequestEntity(String email, String firstName, String lastName, LocalDate checkInDate, LocalDate checkOutDate) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        org.postgresql.core.v3.ConnectionFactoryImpl a;

        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setEmail(email);
        reservationDto.setLastName(lastName);
        reservationDto.setFirstName(firstName);
        reservationDto.setCheckInDate(checkInDate);
        reservationDto.setCheckOutDate(checkOutDate);

        return new HttpEntity<>(reservationDto, headers);
    }

    public HttpEntity<ReservationDto> buildUpdateDatesResponseEntity(LocalDate checkInDate, LocalDate checkOutDate) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setCheckInDate(checkInDate);
        reservationDto.setCheckOutDate(checkOutDate);

        return new HttpEntity<>(reservationDto, headers);
    }

    public HttpEntity<String> buildJsonEntityEmpty() {
        HttpEntity<String> entity;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestJson;
        requestJson = "{}";
        entity = new HttpEntity<>(requestJson,headers);

        return entity;
    }

    public HttpEntity<DateRangeDto> buildDateRangeEntity(LocalDate checkInDate, LocalDate checkOutDate) {
        DateRangeDto dateRangeDto = new DateRangeDto();
        dateRangeDto.setCheckInDate(checkInDate);
        dateRangeDto.setCheckOutDate(checkOutDate);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(dateRangeDto, headers);
    }

    public String createReservation() {
        String responseEntity = restTemplate
                .postForEntity(
                        "http://localhost:" + port + "/reservations",
                        buildCreateReservationRequestEntity(
                                "sampleEmail@email.com",
                                "FirstName",
                                "LastName",
                                LocalDate.now().plusDays(2),
                                LocalDate.now().plusDays(5)
                        ),
                        String.class).getBody();
        return responseEntity.substring(responseEntity.lastIndexOf(": ")+1).replaceAll("\\s+","");
    }

    public String createReservationWithDates(LocalDate checkIn, LocalDate checkOut) {
        String responseEntity = restTemplate
                .postForEntity(
                        "http://localhost:" + port + "/reservations",
                        buildCreateReservationRequestEntity(
                                "sampleEmail@email.com",
                                "FirstName",
                                "LastName",
                                checkIn,
                                checkOut),
                        String.class).getBody();
        return responseEntity.substring(responseEntity.lastIndexOf(": ")+1).replaceAll("\\s+","");
    }
}

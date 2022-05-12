package com.example.campsitereservation;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.example.campsitereservation.entities.DateRangeDto;
import com.example.campsitereservation.entities.ReservationDto;

@SpringBootTest(classes = CampsiteReservationApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Commit
class CampsiteReservationApplicationIT {

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    public HttpEntity<ReservationDto> buildCreateReservationRequestEntity(String email, String firstName, String lastName, LocalDate checkInDate, LocalDate checkOutDate) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

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
}

//Assignment 2 COMP303-002
//Maria Angelina Guerta Ligo 301428777
// Ayushi Parmar 301444746
//10-30-2025

package com.spring.boot.assignment2.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

@Entity
@Table(name = "Reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Integer reservationId;

    @NotNull(message = "Passenger is required.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passengerId;

    @NotNull(message = "Flight is required.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @PastOrPresent(message = "Booking date cannot be in the future.")
    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate = LocalDate.now();

    @Min(value = 1, message = "At least one adult must be included.")
    @Column(name = "no_of_adults", nullable = false)
    private Integer noOfAdults;

    @Min(value = 0, message = "Number of children cannot be negative.")
    @Column(name = "no_of_children")
    private Integer noOfChildren = 0;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "status", length = 50, nullable = false)
    private String status = "Confirmed";

    public Reservation() {}

    public Reservation(@NotNull Passenger passengerId, @NotNull Flight flight, LocalDate bookingDate,
                       Integer noOfAdults, Integer noOfChildren, BigDecimal totalPrice, String status) {
        this.passengerId = passengerId;
        this.flight = flight;
        this.bookingDate = bookingDate;
        this.noOfAdults = noOfAdults;
        this.noOfChildren = noOfChildren;
        this.totalPrice = totalPrice;
        this.status = status;
    }


    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public Passenger getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Passenger passenger) {
        this.passengerId = passenger;
    }

    public Flight getFlightId() {
        return flight;
    }

    public void setFlightId(Flight flight) {
        this.flight = flight;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Integer getNoOfAdults() {
        return noOfAdults;
    }

    public void setNoOfAdults(Integer noOfAdults) {
        this.noOfAdults = noOfAdults;
    }

    public Integer getNoOfChildren() {
        return noOfChildren;
    }

    public void setNoOfChildren(Integer noOfChildren) {
        this.noOfChildren = noOfChildren;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

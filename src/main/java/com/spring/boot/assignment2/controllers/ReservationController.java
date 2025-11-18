//Assignment 2 COMP303-002
//Maria Angelina Guerta Ligo 301428777
// Ayushi Parmar 301444746
//10-30-2025

package com.spring.boot.assignment2.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.spring.boot.assignment2.models.CancelledReservation;
import com.spring.boot.assignment2.models.Flight;
import com.spring.boot.assignment2.models.Passenger;
import com.spring.boot.assignment2.models.Reservation;
import com.spring.boot.assignment2.repositories.CancelledReservationRepository;
import com.spring.boot.assignment2.repositories.FlightRepository;
import com.spring.boot.assignment2.repositories.PassengerRepository;
import com.spring.boot.assignment2.repositories.ReservationRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/reservation")
public class ReservationController {

    private final FlightRepository flightRepository;
    private final ReservationRepository reservationRepository;
    private final PassengerRepository passengerRepository;
    private final CancelledReservationRepository cancelledReservationRepository;

    public ReservationController(FlightRepository flightRepository,
                                 ReservationRepository reservationRepository,
                                 PassengerRepository passengerRepository,
                                 CancelledReservationRepository cancelledReservationRepository) {
        this.flightRepository = flightRepository;
        this.reservationRepository = reservationRepository;
        this.passengerRepository = passengerRepository;
        this.cancelledReservationRepository = cancelledReservationRepository;
    }

    //new reservation
    @GetMapping("/new")
    public String showReservationForm(@RequestParam String username, Model model) {
    	 
        Passenger passenger = passengerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Passenger not found"));

        List<Flight> flights = flightRepository.findAll();

        List<String> origins = flights.stream().map(Flight::getOrigin).distinct().toList();
        List<String> destinations = flights.stream().map(Flight::getDestination).distinct().toList();
        List<String> airlines = flights.stream().map(Flight::getAirlineName).distinct().toList();


        model.addAttribute("passenger", passenger);
        model.addAttribute("origins", origins);
        model.addAttribute("destinations", destinations);
        model.addAttribute("airlines", airlines);
        model.addAttribute("departureTimes", List.of()); 
        model.addAttribute("selectedOrigin", null);
        model.addAttribute("selectedDestination", null);
        model.addAttribute("selectedAirline", null);

        return "new-reservation";
    }

    //show dates and times after user selects the airplane company
    @PostMapping("/load-times")
    public String loadDepartureTimes(@RequestParam String username,
                                     @RequestParam String origin,
                                     @RequestParam String destination,
                                     @RequestParam String airline,
                                     Model model) {

        Passenger passenger = passengerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Passenger not found"));

        List<Flight> flights = flightRepository.findAll();

        List<String> origins = flights.stream().map(Flight::getOrigin).distinct().toList();
        List<String> destinations = flights.stream().map(Flight::getDestination).distinct().toList();
        List<String> airlines = flights.stream().map(Flight::getAirlineName).distinct().toList();

        List<String> departureTimes = flightRepository
                .findByOriginAndDestinationAndAirlineName(origin, destination, airline)
                .stream()
                .map(f -> f.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
                .distinct()
                .toList();
       
        model.addAttribute("origins", origins);
        model.addAttribute("destinations", destinations);
        model.addAttribute("airlines", airlines);
        model.addAttribute("departureTimes", departureTimes);
        model.addAttribute("passenger", passenger);

        // keep selections
        model.addAttribute("selectedOrigin", origin);
        model.addAttribute("selectedDestination", destination);
        model.addAttribute("selectedAirline", airline);

        return "new-reservation";
    }

    //save new reservation
    @PostMapping("/confirm")
    public String confirmReservation(@RequestParam String username,
                                     @RequestParam String origin,
                                     @RequestParam String destination,
                                     @RequestParam String airline,
                                     @RequestParam String departureTime,
                                     @RequestParam int noOfAdults,
                                     @RequestParam int noOfChildren,
                                     Model model) {

        Passenger passenger = passengerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Passenger not found"));

        LocalDateTime depTime = LocalDateTime.parse(departureTime,
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        Flight flight = flightRepository.findByOriginAndDestinationAndAirlineNameAndDepartureTime(
                origin, destination, airline, depTime
        ).orElseThrow(() -> new IllegalArgumentException("Flight not found"));

        BigDecimal adultPrice = flight.getPrice();
        //if its child, 20% 
        BigDecimal childPrice = flight.getPrice().multiply(BigDecimal.valueOf(0.8)); 

        BigDecimal totalAdults = adultPrice.multiply(BigDecimal.valueOf(noOfAdults));
        BigDecimal totalChildren = childPrice.multiply(BigDecimal.valueOf(noOfChildren));

        BigDecimal totalPrice = totalAdults.add(totalChildren);

        Reservation reservation = new Reservation();
        reservation.setPassengerId(passenger);
        reservation.setFlightId(flight);
        reservation.setNoOfAdults(noOfAdults);
        reservation.setNoOfChildren(noOfChildren);
        reservation.setBookingDate(LocalDate.now());
        reservation.setTotalPrice(totalPrice);
        reservation.setStatus("Pending Payment");

        reservationRepository.save(reservation);
        return "redirect:/payment/start?reservationId=" + reservation.getReservationId();
    }

    //show reservations
    @GetMapping("/view")
    public String viewReservations(@RequestParam String username, 
    		Model model, @ModelAttribute("message") String message,
            @ModelAttribute("error") String error) {
    	
        Passenger passenger = passengerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Passenger not found"));

        List<Reservation> reservations = reservationRepository.findByPassengerId(passenger);
        List<CancelledReservation> cancelledReservations = cancelledReservationRepository.findByUsername(username);

        model.addAttribute("username", username);
        model.addAttribute("passenger", passenger);
        model.addAttribute("reservations", reservations);
        model.addAttribute("cancelledReservations", cancelledReservations);
        if (message != null && !message.isEmpty()) {
            model.addAttribute("message", message);
        }
        if (error != null && !error.isEmpty()) {
            model.addAttribute("error", error);
        }

        return "view-reservation"; 
    }

    //edit a reservation only for passengers and dates
    @GetMapping("/edit/{id}")
    public String editReservation(@PathVariable Integer id, Model model) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
       

        Flight flight = reservation.getFlightId();

        List<Flight> matchingFlights = flightRepository.findByOriginAndDestinationAndAirlineName(
                flight.getOrigin(), flight.getDestination(), flight.getAirlineName()
        );
        List<LocalDateTime> departureTimes = matchingFlights.stream()
                .map(Flight::getDepartureTime)
                .toList();

        model.addAttribute("reservation", reservation);
        model.addAttribute("departureTimes", departureTimes);
        model.addAttribute("passenger", reservation.getPassengerId());

        return "edit-reservation";
    }

    //save new reservation
    @PostMapping("/update")
    public String updateReservation(
            @RequestParam Integer id,
            @RequestParam String departureTime,  
            @RequestParam int noOfAdults,
            @RequestParam int noOfChildren) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        LocalDateTime depTime = LocalDateTime.parse(departureTime,
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

        Flight newFlight = flightRepository.findByOriginAndDestinationAndAirlineNameAndDepartureTime(
                reservation.getFlightId().getOrigin(),
                reservation.getFlightId().getDestination(),
                reservation.getFlightId().getAirlineName(),
                depTime
        ).orElseThrow(() -> new IllegalArgumentException("Flight not found"));

        reservation.setFlightId(newFlight);
        reservation.setNoOfAdults(noOfAdults);
        reservation.setNoOfChildren(noOfChildren);
        reservation.setTotalPrice(newFlight.getPrice()
                .multiply(BigDecimal.valueOf(noOfAdults + noOfChildren)));

        reservationRepository.save(reservation);

        return "redirect:/reservation/view?username=" + reservation.getPassengerId().getUsername();
    }

    //cancel reservation that deletes from database, but still shows in the view as cancelled 
    @GetMapping("/cancel/{id}")
    @Transactional
    public String cancelReservation(@PathVariable("id") Integer id,
                                    RedirectAttributes redirectAttributes) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reservation ID: " + id));

        LocalDateTime departureTime = reservation.getFlightId().getDepartureTime();
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(departureTime.minusDays(10))) {
            redirectAttributes.addFlashAttribute("error",
                    "Cancellation not allowed within 10 days of departure.");
            return "redirect:/reservation/view?username=" + reservation.getPassengerId().getUsername();
        }
        CancelledReservation cancelled = new CancelledReservation();
        cancelled.setUsername(reservation.getPassengerId().getUsername());
        cancelled.setOrigin(reservation.getFlightId().getOrigin());
        cancelled.setDestination(reservation.getFlightId().getDestination());
        cancelled.setAirline(reservation.getFlightId().getAirlineName());
        cancelled.setDepartureTime(reservation.getFlightId().getDepartureTime());
        cancelled.setArrivalTime(reservation.getFlightId().getArrivalTime());
        cancelled.setNoOfAdults(reservation.getNoOfAdults());
        cancelled.setNoOfChildren(reservation.getNoOfChildren());
        cancelled.setTotalPrice(reservation.getTotalPrice());
        cancelled.setBookingDate(reservation.getBookingDate());
        cancelled.setStatus("Cancelled");

        cancelledReservationRepository.save(cancelled);

        reservationRepository.delete(reservation);

        redirectAttributes.addFlashAttribute("message", "Reservation successfully cancelled.");

        return "redirect:/reservation/view?username=" + reservation.getPassengerId().getUsername();
    }
    
    //delete the cancelled reservation in the view
    @GetMapping("/delete/{id}")
    @Transactional
    public String deleteCancelledReservation(@PathVariable("id") Integer id,
                                             RedirectAttributes redirectAttributes) {
        var cancelled = cancelledReservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cancelled reservation not found"));

        String username = cancelled.getUsername();

        cancelledReservationRepository.deleteById(id);

        redirectAttributes.addFlashAttribute("message", "Cancelled reservation deleted successfully.");

        return "redirect:/reservation/view?username=" + username;
    }


   
}

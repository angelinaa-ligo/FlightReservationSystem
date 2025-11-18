//Assignment 2 COMP303-002
//Maria Angelina Guerta Ligo 301428777
// Ayushi Parmar 301444746
//10-30-2025

package com.spring.boot.assignment2.controllers;

import com.spring.boot.assignment2.models.Reservation;
import com.spring.boot.assignment2.repositories.ReservationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final ReservationRepository reservationRepository;

    public PaymentController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    //payment for passenger
    @GetMapping("/start")
    public String showPayment(@RequestParam("reservationId") Integer reservationId, Model model) {
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        if (r.getPassengerId() == null) {
            throw new IllegalStateException("Reservation has no passenger assigned");
        }

        model.addAttribute("reservation", r);
        return "payment"; 
    }

    //sending payment locally
    @PostMapping("/confirm")
    public String confirmPayment(
            @RequestParam("reservationId") Integer reservationId,
            @RequestParam("method") String method,
            RedirectAttributes ra) {

        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        r.setStatus("Confirmed");
        reservationRepository.save(r);

        ra.addFlashAttribute("message", "Payment successful via " + method + ". Reservation marked as Confirmed.");
        return "redirect:/reservation/view?username=" + r.getPassengerId().getUsername();
    }

    //saving status as pending payment
    @GetMapping("/cancel")
    public String cancelPayment(@RequestParam("reservationId") Integer reservationId) {
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        return "redirect:/reservation/view?username=" + r.getPassengerId().getUsername();
    }
}

//Assignment 2 COMP303-002
//Maria Angelina Guerta Ligo 301428777
// Ayushi Parmar 301444746
//10-30-2025

package com.spring.boot.assignment2.controllers;

import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.spring.boot.assignment2.models.Passenger;
import com.spring.boot.assignment2.models.Reservation;
import com.spring.boot.assignment2.repositories.PassengerRepository;

@Controller
public class PassengerController {

    @Autowired
    private PassengerRepository passengerRepository;

   //index page 
    @GetMapping("/")
    public String showLoginPage(Model model) {
        model.addAttribute("passenger", new Passenger());
        return "index";
    }

    //login to reservation
    @PostMapping("/")
    public String signIn(@ModelAttribute Passenger passenger, Model model) {
        Optional<Passenger> existingOpt = passengerRepository.findByUsername(passenger.getUsername());

        if (existingOpt.isPresent() && existingOpt.get().getPassword().equals(passenger.getPassword())) {
            Passenger existing = existingOpt.get();

            return "redirect:/reservation/new?username=" + existing.getUsername();
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "index";
        }
    }

    //create new account
    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("passenger", new Passenger());
        return "signup";
    }

    //create account in database
    @PostMapping("/signup")
    public String registerPassenger(
            @Valid @ModelAttribute("passenger") Passenger passenger,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            System.out.println("Validation errors: " + result.getAllErrors());
            return "signup";
        }

        Optional<Passenger> existing = passengerRepository.findByUsername(passenger.getUsername());
        if (existing.isPresent()) {
            model.addAttribute("usernameError", "Username already exists!");
            return "signup";
        }
        passengerRepository.save(passenger);
        System.out.println("Saved passenger: " + passenger.getUsername());
        return "redirect:/";
    }

    //edit profile
    @GetMapping("/edit")
    public String showEditProfileForm(@RequestParam String username, Model model) {
        Passenger passenger = passengerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Passenger not found"));

        model.addAttribute("passenger", passenger);
        return "edit-profile"; // Thymeleaf template
    }
    
    //update in database
    @PostMapping("/edit")
    public String updateProfile(@RequestParam String username,
                                @RequestParam String address,
                                @RequestParam String city,
                                @RequestParam String postalcode,
                                @RequestParam String password,
                                Model model) {
        Passenger passenger = passengerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Passenger not found"));

        if (address == null || address.isBlank() ||
            city == null || city.isBlank() ||
            postalcode == null || postalcode.isBlank() ||
            password == null || password.isBlank()) {

            model.addAttribute("errorMessage", "All fields are required. Please fill out every field.");
            model.addAttribute("passenger", passenger);
            return "edit-profile"; 
        }

        if (!postalcode.matches("^[A-Za-z]\\d[A-Za-z][ ]?\\d[A-Za-z]\\d$")) {
            model.addAttribute("errorMessage", "Invalid postal code format (e.g. A1B 2C3).");
            model.addAttribute("passenger", passenger);
            return "edit-profile";
        }

        if (password.length() < 6) {
            model.addAttribute("errorMessage", "Password must be at least 6 characters long.");
            model.addAttribute("passenger", passenger);
            return "edit-profile";
        }
        passenger.setAddress(address);
        passenger.setCity(city);
        passenger.setPostalcode(postalcode);
        passenger.setPassword(password);

        passengerRepository.save(passenger);

        model.addAttribute("passenger", passenger);
        model.addAttribute("successMessage", "Profile updated successfully!");

        return "redirect:/reservation/new?username=" + username;
    }

}


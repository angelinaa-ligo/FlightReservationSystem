//Assignment 2 COMP303-002
//Maria Angelina Guerta Ligo 301428777
// Ayushi Parmar 301444746
//10-30-2025

package com.spring.boot.assignment2.repositories;

import com.spring.boot.assignment2.models.CancelledReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CancelledReservationRepository extends JpaRepository<CancelledReservation, Integer> {
	
	//find the cancelled reservations by username
    List<CancelledReservation> findByUsername(String username);
}

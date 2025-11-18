//Assignment 2 COMP303-002
//Maria Angelina Guerta Ligo 301428777
// Ayushi Parmar 301444746
//10-30-2025

package com.spring.boot.assignment2.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.spring.boot.assignment2.models.Passenger;
import com.spring.boot.assignment2.models.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
	
	//get reservations by passenger
	List<Reservation> findByPassengerId(Passenger passenger);
}
   


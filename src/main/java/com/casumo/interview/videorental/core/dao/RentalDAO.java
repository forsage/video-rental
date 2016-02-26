package com.casumo.interview.videorental.core.dao;

import com.casumo.interview.videorental.api.Customer;
import com.casumo.interview.videorental.api.Film;
import com.casumo.interview.videorental.api.Rental;
import com.google.common.base.Optional;

import java.util.Date;
import java.util.List;

public interface RentalDAO {
	Rental create(Rental rental);

	Optional<Rental> findById(int id);

	List<Rental> findAll();

	List<Rental> findByCustomer(Customer customer);

	List<Rental> findByFilm(Film input);

	void updateReturnedOn(Rental rental, Date date);

	void updateNormalPrice(Rental rental, int normalPrice);

	void updateLateCharge(Rental rental, int lateCharge);
}

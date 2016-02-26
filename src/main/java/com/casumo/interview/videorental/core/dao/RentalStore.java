package com.casumo.interview.videorental.core.dao;

import com.casumo.interview.videorental.Sequence;
import com.casumo.interview.videorental.api.Customer;
import com.casumo.interview.videorental.api.Film;
import com.casumo.interview.videorental.api.Rental;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class RentalStore implements RentalDAO {
	private final Sequence sequence;

	private final List<Rental> rentals = Lists.newArrayList();

	public RentalStore(final Sequence sequence) {
		this.sequence = sequence;
	}

	@Override
	public Rental create(final Rental external) {
		Preconditions.checkNotNull(external, "rental has to be set");

		final Rental rental = new Rental();

		rental.setId(sequence.nextVal());
		rental.setCustomer(external.getCustomer());
		rental.setDaysRentedFor(external.getDaysRentedFor());
		rental.setRentedOn(new Date());
		rental.getFilmsRented().addAll(external.getFilmsRented());

		rentals.add(rental);

		return rental;
	}

	@Override
	public Optional<Rental> findById(final int id) {
		return Iterables.tryFind(rentals, new Predicate<Rental>() {
			@Override
			public boolean apply(final Rental input) {
				return input.getId() == id;
			}
		});
	}

	@Override
	public List<Rental> findAll() {
		return rentals;
	}

	@Override
	public List<Rental> findByCustomer(final Customer customer) {
		Preconditions.checkNotNull(customer, "customer has to be filled");
		return Lists.newArrayList(Iterables.filter(rentals, new Predicate<Rental>() {
			@Override
			public boolean apply(final Rental input) {
				return input.getCustomer().equals(customer);
			}
		}));
	}

	@Override
	public List<Rental> findByFilm(final Film film) {
		Preconditions.checkNotNull(film, "film has to be filled");
		return Lists.newArrayList(Iterables.filter(rentals, new Predicate<Rental>() {
			@Override
			public boolean apply(final Rental input) {
				return input.getFilmsRented().contains(film);
			}
		}));
	}

	@Override
	public void updateReturnedOn(final Rental rental, final Date returnedOn) {
		Preconditions.checkNotNull(rental, "rental has to be filled");
		Preconditions.checkNotNull(returnedOn, "returnedOn has to be filled");
		Preconditions.checkArgument(returnedOn.after(rental.getRentedOn()), "returnedOn has to come after rentedOn");

		rental.setReturnedOn(returnedOn);
	}

	@Override
	public void updateNormalPrice(final Rental rental, final int normalPrice) {
		Preconditions.checkNotNull(rental, "rental has to be filled");
		rental.setNormalPrice(normalPrice);
	}

	@Override
	public void updateLateCharge(final Rental rental, final int lateCharge) {
		Preconditions.checkNotNull(rental, "rental has to be filled");
		rental.setLateCharge(lateCharge);
	}
}

package com.casumo.interview.videorental.core.price;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.casumo.interview.videorental.api.Age;
import com.casumo.interview.videorental.api.Film;
import com.casumo.interview.videorental.api.Rental;
import com.google.common.collect.Lists;

public class LateChargeCalculatorTest {

	private final PriceCalculator lateChargeCalculator = new LateChargeCalculator();

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	@Test
	public void calculateWithEmptyDatesThrowsException() {
		final Rental withEmptyDates = createRental(Collections.<Film> emptyList());
		withEmptyDates.setRentedOn(null);
		withEmptyDates.setReturnedOn(null);

		expectedException.expect(NullPointerException.class);

		lateChargeCalculator.calculate(withEmptyDates);
	}

	private Rental createRental(final List<Film> films) {
		final Rental rental = new Rental();

		rental.setFilmsRented(films);

		return rental;
	}

	@Test
	public void calculateWithWrongDatesThrowsException() {
		final Rental withEmptyDates = createRental(Collections.<Film> emptyList());
		final Date now = new Date();
		withEmptyDates.setRentedOn(now);
		withEmptyDates.setReturnedOn(now);

		expectedException.expect(IllegalArgumentException.class);

		lateChargeCalculator.calculate(withEmptyDates);
	}

	@Test
	public void calculateInTimeGivesZeroLateCharge() {
		final Rental inTime = createRental(Collections.<Film> emptyList());
		final int daysRentedFor = 1;
		final int daysOfReturn = 1;
		setDateTimeAttributes(inTime, daysRentedFor, daysOfReturn);

		final int lateCharge = lateChargeCalculator.calculate(inTime);

		assertThat(lateCharge).isEqualTo(0);
	}

	private void setDateTimeAttributes(final Rental rental, final int daysRentedFor, final int daysOfReturn) {
		rental.setDaysRentedFor(daysRentedFor);
		final DateTime today = new DateTime().withTimeAtStartOfDay();
		final DateTime future = today.plusDays(daysOfReturn).withTimeAtStartOfDay();
		rental.setRentedOn(today.toDate());
		rental.setReturnedOn(future.toDate());
	}

	@Test
	public void calculateLateEmptyGivesZeroLateCharge() {
		final Rental lateEmpty = createRental(Collections.<Film> emptyList());
		setDateTimeAttributes(lateEmpty, 1, 2);

		final int lateCharge = lateChargeCalculator.calculate(lateEmpty);

		assertThat(lateCharge).isEqualTo(0);
	}

	@Test
	public void calculateLateWithBrokenFilmThrowsException() {
		final Rental lateWithBrokenFilm = createRental(Lists.newArrayList(createFilm(null)));
		setDateTimeAttributes(lateWithBrokenFilm, 1, 2);

		expectedException.expect(NullPointerException.class);

		lateChargeCalculator.calculate(lateWithBrokenFilm);
	}

	@Test
	public void calculateLateWithFilmsGivesCorrectBonus() {
		final Rental lateWithFilms = createRental(Lists.newArrayList(createFilm(Age.NEW), createFilm(Age.NORMAL), createFilm(Age.OLD)));
		setDateTimeAttributes(lateWithFilms, 4, 7);

		final int bonus = lateChargeCalculator.calculate(lateWithFilms);

		assertThat(bonus).isEqualTo(120 + 90 + 90);
	}

	private Film createFilm(final Age age) {
		final Film film = new Film();

		film.setAge(age);

		return film;
	}

}
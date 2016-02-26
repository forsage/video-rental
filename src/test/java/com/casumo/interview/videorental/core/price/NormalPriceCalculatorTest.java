package com.casumo.interview.videorental.core.price;

import com.casumo.interview.videorental.api.Age;
import com.casumo.interview.videorental.api.Film;
import com.casumo.interview.videorental.api.Rental;
import com.google.common.collect.Lists;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class NormalPriceCalculatorTest {

	private final PriceCalculator normalPriceCalculator = new NormalPriceCalculator();

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	@Test
	public void calculateEmptyRentalGivesZeroNormalPrice() {
		final Rental empty = createRental(Collections.<Film> emptyList());

		final int bonus = normalPriceCalculator.calculate(empty);

		assertThat(bonus).isEqualTo(0);
	}

	private Rental createRental(final List<Film> films) {
		final Rental withFilms = new Rental();

		withFilms.setFilmsRented(films);

		return withFilms;
	}

	@Test
	public void calculateRentalWithBrokenFilmThrowsException() {
		final Rental withBrokenFilm = createRental(Lists.newArrayList(createFilm(null)));

		expectedException.expect(NullPointerException.class);

		normalPriceCalculator.calculate(withBrokenFilm);
	}

	@Test
	public void calculateRentalWithFilmsGivesCorrectNormalPrice() {
		final Rental withFilms = createRental(Lists.newArrayList(createFilm(Age.NEW), createFilm(Age.NORMAL), createFilm(Age.OLD)));
		withFilms.setDaysRentedFor(7);

		final int bonus = normalPriceCalculator.calculate(withFilms);

		assertThat(bonus).isEqualTo(280 + 150 + 90);
	}

	private Film createFilm(final Age age) {
		final Film film = new Film();

		film.setAge(age);

		return film;
	}

}
package com.casumo.interview.videorental.core.dao;

import com.casumo.interview.videorental.Sequence;
import com.casumo.interview.videorental.api.Age;
import com.casumo.interview.videorental.api.Film;
import com.casumo.interview.videorental.api.Rental;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FilmStoreTest {

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	private FilmDAO filmDAO;

	@Mock
	private RentalDAO rentalDAO;

	@Before
	public void setUp() throws Exception {
		final Sequence sequence = new Sequence();
		filmDAO = new FilmStore(sequence, rentalDAO);
	}

	@Test
	public void createFromNullThrowsException() {
		expectedException.expect(NullPointerException.class);

		filmDAO.create(null);
	}

	@Test
	public void createFilledPerformsCorrectly() {
		final Film film = createFilm();

		final Film created = filmDAO.create(film);

		assertThat(created.getId()).isEqualTo(1);
		assertThat(created.getTitle()).isEqualTo(film.getTitle());
		assertThat(created.getAge()).isEqualTo(film.getAge());
	}

	private Film createFilm() {
		final Film film = new Film();

		film.setTitle("The Beauty And The Beast");
		film.setAge(Age.NEW);

		return film;
	}

	@Test
	public void findByAbsentId() {
		final Optional<Film> maybeFilm = filmDAO.findById(0);

		assertThat(maybeFilm.isPresent()).isFalse();
	}

	@Test
	public void findByPresentId() {
		final Film film = createFilm();

		final Film created = filmDAO.create(film);
		final Optional<Film> maybeFilm = filmDAO.findById(created.getId());

		assertThat(maybeFilm.isPresent()).isTrue();
		assertThat(maybeFilm.get()).isEqualTo(created);
	}

	@Test
	public void findAllEmpty() {
		final List<Film> empty = filmDAO.findAll();

		assertThat(empty).isEmpty();
	}

	@Test
	public void findAllFilled() {
		final Film filled = createFilm();
		final Film firstExisting = filmDAO.create(filled);
		final Film secondExisting = filmDAO.create(filled);

		final List<Film> films = filmDAO.findAll();

		assertThat(films.size()).isEqualTo(2);
		assertThat(films.get(0)).isEqualTo(firstExisting);
		assertThat(films.get(1)).isEqualTo(secondExisting);
	}

	@Test
	public void findAvailableNoRentalDone() {
		when(rentalDAO.findByFilm(any(Film.class))).thenReturn(Collections.<Rental> emptyList());

		final Film filled = createFilm();
		final Film firstExisting = filmDAO.create(filled);
		final Film secondExisting = filmDAO.create(filled);

		final List<Film> availableFilms = filmDAO.findAvailable();

		assertThat(availableFilms.size()).isEqualTo(2);
		assertThat(availableFilms.get(0)).isEqualTo(firstExisting);
		assertThat(availableFilms.get(1)).isEqualTo(secondExisting);
	}

	@Test
	public void findAvailableOneRentalDone() {
		final Film filled = createFilm();
		final Film firstExisting = filmDAO.create(filled);
		final Film secondExisting = filmDAO.create(filled);
		final Rental withOneFilm = createRental(firstExisting);
		when(rentalDAO.findByFilm(firstExisting)).thenReturn(Lists.newArrayList(withOneFilm));

		final List<Film> availableFilms = filmDAO.findAvailable();

		assertThat(availableFilms.size()).isEqualTo(1);
		assertThat(availableFilms.get(0)).isEqualTo(secondExisting);
	}

	private Rental createRental(final Film... films) {
		final Rental rental = new Rental();

		rental.setFilmsRented(Lists.newArrayList(films));

		return rental;
	}

	@Test
	public void findAvailableTwoRentalsDone() {
		final Film filled = createFilm();
		final Film firstExisting = filmDAO.create(filled);
		final Film secondExisting = filmDAO.create(filled);
		final Film thirdExisting = filmDAO.create(filled);
		final Rental withFirstFilm = createRental(firstExisting);
		final Rental withSecondFilm = createRental(secondExisting);
		when(rentalDAO.findByFilm(firstExisting)).thenReturn(Lists.newArrayList(withFirstFilm));
		when(rentalDAO.findByFilm(secondExisting)).thenReturn(Lists.newArrayList(withSecondFilm));

		final List<Film> availableFilms = filmDAO.findAvailable();

		assertThat(availableFilms.size()).isEqualTo(1);
		assertThat(availableFilms.get(0)).isEqualTo(thirdExisting);
	}
}
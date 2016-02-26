package com.casumo.interview.videorental.core.dao;

import com.casumo.interview.videorental.Sequence;
import com.casumo.interview.videorental.api.Film;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;

public class FilmStore implements FilmDAO {
	private final Sequence sequence;
	private final RentalDAO rentalDAO;
	private final List<Film> films = Lists.newArrayList();

	public FilmStore(final Sequence sequence, final RentalDAO rentalDAO) {
		this.sequence = sequence;
		this.rentalDAO = rentalDAO;
	}

	@Override
	public Film create(final Film external) {
		Preconditions.checkNotNull(external, "film has to be set");

		final Film film = new Film();

		film.setId(sequence.nextVal());
		film.setTitle(external.getTitle());
		film.setAge(external.getAge());

		films.add(film);

		return film;
	}

	@Override
	public Optional<Film> findById(final int id) {
		return Iterables.tryFind(films, new Predicate<Film>() {
			@Override
			public boolean apply(final Film input) {
				return input.getId() == id;
			}
		});
	}

	@Override
	public List<Film> findAll() {
		return films;
	}

	@Override
	public List<Film> findAvailable() {
		return Lists.newArrayList(Iterables.filter(films, new Predicate<Film>() {
			@Override
			public boolean apply(final Film input) {
				return rentalDAO.findByFilm(input).size() == 0;
			}
		}));
	}
}

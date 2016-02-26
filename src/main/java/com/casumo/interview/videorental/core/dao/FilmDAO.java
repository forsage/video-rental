package com.casumo.interview.videorental.core.dao;

import com.casumo.interview.videorental.api.Age;
import com.casumo.interview.videorental.api.Film;
import com.google.common.base.Optional;

import java.util.List;

public interface FilmDAO {
	Film create(Film film);

	Optional<Film> findById(int id);

	List<Film> findAll();

	List<Film> findAvailable();
}

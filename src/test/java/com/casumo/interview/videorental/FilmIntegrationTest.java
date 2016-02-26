package com.casumo.interview.videorental;

import com.casumo.interview.videorental.api.Film;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FilmIntegrationTest extends IntegrationTestActions {

	@Test
	public void testFilmResource() {
		final Film film = createAndCheckFilm();

		final List<Film> allFilms = getAllFilms();

		assertThat(allFilms.size()).isEqualTo(1);
		assertThat(allFilms.get(0)).isEqualTo(film);
	}
}

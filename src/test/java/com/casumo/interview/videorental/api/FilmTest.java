package com.casumo.interview.videorental.api;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;

public class FilmTest {

	private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

	@Test
	public void whenSerializedToJSONValuesAreWritten() throws Exception {
		final Film film = createFilm();

		final String expected = MAPPER.writeValueAsString(MAPPER.readValue(createFixture(), Film.class));

		assertThat(MAPPER.writeValueAsString(film)).isEqualTo(expected);
	}

	@Test
	public void whenDeserializedFromJSONValuesAreRead() throws Exception {
		final Film film = createFilm();

		assertThat(MAPPER.readValue(createFixture(), Film.class)).isEqualTo(film);
	}

	private Film createFilm() {
		final Film film = new Film();

		film.setId(1);
		film.setTitle("The Beauty And The Beast");
		film.setAge(Age.NEW);

		return film;
	}

	private String createFixture() {
		return fixture("fixtures/film.json");
	}
}
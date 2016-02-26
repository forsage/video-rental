package com.casumo.interview.videorental.resources;

import com.casumo.interview.videorental.api.Age;
import com.casumo.interview.videorental.api.Film;
import com.casumo.interview.videorental.core.dao.FilmDAO;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FilmResourceTest {

	private static final FilmDAO filmDAO = mock(FilmDAO.class);

	@ClassRule
	public static final ResourceTestRule resources = ResourceTestRule.builder()
			.addResource(new FilmResource(filmDAO))
			.build();

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Captor
	private ArgumentCaptor<Film> filmCaptor;
	private final Film film;
	private final List<Film> films;
	{
		film = new Film();

		film.setId(1);
		film.setTitle("The Beauty And The Beast");
		film.setAge(Age.NEW);

		films = Lists.newArrayList(film);
	}

	@Before
	public void setUp() throws Exception {
		when(filmDAO.findById(any(Integer.class))).thenReturn(Optional.of(film));
		when(filmDAO.findAll()).thenReturn(films);
		when(filmDAO.findAvailable()).thenReturn(films);
		when(filmDAO.create(any(Film.class))).thenReturn(film);
	}

	@After
	public void tearDown() throws Exception {
		reset(filmDAO);
	}

	@Test
	public void getAllFoundCorrectly() {
		final List<Film> found = resources.client()
				.target("/film/getAll")
				.request()
				.get(new GenericType<List<Film>>() {
				});

		assertThat(found.get(0).getId()).isEqualTo(films.get(0).getId());

		verify(filmDAO).findAll();
	}

	@Test
	public void getAvailableFoundCorrectly() {
		final List<Film> found = resources.client()
				.target("/film/getAvailable")
				.request()
				.get(new GenericType<List<Film>>() {
				});

		assertThat(found.get(0).getId()).isEqualTo(films.get(0).getId());

		verify(filmDAO).findAvailable();
	}

	@Test
	public void getWithTooShortPrefixExceptionIsThrown() {
		final String tooShortTitle = "aa";

		expectedException.expect(ProcessingException.class);
		expectedException.expectMessage("Server-side request processing failed with an error.");

		resources.client()
				.target(String.format("/film/getWithPrefix/%s", tooShortTitle))
				.request(MediaType.APPLICATION_JSON_TYPE)
				.get();
	}

	@Test
	public void getWithAbsentPrefixNothingIsFound() {
		final String absentTitlePrefix = "aaa";

		final List<Film> found = resources.client()
				.target(String.format("/film/getWithPrefix/%s", absentTitlePrefix))
				.request(MediaType.APPLICATION_JSON_TYPE)
				.get(new GenericType<List<Film>>() {});

		assertThat(found.size()).isEqualTo(0);

		verify(filmDAO).findAll();
	}

	@Test
	public void getWithPresentPrefixFoundCorrectly() {
		final String presentTitlePrefix = "The B";

		final List<Film> found = resources.client()
				.target(String.format("/film/getWithPrefix/%s", presentTitlePrefix))
				.request(MediaType.APPLICATION_JSON_TYPE)
				.get(new GenericType<List<Film>>() {});

		assertThat(found.size()).isEqualTo(1);
		assertThat(found.get(0)).isEqualTo(film);

		verify(filmDAO).findAll();
	}

	@Test
	public void testCreatingWithParametersCreatedCorrectly() {
		final Response response = resources.client()
				.target("/film/create")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.entity(film, MediaType.APPLICATION_JSON_TYPE));

		assertThat(response.getStatusInfo()).isEqualTo(Response.Status.OK);

		verify(filmDAO).create(filmCaptor.capture());
		assertThat(filmCaptor.getValue()).isEqualTo(film);
	}
}
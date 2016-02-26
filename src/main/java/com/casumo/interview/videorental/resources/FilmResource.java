package com.casumo.interview.videorental.resources;

import com.casumo.interview.videorental.core.dao.FilmDAO;
import com.casumo.interview.videorental.api.*;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/film")
@Produces(MediaType.APPLICATION_JSON)
public class FilmResource {
	private static final Logger logger = LoggerFactory.getLogger(FilmResource.class);

	private final FilmDAO filmDAO;

	public FilmResource(final FilmDAO filmDAO) {
		this.filmDAO = filmDAO;
	}

	@GET
	@Path("/getAll")
	public List<Film> getAll() {
		logger.debug("Getting all the films from the inventory.");

		return filmDAO.findAll();
	}

	@GET
	@Path("/getAvailable")
	public List<Film> getAvailable() {
		logger.debug("Getting the available films from the inventory.");

		return filmDAO.findAvailable();
	}

	@GET
	@Path("/getWithPrefix/{prefix}")
	public List<Film> getWithPrefix(@PathParam("prefix") final String prefix) {
		logger.debug("Getting films title beginning with [prefix={}]", prefix);

		Preconditions.checkArgument(!Strings.isNullOrEmpty(prefix), "prefix has to be filled");
		Preconditions.checkArgument(prefix.length() > 2, "prefix has to contain at least three characters");

		return Lists.newArrayList(Iterables.filter(getAll(), new Predicate<Film>() {
			@Override
			public boolean apply(final Film input) {
				return input.getTitle().startsWith(prefix);
			}
		}));
	}

	@POST
	@Path("/create")
	public Film create(final Film film) {
		logger.debug("Adding a film to the inventory. [film={}]", film);

		return filmDAO.create(film);
	}
}

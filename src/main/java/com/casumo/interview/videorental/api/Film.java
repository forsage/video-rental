package com.casumo.interview.videorental.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public class Film {
	private int id;
	private String title;
	private Age age;

	public Film() {

	}

	@JsonProperty
	public int getId() {
		return id;
	}

	@JsonProperty
	public void setId(final int id) {
		this.id = id;
	}

	@JsonProperty
	public String getTitle() {
		return title;
	}

	@JsonProperty
	public void setTitle(final String title) {
		this.title = title;
	}

	@JsonProperty
	public Age getAge() {
		return age;
	}

	@JsonProperty
	public void setAge(final Age age) {
		this.age = age;
	}

	@Override
	public String toString() {
		// @formatter:off
		return MoreObjects.toStringHelper(this).omitNullValues()
				.add("id", id)
				.add("title", title)
				.add("age", age)
				.toString();
		// @formatter:off
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final Film film = (Film) o;

		return id == film.id;

	}

	@Override
	public int hashCode() {
		return id;
	}
}

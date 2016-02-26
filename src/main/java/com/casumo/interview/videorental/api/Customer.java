package com.casumo.interview.videorental.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public class Customer {
	private int id;
	private String name;
	private int bonus;
	private int balance;

	public Customer() {
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
	public String getName() {
		return name;
	}

	@JsonProperty
	public void setName(final String name) {
		this.name = name;
	}

	@JsonProperty
	public int getBonus() {
		return bonus;
	}

	@JsonProperty
	public void setBonus(final int bonus) {
		this.bonus = bonus;
	}

	@JsonProperty
	public int getBalance() {
		return balance;
	}

	@JsonProperty
	public void setBalance(final int balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		// @formatter:off
		return MoreObjects.toStringHelper(this).omitNullValues()
				.add("id", id)
				.add("name", name)
				.add("bonus", bonus)
				.add("balance", balance)
				.toString();
		// @formatter:on
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final Customer customer = (Customer) o;

		return id == customer.id;

	}

	@Override
	public int hashCode() {
		return id;
	}
}

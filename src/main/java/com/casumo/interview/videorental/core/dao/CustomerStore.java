package com.casumo.interview.videorental.core.dao;

import com.casumo.interview.videorental.Sequence;
import com.casumo.interview.videorental.api.Customer;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;

public class CustomerStore implements CustomerDAO {
	private final Sequence sequence;

	private final List<Customer> customers = Lists.newArrayList();

	public CustomerStore(final Sequence sequence) {
		this.sequence = sequence;
	}

	@Override
	public Customer create(final Customer external) {
		Preconditions.checkNotNull(external, "customer has to be set");

		final Customer customer = new Customer();

		customer.setId(sequence.nextVal());
		customer.setName(external.getName());
		customer.setBalance(external.getBalance());
		customer.setBonus(external.getBonus());

		customers.add(customer);

		return customer;
	}

	@Override
	public Optional<Customer> findById(final int id) {
		return Iterables.tryFind(customers, new Predicate<Customer>() {
			@Override
			public boolean apply(final Customer input) {
				return input.getId() == id;
			}
		});
	}

	@Override
	public List<Customer> findAll() {
		return customers;
	}

	@Override
	public void withdrawBalance(final Customer customer, final int amount) {
		Preconditions.checkNotNull(customer, "customer has to be set");
		Preconditions.checkArgument(customer.getBalance() >= amount, String.format("customer must have at least [%s] before withdrawal", amount));

		customer.setBalance(customer.getBalance() - amount);
	}

	@Override
	public void addBonus(final Customer customer, final int bonus) {
		Preconditions.checkNotNull(customer, "customer has to be set");
		Preconditions.checkArgument(bonus > 0, "bonus has to be positive");

		customer.setBonus(customer.getBonus() + bonus);
	}

	@Override
	public void delete(final Customer customer) {
		Preconditions.checkNotNull(customer, "customer has to be set");

		customers.remove(customer);
	}
}

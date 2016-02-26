package com.casumo.interview.videorental;

import java.util.concurrent.atomic.AtomicInteger;

public class Sequence {
	private final AtomicInteger counter = new AtomicInteger(0);

	public int nextVal() {
		return counter.incrementAndGet();
	}
}

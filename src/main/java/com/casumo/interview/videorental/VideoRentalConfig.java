package com.casumo.interview.videorental;

import io.dropwizard.Configuration;

public class VideoRentalConfig extends Configuration {
	public static final int PREMIUM_PRICE = 40;
	public static final int BASIC_PRICE = 30;
	public static final int NORMAL_DAYS = 3;
	public static final int OLD_DAYS = 5;
	public static final int PREMIUM_BONUS = 2;
	public static final int BASIC_BONUS = 1;
	public static final int CUSTOMER_DEPOSIT = 100;
	public static final int START_BALANCE = CUSTOMER_DEPOSIT * 10;
}

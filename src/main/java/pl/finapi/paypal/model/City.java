package pl.finapi.paypal.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class City {

	public static final City NO_CITY = new City("                   ");
	private final String city;

	public City(String city) {
		this.city = city;
	}

	public String getCity() {
		return city;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}

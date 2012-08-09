package pl.finapi.paypal.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;

public class Day implements Comparable<Day> {

	private final int year;
	private final int month;
	private final int day;

	public Day(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDay() {
		return day;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int compareTo(Day o) {
		if (o.year == year && o.month == month && o.day == day) {
			return 0;
		}
		boolean after = new DateTime(year, month, day, 0, 0, 0).isAfter(new DateTime(o.year, o.month, o.day, 0, 0, 0));
		return after ? +1 : -1;
	}

}

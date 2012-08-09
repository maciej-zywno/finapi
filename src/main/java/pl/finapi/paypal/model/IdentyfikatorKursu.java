package pl.finapi.paypal.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class IdentyfikatorKursu {

	private final String identyfikatorKursu;
	private final IdentyfikatorKursuSource identyfikatorKursuSource;
	private final Day day;

	public IdentyfikatorKursu(String identyfikatorKursu, IdentyfikatorKursuSource identyfikatorKursuSource, Day day) {
		this.identyfikatorKursu = identyfikatorKursu;
		this.identyfikatorKursuSource = identyfikatorKursuSource;
		this.day = day;
	}

	public String getIdentyfikatorKursu() {
		return identyfikatorKursu;
	}

	public IdentyfikatorKursuSource getIdentyfikatorKursuSource() {
		return identyfikatorKursuSource;
	}

	public Day getDay() {
		return day;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}

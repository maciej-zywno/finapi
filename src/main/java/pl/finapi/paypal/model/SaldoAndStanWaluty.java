package pl.finapi.paypal.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SaldoAndStanWaluty {

	private final Amount saldo;
	private final StanWaluty stanWaluty;

	public SaldoAndStanWaluty(Amount saldo, StanWaluty stanWaluty) {
		this.saldo = saldo;
		this.stanWaluty = stanWaluty;
	}

	public StanWaluty getStanWaluty() {
		return stanWaluty;
	}

	public Amount getSaldo() {
		return saldo;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
}

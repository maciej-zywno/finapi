package pl.finapi.paypal.model.roznice;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import pl.finapi.paypal.model.Amount;

public class AmountAtRate {

	private final Amount amount;
	private final Amount exchangeRate;

	public AmountAtRate(Amount amount, Amount exchangeRate) {
		this.amount = amount;
		this.exchangeRate = exchangeRate;
	}

	public Amount getAmount() {
		return amount;
	}

	public Amount getExchangeRate() {
		return exchangeRate;
	}

	public Amount getResult() {
		return new Amount(amount.getAmount().multiply(exchangeRate.getAmount()));
	}

	public AmountAtRate negate() {
		return new AmountAtRate(getAmount().negate(), getExchangeRate());
	}

	public boolean isNegative() {
		return amount.isNegative();
	}

	public boolean isPositive() {
		return amount.isPositive();
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

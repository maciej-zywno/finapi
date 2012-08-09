package pl.finapi.paypal.model;

import java.math.BigDecimal;
import java.math.MathContext;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Amount {

	public static final Amount ZERO = new Amount(BigDecimal.ZERO);

	private final BigDecimal amount;

	public Amount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public boolean isLessThan(Amount amount) {
		return this.amount.compareTo(amount.amount) == -1;
	}

	public boolean isMoreThan(Amount amount) {
		return this.amount.compareTo(amount.amount) == +1;
	}

	public boolean isEqual(Amount amount) {
		return this.amount.compareTo(amount.amount) == 0;
	}

	public boolean isPositive() {
		return getAmount().compareTo(BigDecimal.ZERO) == +1;
	}

	public boolean isNegative() {
		return getAmount().compareTo(BigDecimal.ZERO) == -1;
	}

	public boolean isZero() {
		return getAmount().equals(BigDecimal.ZERO);
	}

	public Amount times(Amount amount) {
		return new Amount(this.amount.multiply(amount.getAmount()));
	}

	public Amount sum(Amount amount) {
		return new Amount(getAmount().add(amount.getAmount()));
	}

	public Amount minus(Amount amount) {
		return new Amount(getAmount().subtract(amount.getAmount()));
	}

	public Amount divideBy(Amount mianownik) {
		MathContext mc = new MathContext(mianownik.getAmount().precision());
		return new Amount(getAmount().divide(mianownik.getAmount(), mc));
	}

	public Amount negate() {
		return new Amount(getAmount().negate());
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

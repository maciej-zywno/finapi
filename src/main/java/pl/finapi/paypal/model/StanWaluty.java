package pl.finapi.paypal.model;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import pl.finapi.paypal.model.roznice.AmountAtRate;

public class StanWaluty {

	private final List<AmountAtRate> stanWalutyList;

	public StanWaluty(List<AmountAtRate> stanWalutyList) {
		this.stanWalutyList = stanWalutyList;
	}

	public List<AmountAtRate> getStanWalutyList() {
		return stanWalutyList;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public Amount sum() {
		Amount sum = Amount.ZERO;
		for (AmountAtRate amountAtRate : getStanWalutyList()) {
			sum = sum.sum(amountAtRate.getAmount());
		}
		return sum;
	}

	public boolean isPositive() {
		return sum().isPositive();
	}

	public boolean isNegative() {
		return sum().isNegative();
	}

	public boolean isNonNegative() {
		return !sum().isNegative();
	}

	public boolean isNonPositive() {
		return !sum().isPositive();
	}
}

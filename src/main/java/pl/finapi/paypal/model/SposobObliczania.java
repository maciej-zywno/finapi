package pl.finapi.paypal.model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import pl.finapi.paypal.model.roznice.AmountAtRate;

public class SposobObliczania {

	public static final SposobObliczania EMPTY = new SposobObliczania(Collections.<AmountAtRate> emptyList(), null);
	
	private final List<AmountAtRate> calculations;
	private final AmountAtRate amountAtRate;

	public SposobObliczania(List<AmountAtRate> calculations, AmountAtRate amountAtRate) {
		this.calculations = calculations;
		this.amountAtRate = amountAtRate;
	}

	public List<AmountAtRate> getCalculations() {
		return calculations;
	}

	public AmountAtRate getAmountAtRate() {
		return amountAtRate;
	}

	public Amount calculateResult() {
		return new Amount(getAmountAtRate().getResult().getAmount().subtract(sum(getCalculations()).getAmount()));
	}

	private Amount sum(List<AmountAtRate> calculations) {
		BigDecimal sum = BigDecimal.ZERO;
		for (AmountAtRate amountAtRate : calculations) {
			sum = sum.add(amountAtRate.getResult().getAmount());
		}
		return new Amount(sum);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}

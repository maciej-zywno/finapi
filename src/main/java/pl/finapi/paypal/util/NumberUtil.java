package pl.finapi.paypal.util;

import java.math.BigDecimal;
import java.math.MathContext;

import org.springframework.stereotype.Component;

import pl.finapi.paypal.model.Amount;
import pl.finapi.paypal.model.roznice.AmountAtRate;

@Component
public class NumberUtil {

	public Amount negateAndFixZeroCase(Amount amount) {
		BigDecimal value = amount.getAmount();
		return new Amount(value.negate());
	}

	public Amount asAmount(double val) {
		MathContext mc = MathContext.DECIMAL32;
		return new Amount(new BigDecimal(val, mc));
	}

	public AmountAtRate asStanWaluty(double amount, double exchangeRate) {
		return new AmountAtRate(asAmount(amount), asAmount(exchangeRate));
	}

	public Amount zeroAmount() {
		return new Amount(BigDecimal.ZERO);
	}

	public String expandToTwoDigits(int value) {
		return value < 10 ? "0" + value : Integer.toString(value);
	}

}

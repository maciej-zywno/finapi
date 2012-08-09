package pl.finapi.paypal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import pl.finapi.paypal.model.Amount;
import pl.finapi.paypal.model.Currency;
import pl.finapi.paypal.model.SaldoAndStanWaluty;
import pl.finapi.paypal.model.StanWaluty;
import pl.finapi.paypal.model.roznice.AmountAtRate;
import pl.finapi.paypal.util.NumberUtil;

public class SampleData {

	public static Map<Currency, SaldoAndStanWaluty> emptySaldoAndStanWalutyForEachCurrency(NumberUtil numberUtil) {
		Amount initialSaldoInEuro = numberUtil.asAmount(0);
		Amount initialSaldoInEuroExchangeRate = numberUtil.asAmount(0);

		StanWaluty stanWaluty = new StanWaluty(Arrays.asList(new AmountAtRate(initialSaldoInEuro, initialSaldoInEuroExchangeRate)));
		SaldoAndStanWaluty saldoAndStanWaluty = new SaldoAndStanWaluty(initialSaldoInEuro, stanWaluty);

		Map<Currency, SaldoAndStanWaluty> map = new HashMap<>();
		for (Currency currency : Currency.values()) {
			map.put(currency, saldoAndStanWaluty);
		}
		return map;
	}

}

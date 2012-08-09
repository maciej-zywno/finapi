package pl.finapi.paypal;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.finapi.paypal.model.Currency;
import pl.finapi.paypal.model.Day;
import pl.finapi.paypal.model.ExchangeRate;
import pl.finapi.paypal.model.ExchangeRateResponse;
import pl.finapi.paypal.model.IdentyfikatorKursu;
import pl.finapi.paypal.model.IdentyfikatorKursuSource;
import pl.finapi.paypal.util.NumberUtil;

//@Component
public class ExchangeRateServiceDummyImpl implements ExchangeRateService {

	private final NumberUtil numberUtil;

	@Autowired
	public ExchangeRateServiceDummyImpl(NumberUtil numberUtil) {
		this.numberUtil = numberUtil;
	}

	@Override
	public Pair<Day, ExchangeRateResponse> findExchangeRate(Currency currency, Day day) {
		IdentyfikatorKursu identyfikatorKursu = new IdentyfikatorKursu("dummy", IdentyfikatorKursuSource.NBP, day);
		ExchangeRate exchangeRate = new ExchangeRate(Currency.PLN, Currency.EUR, numberUtil.asAmount(1.2345), identyfikatorKursu);
		String tableName = "fooTableName";
		ExchangeRateResponse response = new ExchangeRateResponse(exchangeRate, tableName);
		return new ImmutablePair<Day, ExchangeRateResponse>(day, response);
	}
}

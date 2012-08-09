package pl.finapi.paypal;

import org.apache.commons.lang3.tuple.Pair;

import pl.finapi.paypal.model.Currency;
import pl.finapi.paypal.model.Day;
import pl.finapi.paypal.model.ExchangeRateResponse;

public interface ExchangeRateService {

	Pair<Day, ExchangeRateResponse> findExchangeRate(Currency currency, Day day);

}
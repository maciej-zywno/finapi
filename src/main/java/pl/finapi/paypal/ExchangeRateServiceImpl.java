package pl.finapi.paypal;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import pl.finapi.paypal.model.Currency;
import pl.finapi.paypal.model.Day;
import pl.finapi.paypal.model.ExchangeRate;
import pl.finapi.paypal.model.ExchangeRateResponse;
import pl.finapi.paypal.model.IdentyfikatorKursu;
import pl.finapi.paypal.model.IdentyfikatorKursuSource;
import pl.finapi.paypal.util.NumberUtil;
import pl.finapi.paypal.util.TimeUtil;

@Component
public class ExchangeRateServiceImpl implements ExchangeRateService {

	// state - cache
	private final Map<Pair<Currency, Day>, Pair<Day, ExchangeRateResponse>> cache = new HashMap<>();

	private final DateFormat yearMonthDayDashDelimitedFormat;
	private final String exchangeRateWebServiceBaseUrl;
	private final TimeUtil timeUtil;
	private final int maxRetryCount;
	private final NumberUtil numberUtil;

	private final Logger log = Logger.getLogger(this.getClass());

	@Autowired
	public ExchangeRateServiceImpl(@Value("#{yearMonthDayDashDelimitedFormat_WarsawTimeZone}") DateFormat yearMonthDayDashDelimitedFormat_WarsawTimeZone,
			@Value("${exchangeRateWebServiceBaseUrl}") String exchangeRateWebServiceBaseUrl, TimeUtil timeUtil,
			@Value("${exchangeRateWebServiceMaxRetryCount}") int maxRetryCount, NumberUtil numberUtil) {
		this.yearMonthDayDashDelimitedFormat = yearMonthDayDashDelimitedFormat_WarsawTimeZone;
		this.exchangeRateWebServiceBaseUrl = exchangeRateWebServiceBaseUrl;
		this.timeUtil = timeUtil;
		this.maxRetryCount = maxRetryCount;
		this.numberUtil = numberUtil;
	}

	@Override
	public Pair<Day, ExchangeRateResponse> findExchangeRate(Currency currency, Day day) {

		// first check in cache
		if (cache.containsKey(new ImmutablePair<Currency, Day>(currency, day))) {
			return cache.get(new ImmutablePair<Currency, Day>(currency, day));
		}

		// get response from web service
		Pair<Day, ExchangeRateResponse> exchangeRateResponse = findExchangeRate1(currency, day);

		// put response in cache
		cache.put(new ImmutablePair<Currency, Day>(currency, day), exchangeRateResponse);

		return exchangeRateResponse;
	}

	private Pair<Day, ExchangeRateResponse> findExchangeRate1(Currency currency, Day day) {
		try {
			Day day1 = timeUtil.minusDay(day, 1);
			ExchangeRateResponse exchangeRateResponse1 = findExchangeRateOneDay(currency, day1);
			return new ImmutablePair<Day, ExchangeRateResponse>(day1, exchangeRateResponse1);
		} catch (RuntimeException e) {
			try {
				Day day2 = timeUtil.minusDay(day, 2);
				ExchangeRateResponse exchangeRateResponse2 = findExchangeRateOneDay(currency, day2);
				return new ImmutablePair<Day, ExchangeRateResponse>(day2, exchangeRateResponse2);
			} catch (RuntimeException e1) {
				try {
					Day day3 = timeUtil.minusDay(day, 3);
					ExchangeRateResponse exchangeRateResponse3 = findExchangeRateOneDay(currency, day3);
					return new ImmutablePair<Day, ExchangeRateResponse>(day3, exchangeRateResponse3);
				} catch (RuntimeException e2) {
					try {
						Day day4 = timeUtil.minusDay(day, 4);
						ExchangeRateResponse exchangeRateResponse4 = findExchangeRateOneDay(currency, day4);
						return new ImmutablePair<Day, ExchangeRateResponse>(day4, exchangeRateResponse4);
					} catch (RuntimeException e3) {
						throw e3;
					}
				}
			}
		}
	}

	private ExchangeRateResponse findExchangeRateOneDay(Currency currency, Day day) {
		String url = buildUrl(currency, day, exchangeRateWebServiceBaseUrl);
		log.info("fetching: '" + url + "'");
		String response = fetch(url);
		if (response.startsWith("Nie znaleziono kursu")) {
			throw new RuntimeException("Url='" + url + "' and response='" + response + "'");
		}
		try {
			String[] tokens = response.split(",");
			String exchangeRateAsString = tokens[0];
			String tableName = tokens[1];
			double exchangeRate = Double.parseDouble(exchangeRateAsString);
			IdentyfikatorKursu identyfikatorKursu = new IdentyfikatorKursu(tokens[1], IdentyfikatorKursuSource.NBP, day);
			return new ExchangeRateResponse(new ExchangeRate(Currency.PLN, currency, numberUtil.asAmount(exchangeRate), identyfikatorKursu), tableName);
		} catch (NumberFormatException e) {
			log.info("Could not parse finapi.pl response for request '" + url + "'");
			throw e;
		}
	}

	private String fetch(String url) {
		return fetch(url, 1);
	}

	private String fetch(String url, int tryCount) {
		try {
			return IOUtils.toString(new URL(url));
		} catch (IOException e) {
			if (tryCount > maxRetryCount) {
				throw new RuntimeException(e);
			} else {
				tryCount++;
				return fetch(url, tryCount);
			}
		}
	}

	private String buildUrl(Currency currency, Day day, String exchangeRateWebServiceBaseUrl) {
		return exchangeRateWebServiceBaseUrl + "?waluta=" + currency.name() + "&data=" + format(day);
	}

	private String format(Day day) {
		return yearMonthDayDashDelimitedFormat.format(timeUtil.toWarsawDateTime(day));
	}
}

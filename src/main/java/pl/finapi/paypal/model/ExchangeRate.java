package pl.finapi.paypal.model;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import pl.finapi.paypal.util.NumberUtil;

public class ExchangeRate {

	public static final ExchangeRate PLN = new ExchangeRate(Currency.PLN, Currency.PLN, new NumberUtil().asAmount(1), new IdentyfikatorKursu("PLN-PLN",
			IdentyfikatorKursuSource.NBP, new Day(1900, 1, 1)));

	public static final ExchangeRate NO_RATE = new ExchangeRate(Currency.PLN, Currency.PLN, new Amount(BigDecimal.ZERO), new IdentyfikatorKursu("foo",
			IdentyfikatorKursuSource.PAYPAL, new Day(1, 1, 1)));

	private final Currency originCurrency;
	private final Currency foreignCurrency;
	private final Amount exchangeRate;
	private final IdentyfikatorKursu identyfikatorKursu;

	public ExchangeRate(Currency originCurrency, Currency foreignCurrency, Amount exchangeRate, IdentyfikatorKursu identyfikatorKursu) {
		this.originCurrency = originCurrency;
		this.foreignCurrency = foreignCurrency;
		this.exchangeRate = exchangeRate;
		this.identyfikatorKursu = identyfikatorKursu;
	}

	public Currency getOriginCurrency() {
		return originCurrency;
	}

	public Currency getForeignCurrency() {
		return foreignCurrency;
	}

	public Amount getExchangeRate() {
		return exchangeRate;
	}

	public IdentyfikatorKursu getIdentyfikatorKursu() {
		return identyfikatorKursu;
	}

	public static ExchangeRate eur(Amount rate, IdentyfikatorKursu identyfikatorKursu) {
		return new ExchangeRate(Currency.PLN, Currency.EUR, rate, identyfikatorKursu);
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

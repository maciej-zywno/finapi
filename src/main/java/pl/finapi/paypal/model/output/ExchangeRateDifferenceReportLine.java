package pl.finapi.paypal.model.output;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;

import pl.finapi.paypal.model.Amount;
import pl.finapi.paypal.model.ExchangeRate;
import pl.finapi.paypal.model.SposobObliczania;
import pl.finapi.paypal.model.StanWaluty;

public class ExchangeRateDifferenceReportLine {

	private TrescOperacji trescOperacji;
	private Amount amount;
	private DateTime transactionDateTime;
	private StanWaluty stanWaluty;
	private ExchangeRate exchangeRate;
	private SposobObliczania sposobObliczania;

	public void setTrescOperacji(TrescOperacji trescOperacji) {
		this.trescOperacji = trescOperacji;
	}

	public void setAmountFlow(Amount amount) {
		this.amount = amount;
	}

	public void setTransactionDateTime(DateTime transactionDateTime) {
		this.transactionDateTime = transactionDateTime;
	}

	public void setStanWaluty(StanWaluty stanWaluty) {
		this.stanWaluty = stanWaluty;
	}

	public void setExchangeRate(ExchangeRate exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public void setSposobObliczania(SposobObliczania sposobObliczania) {
		this.sposobObliczania = sposobObliczania;
	}

	public StanWaluty getStanWaluty() {
		return stanWaluty;
	}

	public Amount getAmountFlow() {
		return amount;
	}

	public DateTime getTransactionDateTime() {
		return transactionDateTime;
	}

	public TrescOperacji getTrescOperacji() {
		return trescOperacji;
	}

	public ExchangeRate getExchangeRate() {
		return exchangeRate;
	}

	public SposobObliczania getSposobObliczania() {
		return sposobObliczania;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}

package pl.finapi.paypal.model.output;

import java.util.List;

import pl.finapi.paypal.model.Amount;
import pl.finapi.paypal.model.Currency;
import pl.finapi.paypal.model.DateTimeRange;

public class ExchangeRateDifferenceReportModel {

	private final List<ExchangeRateDifferenceReportLine> lines;
	private final Currency currency;
	private final Amount sumOfPositives;
	private final Amount sumOfNegatives;
	private final DateTimeRange transactionDateRange;

	public ExchangeRateDifferenceReportModel(List<ExchangeRateDifferenceReportLine> lines, Currency currency, Amount sumOfPositives,
			Amount sumOfNegatives, DateTimeRange transactionDateRange) {
		this.lines = lines;
		this.currency = currency;
		this.sumOfPositives = sumOfPositives;
		this.sumOfNegatives = sumOfNegatives;
		this.transactionDateRange = transactionDateRange;
	}

	public List<ExchangeRateDifferenceReportLine> getLines() {
		return lines;
	}

	public Currency getCurrency() {
		return currency;
	}

	public Amount getSumOfPositives() {
		return sumOfPositives;
	}

	public Amount getSumOfNegatives() {
		return sumOfNegatives;
	}

	public DateTimeRange getTransactionDateRange() {
		return transactionDateRange;
	}

	public List<ExchangeRateDifferenceReportLine> getTransactionLines() {
		return lines;
	}

}

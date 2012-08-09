package pl.finapi.paypal.model.output;

import pl.finapi.paypal.model.Day;

public class PaypalFeeInvoiceDates {

	private final Day creationDay;
	private final String transactionMonthYearAsString;

	public PaypalFeeInvoiceDates(Day creationDay, String transactionMonthYearAsString) {
		this.creationDay = creationDay;
		this.transactionMonthYearAsString = transactionMonthYearAsString;
	}

	public Day getCreationDay() {
		return creationDay;
	}

	public String getTransactionMonthYearAsString() {
		return transactionMonthYearAsString;
	}

}

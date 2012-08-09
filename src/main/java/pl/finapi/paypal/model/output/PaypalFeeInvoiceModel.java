package pl.finapi.paypal.model.output;

import pl.finapi.paypal.model.Amount;
import pl.finapi.paypal.model.BuyerInfo;
import pl.finapi.paypal.model.Day;

public class PaypalFeeInvoiceModel {

	private final Amount amount;
	private final String transactionMonthYearAsString;
	private final BuyerInfo buyerInfo;
	private final Day creationDay;

	public PaypalFeeInvoiceModel(Amount amount, String transactionMonthYearAsString, BuyerInfo buyerInfo, Day creationDay) {
		this.amount = amount;
		this.transactionMonthYearAsString = transactionMonthYearAsString;
		this.buyerInfo = buyerInfo;
		this.creationDay = creationDay;
	}

	public Amount getAmount() {
		return amount;
	}

	public String getTransactionMonthYearAsString() {
		return transactionMonthYearAsString;
	}

	public BuyerInfo getBuyerInfo() {
		return buyerInfo;
	}

	public Day getCreationDate() {
		return creationDay;
	}

}

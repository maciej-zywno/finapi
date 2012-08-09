package pl.finapi.paypal.source.report;

import java.util.List;

import pl.finapi.paypal.model.TransactionSummary;

public class PaypalReportData {

	private final List<TransactionSummary> transactionSummaries;

	public PaypalReportData(List<TransactionSummary> transactionSumarries) {
		this.transactionSummaries = transactionSumarries;
	}

	public List<TransactionSummary> getTransactionSummaries() {
		return transactionSummaries;
	}

}

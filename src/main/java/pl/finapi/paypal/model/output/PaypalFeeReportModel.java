package pl.finapi.paypal.model.output;

import java.util.List;

import pl.finapi.paypal.model.Amount;
import pl.finapi.paypal.model.BuyerInfo;
import pl.finapi.paypal.model.DateTimeRange;
import pl.finapi.paypal.model.TransactionLine;

public class PaypalFeeReportModel {

	private final List<TransactionLine> transactionLines;
	private final Amount transactionFeeInPlnSum;
	private final DateTimeRange transactionDateRange;
	private final BuyerInfo buyerInfo;

	public PaypalFeeReportModel(List<TransactionLine> transactionLines, Amount transactionFeeInPlnSum, DateTimeRange transactionDateRange, BuyerInfo buyerInfo) {
		this.transactionLines = transactionLines;
		this.transactionFeeInPlnSum = transactionFeeInPlnSum;
		this.transactionDateRange = transactionDateRange;
		this.buyerInfo = buyerInfo;
	}

	public List<TransactionLine> getTransactionLines() {
		return transactionLines;
	}

	public Amount getTransactionFeeInPlnSum() {
		return transactionFeeInPlnSum;
	}

	public DateTimeRange getTransactionDateRange() {
		return transactionDateRange;
	}

	public BuyerInfo getBuyerInfo() {
		return buyerInfo;
	}

}

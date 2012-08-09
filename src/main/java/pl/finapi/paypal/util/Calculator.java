package pl.finapi.paypal.util;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import pl.finapi.paypal.model.Amount;
import pl.finapi.paypal.model.DateTimeRange;
import pl.finapi.paypal.model.TransactionLine;

@Component
// TODO: fix class and method naming
public class Calculator {

	public Amount sum(List<TransactionLine> lines) {
		BigDecimal sum = BigDecimal.ZERO;
		for (TransactionLine transactionLine : lines) {
			Amount feeInPln = transactionLine.getFeeInPln();
			sum = sum.add(feeInPln.getAmount().negate());
		}
		return new Amount(sum);
	}

	public DateTimeRange findDateTimeRange(List<TransactionLine> lines) {
		DateTime startDate = new DateTime(Long.MAX_VALUE);
		DateTime endDate = new DateTime(Long.MIN_VALUE);
		for (TransactionLine line : lines) {
			if (line.getTransactionDateTime().isBefore(startDate)) {
				startDate = line.getTransactionDateTime();
			}
			if (line.getTransactionDateTime().isAfter(endDate)) {
				endDate = line.getTransactionDateTime();
			}
		}
		return new DateTimeRange(startDate, endDate);
	}
}

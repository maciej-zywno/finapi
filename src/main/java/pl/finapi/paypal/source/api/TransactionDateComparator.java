package pl.finapi.paypal.source.api;

import java.util.Comparator;

import pl.finapi.paypal.model.TransactionSummary;

public final class TransactionDateComparator implements Comparator<TransactionSummary> {

	@Override
	public int compare(TransactionSummary o1, TransactionSummary o2) {
		if (o1.getDateTime().isBefore(o2.getDateTime())) {
			return -1;
		} else {
			return +1;
		}
	}
}
package pl.finapi.paypal.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.finapi.paypal.model.CsvTransactionType;
import pl.finapi.paypal.model.TransactionSummary;

@Component
public class TransactionSummaryFilter {

	private final CollectionUtil collectionUtil;

	@Autowired
	public TransactionSummaryFilter(CollectionUtil collectionUtil) {
		this.collectionUtil = collectionUtil;
	}

	public List<TransactionSummary> filterPayment(List<TransactionSummary> transactionSummaries) {
		return filterOut(transactionSummaries, collectionUtil.<CsvTransactionType> asSet());
	}

	private List<TransactionSummary> filterOut(List<TransactionSummary> allTransactions, Set<CsvTransactionType> transactionTypes) {

		List<TransactionSummary> filtered = new ArrayList<>();

		for (TransactionSummary transaction : allTransactions) {

			if (!transactionTypes.contains(transaction.getType())) {
				filtered.add(transaction);
			}
		}

		return filtered;
	}

}

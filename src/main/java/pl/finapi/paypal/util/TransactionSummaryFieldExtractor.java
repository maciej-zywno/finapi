package pl.finapi.paypal.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import pl.finapi.paypal.model.CsvTransactionStatus;
import pl.finapi.paypal.model.CsvTransactionType;
import pl.finapi.paypal.model.TransactionSummary;

@Component
public class TransactionSummaryFieldExtractor {

	public Set<CsvTransactionStatus> extractStatus(List<TransactionSummary> transactionSummaries) {
		Set<CsvTransactionStatus> types = new HashSet<>();
		for (TransactionSummary transactionSummary : transactionSummaries) {
			types.add(transactionSummary.getStatus());
		}
		return types;
	}

	public Set<CsvTransactionType> extractType(List<TransactionSummary> transactionSummaries) {
		Set<CsvTransactionType> types = new HashSet<>();
		for (TransactionSummary transactionSummary : transactionSummaries) {
			types.add(transactionSummary.getType());
		}
		return types;
	}

}

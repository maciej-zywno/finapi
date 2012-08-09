package pl.finapi.paypal.source.report;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.finapi.paypal.model.CsvTransactionStatus;
import pl.finapi.paypal.model.CsvTransactionType;
import pl.finapi.paypal.model.TransactionSummary;
import pl.finapi.paypal.util.TransactionSummaryFieldExtractor;

@Component
public class ConsolePrintUtil {

	private final TransactionSummaryFieldExtractor transactionSummaryFieldExtractor;

	@Autowired
	public ConsolePrintUtil(TransactionSummaryFieldExtractor transactionSummaryFieldExtractor) {
		this.transactionSummaryFieldExtractor = transactionSummaryFieldExtractor;
	}

	public void printStatusesAndTypes(List<TransactionSummary> transactionSummaries) {
		printStatuses(transactionSummaries);
		printEmptyLine();
		printTypes(transactionSummaries);
	}

	public void printStatuses(List<TransactionSummary> transactionSummaries) {
		for (CsvTransactionStatus status : transactionSummaryFieldExtractor.extractStatus(transactionSummaries)) {
			System.out.println(status);
		}
	}

	public void printTypes(List<TransactionSummary> transactionSummaries) {
		for (CsvTransactionType type : transactionSummaryFieldExtractor.extractType(transactionSummaries)) {
			System.out.println(type);
		}
	}

	public void printEmptyLine() {
		System.out.println();
	}

}

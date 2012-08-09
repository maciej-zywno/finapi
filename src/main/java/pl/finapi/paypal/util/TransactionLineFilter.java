package pl.finapi.paypal.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import pl.finapi.paypal.model.CsvTransactionType;
import pl.finapi.paypal.model.TransactionLine;

@Component
public class TransactionLineFilter {

	private final Set<CsvTransactionType> nonSellRelatedTransactionTypes = new CollectionUtil().asSet(new CsvTransactionType[] {
			CsvTransactionType.SHOPPING_CART_ITEM, CsvTransactionType.WITHDRAW_FUNDS_TO_BANK_ACCOUNT, CsvTransactionType.CANCELLED_FEE,
			CsvTransactionType.INVOICE_RECEIVED, CsvTransactionType.INVOICE_SENT });

	public List<TransactionLine> filterOutNonSellRelatedTransactions(List<TransactionLine> transactionLines) {
		return filterOutNonSellRelatedTransactions(transactionLines, nonSellRelatedTransactionTypes);
	}

	public boolean isNonSellRelated(TransactionLine transactionLine) {
		return nonSellRelatedTransactionTypes.contains(transactionLine.getTransactionType());
	}

	private List<TransactionLine> filterOutNonSellRelatedTransactions(List<TransactionLine> transactionLines, Set<CsvTransactionType> nonSellTransactionTypes) {
		List<TransactionLine> filtered = new ArrayList<>();
		for (TransactionLine transactionLine : transactionLines) {
			if (isNonSellRelated(transactionLine)) {
				// skip
			} else {
				filtered.add(transactionLine);
			}
		}
		return filtered;
	}

}

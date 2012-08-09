package pl.finapi.paypal.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.finapi.paypal.model.Amount;
import pl.finapi.paypal.model.CsvTransactionType;
import pl.finapi.paypal.model.Currency;
import pl.finapi.paypal.model.TransactionSummary;
import pl.finapi.paypal.model.TransactionSummaryName;
import pl.finapi.paypal.source.report.PaypalReportData;

@Component
public class PaypalReportChunker {

	private final CollectionUtil collectionUtil;
	private final TimeUtil timeUtil;

	@Autowired
	public PaypalReportChunker(CollectionUtil collectionUtil, TimeUtil timeUtil) {
		this.collectionUtil = collectionUtil;
		this.timeUtil = timeUtil;
	}

	public Map<Currency, PaypalReportData> divideIntoCurrencyChunks(PaypalReportData oneMonthPaypalReportData) {

		Map<Currency, List<TransactionSummary>> map = new HashMap<>();
		for (TransactionSummary transactionSummary : oneMonthPaypalReportData.getTransactionSummaries()) {
			// if in transaction main currency is pln then use transaction secondary currency as a key
			if (isCurrencyConversionInPlnCurrency(transactionSummary) || isMoneyTransferInPlnCurrency(transactionSummary)) {
				Currency foreignCurrency = getForeignCurrency(transactionSummary.getName());
				getOrAddAndGet(map, foreignCurrency).add(transactionSummary);
			}
			// a pln payment e.g. a foreign client sends payment to a Polish paypal account but conversion is done on a client side
			// Polish seller pays a fee but there is no currency conversion
			else if (transactionSummary.getCurrency() == Currency.PLN && transactionSummary.isFeeAvailable() && !transactionSummary.getFee().isZero()) {
				// TODO: should check if a transaction affects the saldo
			}
			// if pln but other conditions above not met then skip a transaction
			else if (transactionSummary.getCurrency() == Currency.PLN) {
				// if pln transaction and no paypal fee so skip
				if (transactionSummary.isFeeAvailable()) {
					assertIsZero(transactionSummary.getFee());
				}
				assertIsType(transactionSummary.getType(), CsvTransactionType.EXPRESS_CHECKOUT_PAYMENT_SENT, CsvTransactionType.WITHDRAW_FUNDS_TO_BANK_ACCOUNT,
						CsvTransactionType.CHARGE_FROM_CREDIT_CARD, CsvTransactionType.PAYPAL_CARD_CONFIRMATION_REFUND,
						CsvTransactionType.MONEY_TRANSFER_CANCELED, CsvTransactionType.PAYMENT_LOCK);
			}
			// otherwise take transaction main currency as a key
			else {
				Currency currency = transactionSummary.getCurrency();
				getOrAddAndGet(map, currency).add(transactionSummary);
				// in case of EUR->USD (both foreign currencies) types of "przelew" or "przeliczenie waluty" we need
				// to put the same transaction line in two chunks
				if (transactionSummary.getType().isCurrencyConversionOrPrzelew() && !transactionSummary.getName().getAssociatedCurrency().equals(Currency.PLN)
						&& !transactionSummary.getCurrency().equals(Currency.PLN)) {
					Currency associatedCurrency = transactionSummary.getName().getAssociatedCurrency();
					getOrAddAndGet(map, associatedCurrency).add(transactionSummary);
				}
			}
		}
		return toCurrencyToPaypalReportDataMap(map);
	}

	// private boolean isMoneyTransferBetweenTwoForeignSaldos(TransactionSummary transactionSummary) {
	// boolean moneyTranfer = transactionSummary.getType() == CsvTransactionType.MONEY_TRANSFER;
	//
	// boolean isForeignCurrency = transactionSummary.getCurrency().isForeign();
	// boolean isFromForeignCurrency = transactionSummary.getName().isFromForeignCurrency();
	// boolean isToForeignCurrency = transactionSummary.getName().isToForeignCurrency();
	//
	// return moneyTranfer && isForeignCurrency && (isFromForeignCurrency || isToForeignCurrency);
	//
	// }

	private boolean isMoneyTransferInPlnCurrency(TransactionSummary transactionSummary) {
		boolean b = transactionSummary.getCurrency() == Currency.PLN && transactionSummary.getType() == CsvTransactionType.MONEY_TRANSFER;
		return b;
	}

	private boolean isCurrencyConversionInPlnCurrency(TransactionSummary transactionSummary) {
		boolean b = transactionSummary.getCurrency() == Currency.PLN && transactionSummary.getType() == CsvTransactionType.CURRENCY_CONVERSION;
		return b;
	}

	private void assertIsType(CsvTransactionType actualType, CsvTransactionType... validTypes) {
		if (!Arrays.asList(validTypes).contains(actualType)) {
			throw new RuntimeException();
		}
	}

	private void assertIsZero(Amount amount) {
		if (!amount.isZero()) {
			throw new RuntimeException();
		}
	}

	public List<PaypalReportData> divideIntoMonthChunks(PaypalReportData reportData) {

		List<PaypalReportData> monthChunks = new ArrayList<>();

		DateTime nextMonthStart = timeUtil.nextMonthStartDateTime(collectionUtil.getFirst(reportData.getTransactionSummaries()).getDateTime());

		List<TransactionSummary> currentMonthTransactions = new ArrayList<>();

		for (TransactionSummary transactionSummary : reportData.getTransactionSummaries()) {

			if (transactionSummary.getDateTime().isBefore(nextMonthStart)) {
				currentMonthTransactions.add(transactionSummary);
			} else {
				monthChunks.add(new PaypalReportData(currentMonthTransactions));
				currentMonthTransactions = new ArrayList<>();
				currentMonthTransactions.add(transactionSummary);
				nextMonthStart = timeUtil.nextMonthStartDateTime(transactionSummary.getDateTime());
			}
		}

		if (!currentMonthTransactions.isEmpty()) {
			monthChunks.add(new PaypalReportData(currentMonthTransactions));
		}

		return monthChunks;
	}

	private Currency getForeignCurrency(TransactionSummaryName name) {
		switch (name) {
		case FROM_EURO:
			return Currency.EUR;
		case FROM_BRITISH_POUND:
			return Currency.GBP;
		case FROM_USD:
			return Currency.USD;
		case TO_EURO:
			return Currency.EUR;
		case TO_BRITISH_POUND:
			return Currency.GBP;
		default:
			throw new RuntimeException();
		}
	}

	private List<TransactionSummary> getOrAddAndGet(Map<Currency, List<TransactionSummary>> map, Currency currency) {
		if (!map.containsKey(currency)) {
			map.put(currency, new ArrayList<TransactionSummary>());
		}
		return map.get(currency);
	}

	private Map<Currency, PaypalReportData> toCurrencyToPaypalReportDataMap(Map<Currency, List<TransactionSummary>> map) {
		Map<Currency, PaypalReportData> map1 = new HashMap<>();
		for (Entry<Currency, List<TransactionSummary>> entry : map.entrySet()) {
			map1.put(entry.getKey(), new PaypalReportData(entry.getValue()));
		}
		return map1;
	}
}

package pl.finapi.paypal;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.finapi.paypal.model.Amount;
import pl.finapi.paypal.model.CsvTransactionStatus;
import pl.finapi.paypal.model.CsvTransactionType;
import pl.finapi.paypal.model.Currency;
import pl.finapi.paypal.model.Day;
import pl.finapi.paypal.model.ExchangeRate;
import pl.finapi.paypal.model.ExchangeRateResponse;
import pl.finapi.paypal.model.TransactionLine;
import pl.finapi.paypal.model.TransactionSummary;
import pl.finapi.paypal.util.TimeUtil;

@Component
public class CsvModelConverter {

	private final TimeUtil timeUtil;

	@Autowired
	public CsvModelConverter(TimeUtil timeUtil) {
		this.timeUtil = timeUtil;
	}

	public List<TransactionLine> toLineModel(List<TransactionSummary> transactions, ExchangeRateService exchangeRateService) {
		List<TransactionLine> lines = new ArrayList<>();
		for (int i = 0; i < transactions.size(); i++) {
			TransactionSummary transaction = transactions.get(i);

			Day nbpExchangeRateDay;
			ExchangeRate nbpExchangeRate;
			String tableName;

			if (isPlnTransaction(transaction)) {
				nbpExchangeRateDay = null;
				nbpExchangeRate = ExchangeRate.PLN;
				tableName = "";
			} else {
				Day warsawDay = timeUtil.toWarsawDay(transaction.getDateTime());
				Pair<Day, ExchangeRateResponse> response = exchangeRateService.findExchangeRate(transaction.getCurrency(), warsawDay);
				nbpExchangeRateDay = response.getLeft();
				nbpExchangeRate = response.getRight().getExchangeRate();
				tableName = response.getRight().getTableName();
			}

			int number = i + 1;
			DateTime transactionDateTime = transaction.getDateTime();
			CsvTransactionType transactionType = transaction.getType();
			CsvTransactionStatus transactionStatus = transaction.getStatus();

			boolean hasFee = transaction.isFeeAvailable();
			Amount feeInForeignCurrency = hasFee ? transaction.getFee() : null;
			Amount feeInPln = hasFee ? transaction.getFee().times(nbpExchangeRate.getExchangeRate()) : null;

			boolean hasSaldo = transaction.isSaldoAvailable();
			Amount saldo = hasSaldo ? transaction.getSaldo() : null;

			lines.add(new TransactionLine(number, transactionDateTime, transactionType, transactionStatus, hasFee, feeInForeignCurrency, nbpExchangeRate,
					tableName, nbpExchangeRateDay, feeInPln, transaction.getCurrency(), hasSaldo, saldo, transaction.getName(), transaction.getNettoAmount()));
		}

		return lines;
	}

	private boolean isPlnTransaction(TransactionSummary transaction) {
		return transaction.getCurrency().equals(Currency.PLN);
	}
}

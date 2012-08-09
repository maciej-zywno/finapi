package pl.finapi.paypal.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;

import pl.finapi.paypal.util.Assert;

public class TransactionLine {

	private final int number;
	private final DateTime transactionDateTime;
	private final CsvTransactionType transactionType;
	private final CsvTransactionStatus transactionStatus;
	private final boolean feeAvailable;
	private final Amount feeInForeignCurrency;
	private final ExchangeRate exchangeRate;
	private final String tableName;
	private final Day nbpExchangeRateDay;
	private final Amount feeInPln;
	private final Currency currency;
	private final boolean saldoAvailable;
	private final Amount saldo;
	private final TransactionSummaryName name;
	private final Amount nettoAmount;

	public TransactionLine(int number, DateTime transactionDateTime, CsvTransactionType transactionType, CsvTransactionStatus transactionStatus,
			boolean hasFee, Amount feeInForeignCurrency, ExchangeRate exchangeRate, String tableName, Day nbpExchangeRateDay, Amount feeInPln,
			Currency currency, boolean hasSaldo, Amount saldo, TransactionSummaryName transactionSummaryName, Amount nettoAmount) {
		this.number = number;
		this.transactionDateTime = transactionDateTime;
		this.transactionType = transactionType;
		this.transactionStatus = transactionStatus;
		this.feeAvailable = hasFee;
		this.feeInForeignCurrency = feeInForeignCurrency;
		this.exchangeRate = exchangeRate;
		this.tableName = tableName;
		this.nbpExchangeRateDay = nbpExchangeRateDay;
		this.feeInPln = feeInPln;
		this.currency = currency;
		this.saldoAvailable = hasSaldo;
		this.saldo = saldo;
		this.name = transactionSummaryName;
		this.nettoAmount = nettoAmount;
	}

	public int getNumber() {
		return number;
	}

	public DateTime getTransactionDateTime() {
		return transactionDateTime;
	}

	public CsvTransactionType getTransactionType() {
		return transactionType;
	}

	public boolean isFeeAvailable() {
		return feeAvailable;
	}

	public Amount getFeeInForeignCurrency() {
		return feeInForeignCurrency;
	}

	public ExchangeRate getExchangeRate() {
		return exchangeRate;
	}

	public String getTableName() {
		return tableName;
	}

	public Day getNbpExchangeRateDay() {
		return nbpExchangeRateDay;
	}

	public Amount getFeeInPln() {
		return feeInPln;
	}

	public Currency getCurrency() {
		return currency;
	}

	public boolean isSaldoAvailable() {
		return saldoAvailable;
	}

	public Amount getSaldo() {
		return saldo;
	}

	public TransactionSummaryName getName() {
		return name;
	}

	public Amount getNettoAmount() {
		return nettoAmount;
	}

	public boolean isCurrencyConversionToPLNInForeignSaldo() {
		return getTransactionType().equals(CsvTransactionType.CURRENCY_CONVERSION) && getName().equals(TransactionSummaryName.TO_POLISH_ZLOTY)
				&& !getCurrency().equals(Currency.PLN);
	}

	public boolean isCurrencyConversionToForeignCurrencyInPLNSaldo() {
		return getTransactionType().equals(CsvTransactionType.CURRENCY_CONVERSION) && getName().isToForeignCurrency() && getCurrency().equals(Currency.PLN);
	}

	public boolean isCurrencyConversionFromForeignCurrencyInPLNSaldo() {
		return isCurrencyConversion() && getName().isFromForeignCurrency() && getCurrency() == Currency.PLN;
	}

	public boolean isCurrencyConversionFromPLNInForeignSaldo() {
		return isCurrencyConversion() && getName().equals(TransactionSummaryName.FROM_POLISH_ZLOTY) && !getCurrency().equals(Currency.PLN);
	}

	public boolean isCurrencyConversionToOrFromForeignCurrencyAInForeignCurrencyBSaldo() {
		if (!isCurrencyConversion()) {
			return false;
		}

		if (!isForeignSaldo()) {
			return false;
		}

		if (!transactionNameHasAssociatedCurrency()) {
			return false;
		}

		// at this point we know it's currency conversion in a foreign saldo
		// but what is the other currency? PLN or other foreign currency?
		// just make cure it's not PLN

		// but assume that both currencies cannot be the same
		Assert.notSame(getName().getAssociatedCurrency(), getCurrency());

		// and is it FROM or TO TransactionSummaryName
		boolean isTransactionNameAssociatedCurrencyForeign = getName().getAssociatedCurrency().isForeign();
		return isTransactionNameAssociatedCurrencyForeign;
	}

	public boolean isCurrencyConversionToForeignCurrencyAInForeignCurrencyBSaldo() {
		return isCurrencyConversionToOrFromForeignCurrencyAInForeignCurrencyBSaldo() && getName().isTo();
	}

	public boolean isCurrencyConversionFromForeignCurrencyAInForeignCurrencyBSaldo() {
		return isCurrencyConversionToOrFromForeignCurrencyAInForeignCurrencyBSaldo() && getName().isFrom();
	}

	private boolean transactionNameHasAssociatedCurrency() {
		return getName().hasAssociatedCurrency();
	}

	private boolean isForeignSaldo() {
		return getCurrency().isForeign();
	}

	private boolean isCurrencyConversion() {
		return getTransactionType() == CsvTransactionType.CURRENCY_CONVERSION;
	}

	public boolean isPrzelewFromForeignInPLNSaldo() {
		return getTransactionType() == CsvTransactionType.MONEY_TRANSFER && getName().isFromForeignCurrency() && getCurrency() == Currency.PLN;
	}

	public boolean isPrzelewFromPLNInForeignSaldo() {
		return getTransactionType() == CsvTransactionType.MONEY_TRANSFER && getName().equals(TransactionSummaryName.FROM_POLISH_ZLOTY)
				&& !getCurrency().equals(Currency.PLN);
	}

	public boolean isPrzelewToForeignCurrencyInPLNSaldo() {
		return getTransactionType() == CsvTransactionType.MONEY_TRANSFER && getName().isToForeignCurrency() && getCurrency() == Currency.PLN;
	}

	public boolean isPrzelewToPLNInForeignSaldo() {
		return getTransactionType() == CsvTransactionType.MONEY_TRANSFER && getName().equals(TransactionSummaryName.TO_POLISH_ZLOTY)
				&& !getCurrency().equals(Currency.PLN);
	}

	public CsvTransactionStatus getTransactionStatus() {
		return transactionStatus;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}

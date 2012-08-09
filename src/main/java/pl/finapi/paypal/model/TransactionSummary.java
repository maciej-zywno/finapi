package pl.finapi.paypal.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.springframework.util.Assert;

public class TransactionSummary {

	private final Amount bruttoAmount;
	private final String email;
	private final boolean feeAvailable;
	private final Amount fee;
	private final Currency currency;
	private final TransactionSummaryName name;
	private final Amount nettoAmount;
	private final CsvTransactionStatus status;
	private final DateTime dateTime;
	private final String timeZone;
	private final TransactionId transactionId;
	private final CsvTransactionType type;
	private final boolean saldoAvailable;
	private final Amount saldo;

	public TransactionSummary(Amount amount, String email, boolean hasFee, Amount fee, Currency currency, TransactionSummaryName name, Amount netAmount,
			CsvTransactionStatus status, DateTime dateTime, String timeZone, TransactionId transactionId, CsvTransactionType type, boolean saldoAvailable,
			Amount saldo) {
		this.bruttoAmount = amount;
		this.email = email;
		this.feeAvailable = hasFee;
		this.fee = fee;
		this.currency = currency;
		this.name = name;
		this.nettoAmount = netAmount;
		this.status = status;
		this.dateTime = dateTime;
		this.timeZone = timeZone;
		this.transactionId = transactionId;
		this.type = type;
		this.saldoAvailable = saldoAvailable;
		this.saldo = saldo;
	}

	public Amount getBruttoAmount() {
		return bruttoAmount;
	}

	public String getEmail() {
		return email;
	}

	public boolean isFeeAvailable() {
		return feeAvailable;
	}

	public Amount getFee() {
		assertFeeAvailable();
		return fee;
	}

	public Currency getCurrency() {
		return currency;
	}

	public TransactionSummaryName getName() {
		return name;
	}

	public Amount getNettoAmount() {
		return nettoAmount;
	}

	public CsvTransactionStatus getStatus() {
		return status;
	}

	public DateTime getDateTime() {
		return dateTime;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public TransactionId getTransactionId() {
		return transactionId;
	}

	public CsvTransactionType getType() {
		return type;
	}

	public boolean isSaldoAvailable() {
		return saldoAvailable;
	}

	public Amount getSaldo() {
		assertSaldoAvailable();
		return saldo;
	}

	private void assertSaldoAvailable() {
		Assert.isTrue(saldoAvailable);
	}

	private void assertFeeAvailable() {
		Assert.isTrue(feeAvailable);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}

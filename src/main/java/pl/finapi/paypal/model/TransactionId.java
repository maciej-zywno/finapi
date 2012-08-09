package pl.finapi.paypal.model;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class TransactionId {

	private final String transactionId;

	public TransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}

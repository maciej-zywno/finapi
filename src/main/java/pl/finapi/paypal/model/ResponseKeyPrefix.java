package pl.finapi.paypal.model;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public enum ResponseKeyPrefix {

	TIMESTAMP("L_TIMESTAMP"), TIMEZONE("L_TIMEZONE"), TYPE("L_TYPE"), EMAIL("L_EMAIL"), NAME("L_NAME"), TRANSACTION_ID("L_TRANSACTIONID"), STATUS(
			"L_STATUS"), AMOUNT("L_AMT"), FEE_AMOUNT("L_FEEAMT"), NET_AMOUNT("L_NETAMT"), CURRENCY_CODE("L_CURRENCYCODE"), SHORT_MESSAGE(
			"L_SHORTMESSAGE"), LONG_MESSAGE("L_LONGMESSAGE"), ERROR_CODE("L_ERRORCODE"), SEVERITY_CODE("L_SEVERITYCODE");

	private final String prefix;

	ResponseKeyPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}

	public static Pair<ResponseKeyPrefix, ResponseTupleNumber> parse(String key) {
		for (ResponseKeyPrefix responseKeyPrefix : values()) {
			if (key.startsWith(responseKeyPrefix.prefix))
				return new ImmutablePair<ResponseKeyPrefix, ResponseTupleNumber>(responseKeyPrefix, responseKeyPrefix.parseId(key));
		}
		throw new RuntimeException("no prefix found for " + key);
	}

	private ResponseTupleNumber parseId(String key) {
		return new ResponseTupleNumber(Integer.parseInt(key.substring(prefix.length())));
	}
}

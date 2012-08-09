package pl.finapi.paypal.source.api;

import java.text.ParseException;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import pl.finapi.paypal.model.ResponseKeyPrefix;

public class TransactionParseError {

	private final Map<ResponseKeyPrefix, String> map;
	private final ParseException e;

	public TransactionParseError(Map<ResponseKeyPrefix, String> map, ParseException e) {
		this.map = map;
		this.e = e;
	}

	public Map<ResponseKeyPrefix, String> getMap() {
		return map;
	}

	public ParseException getE() {
		return e;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}

package pl.finapi.paypal.source.api;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import pl.finapi.paypal.model.Currency;
import pl.finapi.paypal.model.ResponseKeyPrefix;
import pl.finapi.paypal.model.ResponseTupleNumber;
import pl.finapi.paypal.model.TransactionId;
import pl.finapi.paypal.model.TransactionSummary;
import pl.finapi.paypal.util.TimeUtil;

@SuppressWarnings("unused")
public class PaypalRestClientHelper {

	private final DateFormat paypalResponseDateFormat;
	private final TimeUtil timeUtil;

	public PaypalRestClientHelper(DateFormat paypalResponseDateFormat, TimeUtil timeUtil) {
		this.paypalResponseDateFormat = paypalResponseDateFormat;
		this.timeUtil = timeUtil;
	}

	public Pair<List<TransactionSummary>, List<TransactionParseError>> createTransactions(Map<String, String> transactionSearchResponse) {
		Map<ResponseTupleNumber, Map<ResponseKeyPrefix, String>> responseTupleNumberToKeyValueMap = createResponseTupleNumberToKeyValueMap(transactionSearchResponse);
		List<TransactionSummary> transactionSummaries = new ArrayList<>();
		Collection<Map<ResponseKeyPrefix, String>> maps = responseTupleNumberToKeyValueMap.values();
		List<TransactionParseError> errors = new ArrayList<>();
		for (Map<ResponseKeyPrefix, String> map : maps) {
			try {
				transactionSummaries.add(toTransactionSummary(map));
			} catch (ParseException e) {
				errors.add(new TransactionParseError(map, e));
				// nothing, skip transaction
			}
		}
		return new ImmutablePair<List<TransactionSummary>, List<TransactionParseError>>(transactionSummaries, errors);
	}

	private Map<ResponseTupleNumber, Map<ResponseKeyPrefix, String>> createResponseTupleNumberToKeyValueMap(
			Map<String, String> transactionSearchResponse) {
		Map<ResponseTupleNumber, Map<ResponseKeyPrefix, String>> responseTupleNumberToKeyValueMap = new HashMap<>();
		for (Entry<String, String> entry : transactionSearchResponse.entrySet()) {
			try {
				addToMap(responseTupleNumberToKeyValueMap, ResponseKeyPrefix.parse(entry.getKey()), entry.getValue());
			} catch (RuntimeException e) {
				if (!e.getMessage().contains("CORRELATIONID") && !e.getMessage().contains("TIMESTAMP") && !e.getMessage().contains("BUILD")
						&& !e.getMessage().contains("ACK") && !e.getMessage().contains("VERSION")) {
					throw e;
				}
			}
		}
		return responseTupleNumberToKeyValueMap;
	}

	private TransactionSummary toTransactionSummary(Map<ResponseKeyPrefix, String> map) throws ParseException {
		String amountAsString = map.get(ResponseKeyPrefix.AMOUNT);
		double amount = Double.parseDouble(amountAsString);
		String email = map.get(ResponseKeyPrefix.EMAIL);
		double fee = Double.parseDouble(map.get(ResponseKeyPrefix.FEE_AMOUNT));
		Currency currencyCode = Currency.parse(map.get(ResponseKeyPrefix.CURRENCY_CODE));
		String name = map.get(ResponseKeyPrefix.NAME);
		double netAmount = Double.parseDouble(map.get(ResponseKeyPrefix.NET_AMOUNT));
		String status = map.get(ResponseKeyPrefix.STATUS);
		String timestamp = map.get(ResponseKeyPrefix.TIMESTAMP);
		// GMT
		String timeZone = map.get(ResponseKeyPrefix.TIMEZONE);
		String dateAsString = timestamp.substring(0, timestamp.length() - 1) + " " + timeZone;
		Date date = paypalResponseDateFormat.parse(dateAsString);
		TransactionId transactionId = new TransactionId(map.get(ResponseKeyPrefix.TRANSACTION_ID));
		String type = map.get(ResponseKeyPrefix.TYPE);
		double saldo = -1;
		return null/*
					 * new TransactionSummary(amount, email, fee, currencyCode, name, netAmount, status, timeUtil.toWarsawDateTime(date),
					 * timeZone, transactionId, type, saldo)
					 */;
	}

	private void addToMap(Map<ResponseTupleNumber, Map<ResponseKeyPrefix, String>> responseTupleNumberToToKeyValueMap,
			Pair<ResponseKeyPrefix, ResponseTupleNumber> pair, String value) {
		ResponseTupleNumber responseTupleNumber = pair.getRight();
		if (!responseTupleNumberToToKeyValueMap.containsKey(responseTupleNumber)) {
			responseTupleNumberToToKeyValueMap.put(responseTupleNumber, new HashMap<ResponseKeyPrefix, String>());
		}
		Map<ResponseKeyPrefix, String> mapForResponseTupleNumber = responseTupleNumberToToKeyValueMap.get(responseTupleNumber);
		mapForResponseTupleNumber.put(pair.getLeft(), value);
	}

}

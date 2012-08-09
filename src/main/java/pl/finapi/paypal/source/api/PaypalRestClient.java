/*package pl.finapi.paypal.source.api;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import pl.finapi.paypal.model.DateTimeRange;
import pl.finapi.paypal.model.PaypalCredentials;
import pl.finapi.paypal.model.TransactionSummary;
import pl.finapi.paypal.util.TimeUtil;

import com.paypal.sdk.core.nvp.NVPDecoder;

public class PaypalRestClient {

	private final TimeUtil timeZoneUtil;
	private final PaypalCredentials paypalCredentials;
	private final PaypalRestClientHelper paypalRestClientHelper;
	private final Comparator<TransactionSummary> transactionDateComparator;

	// String testApiPassword = "1328463911";
	// String testApiUsername = "m.zywn_1328463883_biz_api1.gmail.com";
	// String testApiSignature =
	// "A2NemtiRjGrIlZ7m0FfSUPj6IQC5AGTSV16DnLzBmRe9ozJCeTmOMLYt";

	public PaypalRestClient(TimeUtil timeZoneUtil, PaypalCredentials paypalCredentials, PaypalRestClientHelper paypalRestClientHelper,
			Comparator<TransactionSummary> transactionDateComparator) {
		this.timeZoneUtil = timeZoneUtil;
		this.paypalCredentials = paypalCredentials;
		this.paypalRestClientHelper = paypalRestClientHelper;
		this.transactionDateComparator = transactionDateComparator;
	}

	public Pair<List<TransactionSummary>, List<TransactionParseError>> fetchTransactions(DateTimeRange dayRange) {
		String startDayAsString = timeZoneUtil.formatDateToPaypalRequestDayFormat(dayRange.getStartDateTime());
		String endDayAsString = timeZoneUtil.formatDateToPaypalRequestDayFormat(dayRange.getEndDateTime());

		TransactionSearch operation = new TransactionSearch();
		NVPDecoder decoder = operation.search(startDayAsString, endDayAsString, paypalCredentials.getApiPassword(),
				paypalCredentials.getApiSignature(), paypalCredentials.getApiUsername());

		@SuppressWarnings("unchecked")
		Map<String, String> transactionSearchResponse = (Map<String, String>) decoder.getMap();
		Pair<List<TransactionSummary>, List<TransactionParseError>> transactions = paypalRestClientHelper
				.createTransactions(transactionSearchResponse);
		Collections.sort(transactions.getLeft(), transactionDateComparator);
		return transactions;
	}

}
*/
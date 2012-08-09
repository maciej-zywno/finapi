/*package pl.finapi.paypal.source.api;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.paypal.sdk.core.nvp.NVPDecoder;
import com.paypal.sdk.core.nvp.NVPEncoder;
import com.paypal.sdk.exceptions.PayPalException;
import com.paypal.sdk.profiles.APIProfile;
import com.paypal.sdk.profiles.ProfileFactory;
import com.paypal.sdk.services.NVPCallerServices;

public class TransactionSearch {

	public NVPDecoder search(String startDate, String endDate, String apiPassword, String apiSignature, String apiUsername) {
		NVPCallerServices caller = null;
		NVPEncoder encoder = new NVPEncoder();
		NVPDecoder decoder = new NVPDecoder();

		try {
			caller = new NVPCallerServices();
			APIProfile profile = ProfileFactory.createSignatureAPIProfile();
			profile.setAPIUsername(apiUsername);
			profile.setAPIPassword(apiPassword);
			profile.setSignature(apiSignature);
			profile.setEnvironment("live");
			profile.setSubject("");
			caller.setAPIProfile(profile);
			encoder.add("VERSION", "51.0");
			encoder.add("METHOD", "TransactionSearch");

			// Add request-specific fields to the request string.
			encoder.add("TRXTYPE", "Q");
			DateFormat dfRead = new SimpleDateFormat("MM/dd/yyyy");
			if (startDate != null && !startDate.equals("")) {
				Calendar startDateObj = Calendar.getInstance();
				startDateObj.setTime(dfRead.parse(startDate));
				encoder.add("STARTDATE", startDateObj.get(Calendar.YEAR) + "-" + (startDateObj.get(Calendar.MONTH) + 1) + "-"
						+ startDateObj.get(Calendar.DAY_OF_MONTH) + "T00:00:00Z");
			}

			if (endDate != null && !endDate.equals("")) {
				Calendar endDateObj = Calendar.getInstance();
				endDateObj.setTime(dfRead.parse(endDate));
				encoder.add(
						"ENDDATE",
						endDateObj.get(Calendar.YEAR) + "-" + (endDateObj.get(Calendar.MONTH) + 1) + "-"
								+ endDateObj.get(Calendar.DAY_OF_MONTH) + "T24:00:00Z");
			}
			// Date format from server is 2006-9-6T0:0:0.
			// encoder.add("TRANSACTIONID",transactionID);

			// Execute the API operation and obtain the response.
			String NVPRequest = encoder.encode();
			String NVPResponse = caller.call(NVPRequest);
			decoder.decode(NVPResponse);
			return decoder;
		} catch (PayPalException e) {
			throw new RuntimeException(e);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
*/
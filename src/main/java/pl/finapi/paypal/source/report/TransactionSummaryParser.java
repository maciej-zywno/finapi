package pl.finapi.paypal.source.report;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import pl.finapi.paypal.model.Amount;
import pl.finapi.paypal.model.CsvTransactionStatus;
import pl.finapi.paypal.model.CsvTransactionType;
import pl.finapi.paypal.model.Currency;
import pl.finapi.paypal.model.PaypalReportColumn;
import pl.finapi.paypal.model.TransactionId;
import pl.finapi.paypal.model.TransactionSummary;
import pl.finapi.paypal.model.TransactionSummaryName;
import pl.finapi.paypal.util.NumberUtil;

@Component
public class TransactionSummaryParser {

	// "06-02-2012 21:31:29"
	private final DateFormat paypalReportDateFormat_WarsawTimeZone;
	private final NumberUtil numberUtil;

	@Autowired
	public TransactionSummaryParser(@Value("#{paypalReportDateFormat_WarsawTimeZone}") DateFormat paypalReportDateFormat_WarsawTimeZone, NumberUtil numberUtil) {
		this.paypalReportDateFormat_WarsawTimeZone = paypalReportDateFormat_WarsawTimeZone;
		this.numberUtil = numberUtil;
	}

	public TransactionSummary parseTransactionSummary(String[] cells, Map<PaypalReportColumn, Integer> paypalReportColumnToCellIndex, Language language) {

		Integer bruttoIndex = paypalReportColumnToCellIndex.get(PaypalReportColumn.BRUTTO);
		Amount amount = numberUtil.asAmount(Double.parseDouble(fixDoubleAsString(cells[bruttoIndex])));
		String email = cells[paypalReportColumnToCellIndex.get(PaypalReportColumn.FROM_EMAIL)];
		String feeAsString = cells[paypalReportColumnToCellIndex.get(PaypalReportColumn.FEE)];

		// "..." - otrzymano, wyslano fakture, byc moze jeszcze inne
		boolean hasFee = !feeAsString.isEmpty() && !feeAsString.equals("...");
		Amount fee = hasFee ? numberUtil.asAmount(Double.parseDouble(feeAsString.replace(",", "."))) : null;

		Currency currency = Currency.parse(cells[paypalReportColumnToCellIndex.get(PaypalReportColumn.CURRENCY)]);
		TransactionSummaryName name = TransactionSummaryName.getByName(cells[paypalReportColumnToCellIndex.get(PaypalReportColumn.NAME_AND_SURNAME)], language);
		String netAmountAsString = cells[paypalReportColumnToCellIndex.get(PaypalReportColumn.NETTO)];
		Amount netAmount = numberUtil.asAmount(netAmountAsString.isEmpty() ? 0.0 : Double.parseDouble(fixDoubleAsString(netAmountAsString)));
		CsvTransactionStatus status = CsvTransactionStatus.getByName(cells[paypalReportColumnToCellIndex.get(PaypalReportColumn.STATUS)], language);
		String timeZone = cells[paypalReportColumnToCellIndex.get(PaypalReportColumn.TIMEZONE)];
		DateTime dateTime = parseDateTime(cells[paypalReportColumnToCellIndex.get(PaypalReportColumn.DATE)],
				cells[paypalReportColumnToCellIndex.get(PaypalReportColumn.TIME)], timeZone);
		TransactionId transactionId = new TransactionId(cells[paypalReportColumnToCellIndex.get(PaypalReportColumn.TRANSACTION_ID)]);
		CsvTransactionType type = CsvTransactionType.getByName(cells[paypalReportColumnToCellIndex.get(PaypalReportColumn.TYPE)], language);
		String saldoAsString = cells[paypalReportColumnToCellIndex.get(PaypalReportColumn.SALDO)];

		// "..." - otrzymano fakture
		boolean hasSaldo = !saldoAsString.isEmpty() && !saldoAsString.equals("...");
		String fixed = fixDoubleAsString(saldoAsString);
		Amount saldo = hasSaldo ? numberUtil.asAmount(Double.parseDouble(fixed)) : null;

		return new TransactionSummary(amount, email, hasFee, fee, currency, name, netAmount, status, dateTime, timeZone, transactionId, type, hasSaldo, saldo);
	}

	private String fixDoubleAsString(String bruttoAsString) {
		return StringUtils.remove(StringUtils.remove(bruttoAsString, (char) 32), (char) 160).replace(',', '.');
	}

	private DateTime parseDateTime(String dateDayPart, String hour, String timeZone) {
		// "06-02-2012"
		// "21:31:29"
		// "ECT"
		String dateAsString = dateDayPart + " " + hour + " " + timeZone;
		try {
			Date date = paypalReportDateFormat_WarsawTimeZone.parse(dateAsString);
			return new DateTime(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

}

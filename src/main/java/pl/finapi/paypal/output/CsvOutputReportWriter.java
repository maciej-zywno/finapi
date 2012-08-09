package pl.finapi.paypal.output;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import pl.finapi.paypal.model.TransactionLine;
import pl.finapi.paypal.model.output.PaypalFeeReportModel;

import com.Ostermiller.util.CSVPrint;
import com.Ostermiller.util.CSVPrinter;

@Component
public class CsvOutputReportWriter implements EwidencjaProwizjiWriter {

	private final String[] csvHeader = { "L.p.", "Data operacji", "Typ operacji", "Wartość prowizji (EUR)", "Kurs EUR",
			"Identyfikacja kursu przeliczeniowego", "Wartość prowizji (PLN)" };
	private final DateFormat yearMonthDayDotDelimitedFormat_WarsawTimeZone;

	@Autowired
	public CsvOutputReportWriter(
			@Value("#{yearMonthDayDashDelimitedFormat_WarsawTimeZone}") DateFormat yearMonthDayDotDelimitedFormat_WarsawTimeZone) {
		this.yearMonthDayDotDelimitedFormat_WarsawTimeZone = yearMonthDayDotDelimitedFormat_WarsawTimeZone;
	}

	@Override
	public void write(PaypalFeeReportModel reportModel, OutputStream outputStream) {
		CSVPrint csvPrinter = new CSVPrinter(outputStream);
		csvPrinter.setAlwaysQuote(true);
		csvPrinter.println(csvHeader);
		for (TransactionLine transactionLine : reportModel.getTransactionLines()) {
			csvPrinter.println(toCells(transactionLine));
		}
		try {
			csvPrinter.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String[] toCells(TransactionLine transactionLine) {
		String[] cells = new String[7];

		cells[0] = Integer.toString(transactionLine.getNumber());
		cells[1] = yearMonthDayDotDelimitedFormat_WarsawTimeZone.format(transactionLine.getTransactionDateTime().toDate());
		cells[2] = transactionLine.getTransactionType().getPolishName();
		cells[3] = transactionLine.getFeeInForeignCurrency().getAmount().negate().toString();
		cells[4] = transactionLine.getExchangeRate().getExchangeRate().getAmount().toString();
		cells[5] = "Tabela nr " + transactionLine.getTableName() + " z dnia " + transactionLine.getNbpExchangeRateDay();
		cells[6] = transactionLine.getFeeInPln().getAmount().negate().toString();

		return cells;
	}

}

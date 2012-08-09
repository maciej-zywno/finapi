/*package pl.finapi.paypal;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.Pair;

import pl.finapi.paypal.model.DateTimeRange;
import pl.finapi.paypal.model.TransactionLine;
import pl.finapi.paypal.model.TransactionSummary;
import pl.finapi.paypal.model.output.PaypalFeeReportModel;
import pl.finapi.paypal.output.EwidencjaProwizjiWriter;
import pl.finapi.paypal.source.api.PaypalRestClient;
import pl.finapi.paypal.source.api.TransactionParseError;
import pl.finapi.paypal.util.Calculator;
import pl.finapi.paypal.util.TimeUtil;
import pl.finapi.paypal.util.TransactionSummaryFilter;

public class EwidencjaProwizjiFromPaypalAPIMain {

	private static final PaypalRestClient paypalRestClient = Container.paypalRestClient;
	private static final TimeUtil timeUtil = Container.timeUtil;
	private static final CsvModelConverter lineModelConverter = Container.lineModelConverter;
	private static final TransactionSummaryFilter transactionFilter = Container.transactionFilter;
	private static final EwidencjaProwizjiWriter ewidencjaProwizjiWriter = Container.outputReportCsvWriter;
	private static final ExchangeRateService exchangeRateService = Container.exchangeRateService;
	private static final Calculator calculator = Container.calculator;

	public static void main(String[] args) throws ParseException, IOException {

		ToStringBuilder.setDefaultStyle(ToStringStyle.SIMPLE_STYLE);

		String startDay = "01/1/2012";
		String endDay = "02/31/2012";

		Pair<List<TransactionSummary>, List<TransactionParseError>> response = paypalRestClient.fetchTransactions(timeUtil
				.parseWarsawDayRange(startDay, endDay));
		assertHasNoErrors(response);
		List<TransactionSummary> transactionSummaries = response.getLeft();

		List<TransactionLine> lines = lineModelConverter.toLineModel(transactionFilter.filterPayment(transactionSummaries),
				exchangeRateService);
		DateTimeRange dateTimeRange = calculator.findDateTimeRange(lines);
		PaypalFeeReportModel reportModel = new PaypalFeeReportModel(lines, calculator.sum(lines), dateTimeRange);
		FileOutputStream outputStream = new FileOutputStream("E:/dev/kosztyPaypal.csv");
		ewidencjaProwizjiWriter.write(reportModel, outputStream);
		outputStream.close();
	}

	private static void assertHasNoErrors(Pair<List<TransactionSummary>, List<TransactionParseError>> response) {
		if (!response.getRight().isEmpty()) {
			for (TransactionParseError error : response.getRight()) {
				System.out.println(error);
			}
			throw new RuntimeException("not all transactions could be parsed");
		}

	}

}
*/
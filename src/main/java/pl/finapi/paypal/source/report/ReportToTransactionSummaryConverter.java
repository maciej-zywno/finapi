package pl.finapi.paypal.source.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.finapi.paypal.model.CsvTransactionType;
import pl.finapi.paypal.model.PaypalReportColumn;
import pl.finapi.paypal.model.TransactionSummary;

@Component
public class ReportToTransactionSummaryConverter {

	private final TransactionSummaryParser transactionSummaryParser;
	private final CsvUtil csvUtil;

	@Autowired
	public ReportToTransactionSummaryConverter(TransactionSummaryParser transactionSummaryParser, CsvUtil csvUtil) {
		this.transactionSummaryParser = transactionSummaryParser;
		this.csvUtil = csvUtil;
	}

	public PaypalReportData parse(PaypalReport report) {
		List<String> lines = report.getDataLines();
		List<String> logicalLines = toLogicalLines(lines);
		Language language = understandLanguage(report.getHeaderLine());
		Map<PaypalReportColumn, Integer> paypalReportColumnToCellIndex = toPaypalReportColumnToCellIndex(report.getHeaderLine(), language);
		assertHasRequiredColumns(paypalReportColumnToCellIndex.keySet());
		List<TransactionSummary> transactionSummaries = parse(logicalLines, paypalReportColumnToCellIndex, language);

		Collections.reverse(transactionSummaries);

		return new PaypalReportData(transactionSummaries);
	}

	private Language understandLanguage(String headerLine) {
		if (headerLine.startsWith(PaypalReportColumn.DATE.getPolishName())) {
			return Language.PL;
		} else if (headerLine.startsWith(PaypalReportColumn.DATE.getEnglishName())) {
			return Language.EN;
		} else {
			throw new RuntimeException("Could not understand language from header line " + headerLine);
		}
	}

	private void assertHasRequiredColumns(Set<PaypalReportColumn> headerColumns) {
		if (!headerColumns.containsAll(PaypalReportColumn.getRequired())) {

			// calculations to find missing columnes
			Set<PaypalReportColumn> required = PaypalReportColumn.getRequired();
			required.remove(headerColumns);
			Set<PaypalReportColumn> missingColumns = required;

			throw new TooFewCsvHeaderColumnsException(headerColumns, missingColumns);
		}
	}

	private Map<PaypalReportColumn, Integer> toPaypalReportColumnToCellIndex(String headerLine, Language language) {
		Map<PaypalReportColumn, Integer> map = new HashMap<>();
		String[] headerCells = StringUtils.strip(headerLine, " ,").split(",");
		for (int i = 0; i < headerCells.length; i++) {
			String headerCell = StringUtils.strip(headerCells[i]);
			PaypalReportColumn column = PaypalReportColumn.getByName(headerCell, language);
			assertNotNull(column);
			map.put(column, i);
		}
		return map;
	}

	private void assertNotNull(PaypalReportColumn column) {
		if (column == null) {
			throw new RuntimeException("PaypalReportColumn cannot be null");
		}
	}

	private List<TransactionSummary> parse(List<String> logicalLines, Map<PaypalReportColumn, Integer> paypalReportColumnToCellIndex, Language language) {
		List<TransactionSummary> transactionSummaries = new ArrayList<>();
		for (String[] cells : csvUtil.toCells(logicalLines)) {
			try {
				TransactionSummary transactionSummary = transactionSummaryParser.parseTransactionSummary(cells, paypalReportColumnToCellIndex, language);
				if (transactionSummary.getType() != CsvTransactionType.INVOICE_RECEIVED && transactionSummary.getType() != CsvTransactionType.REQUEST_SENT) {
					transactionSummaries.add(transactionSummary);
				}
			} catch (RuntimeException e) {
				System.out.println(StringUtils.join(cells));
				e.printStackTrace();
				throw e;
			}
		}
		return transactionSummaries;
	}

	private List<String> toLogicalLines(List<String> reportLines) {
		List<String> logicalLines = new ArrayList<>();

		String logicalLine = "";
		for (String line : reportLines) {
			// 1 - first line of logical line
			if (line.startsWith("\"")) {
				// first non-header line in file
				if (logicalLine.isEmpty()) {
					logicalLine += line;
				}
				// other than first line in file
				else {
					// add accumulated logical line
					logicalLines.add(logicalLine);
					// and clear it
					logicalLine = line;
				}
			}

			// 2 - part of logical line
			else {
				logicalLine += line;
			}
		}

		// add accumulated last line
		logicalLines.add(logicalLine);

		return logicalLines;
	}

}

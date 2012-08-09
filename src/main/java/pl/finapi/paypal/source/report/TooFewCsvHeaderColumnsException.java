package pl.finapi.paypal.source.report;

import java.util.Set;

import pl.finapi.paypal.model.PaypalReportColumn;

public class TooFewCsvHeaderColumnsException extends RuntimeException {
	
	private static final long serialVersionUID = -733832905152575927L;
	
	private final Set<PaypalReportColumn> actualHeaderColumns;
	private final Set<PaypalReportColumn> missingColumns;

	public TooFewCsvHeaderColumnsException(Set<PaypalReportColumn> actualHeaderColumns, Set<PaypalReportColumn> missingColumns) {
		this.actualHeaderColumns = actualHeaderColumns;
		this.missingColumns = missingColumns;
	}

	@Override
	public String getMessage() {
		return toString();
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Missing columns: \n\r");
		for (PaypalReportColumn paypalReportColumn : missingColumns) {
			buffer.append(paypalReportColumn.getPolishName());
		}
		buffer.append("\n\r");
		buffer.append("Actual columns: \n\r");
		for (PaypalReportColumn paypalReportColumn : actualHeaderColumns) {
			buffer.append(paypalReportColumn.getPolishName());
		}
		return buffer.toString();
	}

}

package pl.finapi.paypal.source.report;

import java.util.List;

public class PaypalReport {

	private final String headerLine;
	private final List<String> dataLines;

	public PaypalReport(String headerLine, List<String> dataLines) {
		this.headerLine = headerLine;
		this.dataLines = dataLines;
	}

	public String getHeaderLine() {
		return headerLine;
	}

	public List<String> getDataLines() {
		return dataLines;
	}

}

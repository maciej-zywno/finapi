package pl.finapi.paypal.model;

import pl.finapi.paypal.source.report.PaypalReportData;

public class PaypalReportDataWithSaldoAndStanWaluty {

	private final SaldoAndStanWaluty saldoAndStanWaluty;
	private final PaypalReportData paypalReportData;

	public PaypalReportDataWithSaldoAndStanWaluty(SaldoAndStanWaluty saldoAndStanWaluty, PaypalReportData report) {
		this.saldoAndStanWaluty = saldoAndStanWaluty;
		this.paypalReportData = report;
	}

	public PaypalReportData getPaypalReportData() {
		return paypalReportData;
	}

	public SaldoAndStanWaluty getSaldoAndStanWaluty() {
		return saldoAndStanWaluty;
	}

}

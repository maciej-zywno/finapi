package pl.finapi.paypal.model.output;

import java.util.Map;

import pl.finapi.paypal.model.Currency;

public class DocumentModels {

	private final PaypalFeeReportModel paypalFeeReportModel;
	private final Map<Currency, ExchangeRateDifferenceReportModels> exchangeRateDifferenceReportModel;
	private final PaypalFeeInvoiceModel paypalFeeInvoiceModel;

	public DocumentModels(PaypalFeeReportModel paypalFeeReportModel, PaypalFeeInvoiceModel paypalFeeInvoiceModel,
			Map<Currency, ExchangeRateDifferenceReportModels> exchangeRateDifferenceReportModel) {
		this.paypalFeeReportModel = paypalFeeReportModel;
		this.paypalFeeInvoiceModel = paypalFeeInvoiceModel;
		this.exchangeRateDifferenceReportModel = exchangeRateDifferenceReportModel;
	}

	public PaypalFeeReportModel getPaypalFeeReportModel() {
		return paypalFeeReportModel;
	}

	public PaypalFeeInvoiceModel getPaypalFeeInvoiceModel() {
		return paypalFeeInvoiceModel;
	}

	public Map<Currency, ExchangeRateDifferenceReportModels> getExchangeRateDifferenceReportModel() {
		return exchangeRateDifferenceReportModel;
	}

}

package pl.finapi.paypal.model.output;

public class ExchangeRateDifferenceReportModels {

	private final ExchangeRateDifferenceReportModel exchangeRateDifferenceReportModel;
	private final DowodWewnetrznyModel dowodWewnetrznyModelForPositive;
	private final DowodWewnetrznyModel dowodWewnetrznyModelForNegative;

	public ExchangeRateDifferenceReportModels(ExchangeRateDifferenceReportModel exchangeRateDifferenceReportModel,
			DowodWewnetrznyModel dowodWewnetrznyModelForNegative, DowodWewnetrznyModel dowodWewnetrznyModelForPositive) {
		this.exchangeRateDifferenceReportModel = exchangeRateDifferenceReportModel;
		this.dowodWewnetrznyModelForPositive = dowodWewnetrznyModelForPositive;
		this.dowodWewnetrznyModelForNegative = dowodWewnetrznyModelForNegative;
	}

	public ExchangeRateDifferenceReportModel getExchangeRateDifferenceReportModel() {
		return exchangeRateDifferenceReportModel;
	}

	public DowodWewnetrznyModel getDowodWewnetrznyModelForPositive() {
		return dowodWewnetrznyModelForPositive;
	}

	public DowodWewnetrznyModel getDowodWewnetrznyModelForNegative() {
		return dowodWewnetrznyModelForNegative;
	}
}

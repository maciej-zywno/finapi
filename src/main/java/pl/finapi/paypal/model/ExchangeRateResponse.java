package pl.finapi.paypal.model;

public class ExchangeRateResponse {

	private final ExchangeRate exchangeRate;
	private final String tableName;

	public ExchangeRateResponse(ExchangeRate exchangeRate, String tableName) {
		this.exchangeRate = exchangeRate;
		this.tableName = tableName;
	}

	public ExchangeRate getExchangeRate() {
		return exchangeRate;
	}

	public String getTableName() {
		return tableName;
	}

}

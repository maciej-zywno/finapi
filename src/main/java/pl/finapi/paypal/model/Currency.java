package pl.finapi.paypal.model;

public enum Currency {

	EUR,
	GBP,
	USD,
	PLN;

	public static Currency parse(String code) {
		switch (code) {
		case "EUR":
			return Currency.EUR;
		case "GBP":
			return Currency.GBP;
		case "USD":
			return Currency.USD;
		case "PLN":
			return Currency.PLN;
		default:
			throw new RuntimeException();
		}
	}

	public boolean isForeign() {
		return this != Currency.PLN;
	}

	public boolean isPLN() {
		return this == Currency.PLN;
	}

}

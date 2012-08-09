package pl.finapi.paypal.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

import pl.finapi.paypal.source.report.Language;

public enum TransactionSummaryName {

	FROM_EURO("From Euro", "z Euro"),
	FROM_BRITISH_POUND("", "z Funt brytyjski"),
	FROM_POLISH_ZLOTY("From Polish Zloty", "z Złoty polski"),
	FROM_USD("", "z Dolar amerykański"),
	TO_EURO("To Euro", "Do Euro"),
	TO_BRITISH_POUND("", "Do Funt brytyjski"),
	TO_POLISH_ZLOTY("To Polish Zloty", "Do Złoty polski"),
	TO_USD("", "Do Dolar amerykański"),

	BANK_ACCOUNT("Bank Account", "Rachunek bankowy"),
	EBAY_EUROPE("eBay Europe S.à.r.l.", ""),
	CREDIT_CARD("Credit Card", "Karta kredytowa"),
	PAYPAL("PayPal", "PayPal"),
	PERSON_NAME("", "");

	private static Map<TransactionSummaryName, Currency> associatedCurrency = new HashMap<>();
	static {
		associatedCurrency.put(FROM_BRITISH_POUND, Currency.GBP);
		associatedCurrency.put(FROM_POLISH_ZLOTY, Currency.PLN);
		associatedCurrency.put(FROM_EURO, Currency.EUR);
		associatedCurrency.put(FROM_USD, Currency.USD);
		associatedCurrency.put(TO_BRITISH_POUND, Currency.GBP);
		associatedCurrency.put(TO_POLISH_ZLOTY, Currency.PLN);
		associatedCurrency.put(TO_EURO, Currency.EUR);
		associatedCurrency.put(TO_USD, Currency.USD);
	}
	private static final Map<String, TransactionSummaryName> polishNameToEnumMap = new HashMap<>();
	static {
		for (TransactionSummaryName type : TransactionSummaryName.values()) {
			polishNameToEnumMap.put(type.polishName, type);
		}
	}
	private static final Map<String, TransactionSummaryName> englishNameToEnumMap = new HashMap<>();
	static {
		for (TransactionSummaryName type : TransactionSummaryName.values()) {
			englishNameToEnumMap.put(type.englishName, type);
		}
	}

	private final String englishName;
	private final String polishName;

	private TransactionSummaryName(String englishName, String polishName) {
		this.englishName = englishName;
		this.polishName = polishName;
	}

	private static TransactionSummaryName getByPolishName(String polishName) {
		TransactionSummaryName type = polishNameToEnumMap.get(polishName);
		Assert.notNull(type);
		return type;
	}

	private static TransactionSummaryName getByEnglishName(String englishName) {
		TransactionSummaryName type = englishNameToEnumMap.get(englishName);
		Assert.notNull(type);
		return type;
	}

	private static boolean hasEnglishName(String englishName) {
		return englishNameToEnumMap.containsKey(englishName);
	}

	private static boolean hasPolishName(String polishName) {
		return polishNameToEnumMap.containsKey(polishName);
	}

	public static TransactionSummaryName getByName(String headerCell, Language language) {
		switch (language) {
		case EN:
			return hasEnglishName(headerCell) ? getByEnglishName(headerCell) : PERSON_NAME;
		case PL:
			return hasPolishName(headerCell) ? getByPolishName(headerCell) : PERSON_NAME;
		default:
			throw new RuntimeException("unsupported language " + language);
		}
	}

	public boolean isToForeignCurrency() {
		// TODO: or == to any other foreign currency
		return this == TransactionSummaryName.TO_EURO || this == TransactionSummaryName.TO_BRITISH_POUND;
	}

	public boolean isFromForeignCurrency() {
		// TODO: or == to any other foreign currency
		return this == TransactionSummaryName.FROM_EURO || this == TransactionSummaryName.FROM_BRITISH_POUND || this == TransactionSummaryName.FROM_USD;
	}

	public boolean hasAssociatedCurrency() {
		return associatedCurrency.containsKey(this);
	}

	public Currency getAssociatedCurrency() {
		if (!associatedCurrency.containsKey(this)) {
			throw new RuntimeException(this + " is not associated with any currency");
		}
		return associatedCurrency.get(this);
	}

	public boolean isTo() {
		return this == TO_EURO || this == TO_BRITISH_POUND || this == TO_POLISH_ZLOTY || this == TO_USD;
	}

	public boolean isFrom() {
		return this == FROM_EURO || this == FROM_BRITISH_POUND || this == FROM_POLISH_ZLOTY || this == FROM_USD;
	}
}
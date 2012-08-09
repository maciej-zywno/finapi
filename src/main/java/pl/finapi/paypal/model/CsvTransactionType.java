package pl.finapi.paypal.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

import pl.finapi.paypal.source.report.Language;

public enum CsvTransactionType {

	PAYMENT_SENT("Payment Sent", "Płatność wysłana"),
	WITHDRAW_FUNDS_TO_BANK_ACCOUNT("Withdraw Funds to a Bank Account", "Wypłać środki na rachunek bankowy"),
	PAYMENT_RECEIVED("Payment Received", "Płatność otrzymana"),
	SHOPPING_CART_ITEM("Shopping Cart Item", "Przedmiot z koszyka"),
	UPDATE_TO_BANK_TRANSFER_RECEIVED("Update to Bank Transfer Received", "Aktualizacja otrzymanego przelewu bankowego"),
	UPDATE_TO_PAYMENT_RECEIVED("", "Aktualizacja płatności otrzymanej"),
	EXPRESS_CHECKOUT_PAYMENT_SENT("Express Checkout Payment Sent", "PayPal Express — Płatność wysłana"),
	CANCELLED_FEE("Cancelled Fee", "Opłata anulowana"),
	CURRENCY_CONVERSION("Currency Conversion", "Przeliczenie waluty"),
	MOBILE_EXPRESS_CHECKOUT_PAYMENT_RECEIVED("Mobile Express Checkout Payment Received", "PayPal Express z telefonu komórkowego — płatność otrzymana"),
	PAYPAL_CARD_CONFIRMATION_REFUND("PayPal card confirmation refund", "Zwrot obciążeń związanych z potwierdzeniem karty PayPal"),
	CHARGE_FROM_CREDIT_CARD("Charge From Credit Card", "Obciążenie karty kredytowej"),
	EXPRESS_CHECKOUT_PAYMENT_RECEIVED("Express Checkout Payment Received", "PayPal Express — Płatność otrzymana"),
	REFUND("Refund", "Zwrot pieniędzy"),
	INVOICE_RECEIVED("", "Otrzymano fakturę"),
	INVOICE_SENT("", "Wysłano fakturę"),
	REQUEST_SENT("", "Żądanie wysłane"),
	TEMPORARY_LOCK("Temporary Hold", "Tymczasowa blokada"),
	REVERSE_UPDATE("Update to Reversal", "Aktualizacja cofnięcia"),
	REVERSE("", "Cofnięcie"),
	PAYMENT_FROM_EBAY_RECEIVED("eBay Payment Received", "Płatność z serwisu eBay otrzymana"),
	PAYMENT_LOCK("", "Blokada płatności"),
	MONEY_REFUND_TO_SOMEONE("Refund To", "Zwrot pieniędzy na rzecz"), // special case
	RECEIVED_ELECTRONIC_CHEQUE_UPDATE("Update to eCheck Received", "Aktualizacja otrzymanego czeku elektronicznego"),
	MONEY_TRANSFER("Transfer", "Przelew"),
	MONEY_TRANSFER_CANCELED("Cancelled Transfer", "Anulowano Przelew"),
	SALDO_FROZEN("Saldo frozen", "Zamrożone saldo"),
	PAYMENT_ANALYSIS("Payment analysis", "Analiza płatności");

	private static final Map<String, CsvTransactionType> polishNameToEnumMap = new HashMap<>();
	static {
		for (CsvTransactionType type : CsvTransactionType.values()) {
			polishNameToEnumMap.put(type.polishName, type);
		}
	}
	private static final Map<String, CsvTransactionType> englishNameToEnumMap = new HashMap<>();
	static {
		for (CsvTransactionType type : CsvTransactionType.values()) {
			englishNameToEnumMap.put(type.englishName, type);
		}
	}

	private final String englishName;
	private final String polishName;

	private CsvTransactionType(String englishName, String polishName) {
		this.englishName = englishName;
		this.polishName = polishName;
	}

	public static CsvTransactionType parse(String type) {
		throw new RuntimeException();
	}

	public String getPolishName() {
		throwIfNull();
		return polishName;
	}

	private void throwIfNull() {
		if (polishName == null) {
			throw new RuntimeException("polish name is null, type is " + englishName);
		}
	}

	private static CsvTransactionType getByPolishName(String polishName) {
		CsvTransactionType type = polishNameToEnumMap.get(polishName);
		if (type == null) {
			throw new RuntimeException("unsupported Polish transaction type '" + polishName + "'");
		}
		Assert.notNull(type);
		return type;
	}

	private static CsvTransactionType getByEnglishName(String englishName) {
		CsvTransactionType type = englishNameToEnumMap.get(englishName);
		if (type == null) {
			throw new RuntimeException("unsupported English transaction type '" + englishName + "'");
		}
		Assert.notNull(type);
		return type;
	}

	public static CsvTransactionType getByName(String headerCell, Language language) {
		switch (language) {
		case EN:
			return getByEnglishName(headerCell);
		case PL:
			return getByPolishName(headerCell);
		default:
			throw new RuntimeException("unsupported language " + language);
		}
	}

	public boolean isPaymentReceived() {
		return this == CsvTransactionType.PAYMENT_RECEIVED || this == CsvTransactionType.EXPRESS_CHECKOUT_PAYMENT_RECEIVED
				|| this == CsvTransactionType.MOBILE_EXPRESS_CHECKOUT_PAYMENT_RECEIVED;
	}

	public boolean isCurrencyConversionOrPrzelew() {
		return this == CsvTransactionType.CURRENCY_CONVERSION || this == CsvTransactionType.MONEY_TRANSFER;
	}
}

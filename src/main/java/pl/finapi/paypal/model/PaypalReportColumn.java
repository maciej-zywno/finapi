package pl.finapi.paypal.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.Assert;

import pl.finapi.paypal.source.report.Language;

public enum PaypalReportColumn {

	DATE("Data", "Date"),
	TIME("Godzina", "Time"),
	TIMEZONE("Strefa czasowa", "Time Zone"),
	NAME_AND_SURNAME("Imię i nazwisko (nazwa)", "Name"),
	TYPE("Typ", "Type"),
	STATUS("Status", "Status"),
	SUBJECT("Temat", "Subject"),
	CURRENCY("Waluta", "Currency"),
	BRUTTO("Brutto", "Gross"),
	FEE("Opłata", "Fee"),
	NETTO("Netto", "Net"),
	NOTE("Uwaga", "Note"),
	FROM_EMAIL("Z adresu e-mail", "From Email Address"),
	TO_EMAIL("Na adres e-mail", "To Email Address"),
	TRANSACTION_ID("Numer identyfikacyjny transakcji", "Transaction ID"),
	PAYMENT_TYPE("Rodzaj płatności", "Payment Type"),
	KONTRAHENT_STATUS("Status kontrahenta", "Counterparty Status"),
	SHIPPING_ADDRESS("Adres wysyłkowy", "Shipping Address"),
	ADDRESS_STATUS("Status adresu", "Address Status"),
	ITEM_NAME("Nazwa przedmiotu", "Item Title"),
	ITEM_ID("Identyfikator przedmiotu", "Item ID"),
	SHIPPING_COST("Koszt wysyłki oraz koszty manipulacyjne", "Shipping and Handling Amount"),
	INSURANCE_COST("Kwota ubezpieczenia", "Insurance Amount"),
	SALES_TAX("Podatek od sprzedaży", "Sales Tax"),
	OPTION1_NAME("Nazwa opcji 1", "Option 1 Name"),
	OPTION1_VALUE("Wartość opcji 1", "Option 1 Value"),
	OPTION2_NAME("Nazwa opcji 2", "Option 2 Name"),
	OPTION2_VALUE("Wartość opcji 2", "Option 2 Value"),
	AUCTION_WEBSITE("Witryna aukcji", "Auction Site"),
	BUYER_NAME("Nazwa kupującego", "Buyer ID"),
	ITEM_URL("Adres URL przedmiotu", "Item URL"),
	CLOSE_DATE("Data zamknięcia", "Closing Date"),
	ITEM_ESCROW_ID("Numer identyfikacyjny w systemie Escrow", "Escrow Id"),
	INVOICE_ID("Identyfikator faktury", "Invoice Id"),
	ITEM_TXN_ID("Txn ID przedmiotu", "Reference Txn ID"),
	INVOICE_NUMBER("Numer faktury", "Invoice Number"),
	SUBSCRIPTION_NUMBER("Numer subskrypcji", "Subscription Number"),
	NON_STANDARD_NUMBER("Numer niestandardowy", "Custom Number"),
	QUANTITY("Ilość", "Quantity"),
	CONFIRMATION_IDENTIFIER("Identyfikator potwierdzenia", "Receipt ID"),
	SALDO("Saldo", "Balance"),
	ADDRESS1("Adres — wiersz 1", "Address Line 1"),
	ADDRESS2("Adres — wiersz 2/dzielnica/osiedle",//
			"Address Line 2/District/Neighborhood"),
	CITY("Miejscowość", "Town/City"),
	STATE("Stan/prowincja/województwo/region/terytorium/prefektura/republika", "State/Province/Region/County/Territory/Prefecture/Republic"),
	ZIP_CODE("Kod pocztowy", "Zip/Postal Code"),
	COUNTRY("Kraj", "Country"),
	PHONE("Numer telefonu do kontaktu", "Contact Phone Number"),
	BALANCE_IMPACT("Wpływ na saldo", "Balance Impact");

	private static final List<PaypalReportColumn> requiredColumns = Arrays.asList(new PaypalReportColumn[] { BRUTTO, FROM_EMAIL, FEE, CURRENCY,
			NAME_AND_SURNAME, NETTO, STATUS, TIMEZONE, DATE, TIME, TRANSACTION_ID, TYPE });

	private static final Map<String, PaypalReportColumn> polishNameToEnumMap = new HashMap<>();
	static {
		for (PaypalReportColumn paypalReportColumn : PaypalReportColumn.values()) {
			polishNameToEnumMap.put(paypalReportColumn.polishName, paypalReportColumn);
		}
	}
	private static final Map<String, PaypalReportColumn> englishNameToEnumMap = new HashMap<>();
	static {
		for (PaypalReportColumn paypalReportColumn : PaypalReportColumn.values()) {
			englishNameToEnumMap.put(paypalReportColumn.englishName, paypalReportColumn);
		}
	}

	private final String polishName;
	private final String englishName;

	private PaypalReportColumn(String polishName, String englishName) {
		this.polishName = polishName;
		this.englishName = englishName;
	}

	private static PaypalReportColumn getByPolishName(String polishName) {
		PaypalReportColumn paypalReportColumn = polishNameToEnumMap.get(polishName);
		Assert.notNull(paypalReportColumn);
		return paypalReportColumn;
	}

	private static PaypalReportColumn getByEnglishName(String englishName) {
		PaypalReportColumn paypalReportColumn = englishNameToEnumMap.get(englishName);
		Assert.notNull(paypalReportColumn);
		return paypalReportColumn;
	}

	public String getPolishName() {
		return polishName;
	}

	public String getEnglishName() {
		return englishName;
	}

	public static Set<PaypalReportColumn> getRequired() {
		return new HashSet<>(requiredColumns);
	}

	public static PaypalReportColumn getByName(String headerCell, Language language) {
		switch (language) {
		case EN:
			return getByEnglishName(headerCell);
		case PL:
			return getByPolishName(headerCell);
		default:
			throw new RuntimeException("unsupported language " + language);
		}
	}
}

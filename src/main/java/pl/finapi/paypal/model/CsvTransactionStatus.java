package pl.finapi.paypal.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

import pl.finapi.paypal.source.report.Language;

public enum CsvTransactionStatus {

	PENDING("Pending", "Oczekujące"),
	REFUNDED("Refunded", "Nastąpił zwrot pieniędzy"),
	PARTIALLY_REFUNDED("Partially Refunded", "Objęta częściowym zwrotem pieniędzy"),
	CANCELED("Canceled", "Anulowano"),
	COMPLETED("Completed", "Zakończone"),
	CLEARED("", "Zwolniona"),
	HELD("", "Nałożona"),
	REVERSED("Reversed", "Cofnięta"),
	REFUNDED_FROM_EBAY("", "Nastąpił zwrot pieniędzy z serwisu eBay"),
	REMOVED("Removed", "Usunięta"),
	PAID("", "Zapłacona"),
	Wstrzymana("", "Wstrzymana"),
	Rozliczona("", "Rozliczona");

	private static final Map<String, CsvTransactionStatus> polishNameToEnumMap = new HashMap<>();
	static {
		for (CsvTransactionStatus type : CsvTransactionStatus.values()) {
			polishNameToEnumMap.put(type.polish, type);
		}
	}
	private static final Map<String, CsvTransactionStatus> englishNameToEnumMap = new HashMap<>();
	static {
		for (CsvTransactionStatus type : CsvTransactionStatus.values()) {
			englishNameToEnumMap.put(type.english, type);
		}
	}

	private final String english;
	private final String polish;

	private CsvTransactionStatus(String english, String polish) {
		this.english = english;
		this.polish = polish;

	}

	private static CsvTransactionStatus getByPolishName(String polishName) {
		CsvTransactionStatus status = polishNameToEnumMap.get(polishName);
		if (status == null) {
			System.out.println(polishName);
		}
		Assert.notNull(status);
		return status;
	}

	private static CsvTransactionStatus getByEnglishName(String englishName) {
		CsvTransactionStatus status = englishNameToEnumMap.get(englishName);
		if (status == null) {
			System.out.println(englishName);
		}
		Assert.notNull(status);
		return status;
	}

	public static CsvTransactionStatus getByName(String headerCell, Language language) {
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

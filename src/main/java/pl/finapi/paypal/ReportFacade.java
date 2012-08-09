package pl.finapi.paypal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.finapi.paypal.model.AccountantInfo;
import pl.finapi.paypal.model.Amount;
import pl.finapi.paypal.model.BuyerInfo;
import pl.finapi.paypal.model.City;
import pl.finapi.paypal.model.CsvTransactionStatus;
import pl.finapi.paypal.model.CsvTransactionType;
import pl.finapi.paypal.model.Currency;
import pl.finapi.paypal.model.DateTimeRange;
import pl.finapi.paypal.model.Day;
import pl.finapi.paypal.model.EmailAddress;
import pl.finapi.paypal.model.ExchangeRate;
import pl.finapi.paypal.model.IdentyfikatorKursu;
import pl.finapi.paypal.model.IdentyfikatorKursuSource;
import pl.finapi.paypal.model.PaypalReportDataWithSaldoAndStanWaluty;
import pl.finapi.paypal.model.SaldoAndStanWaluty;
import pl.finapi.paypal.model.SposobObliczania;
import pl.finapi.paypal.model.StanWaluty;
import pl.finapi.paypal.model.TransactionLine;
import pl.finapi.paypal.model.TransactionSummary;
import pl.finapi.paypal.model.output.DocumentModels;
import pl.finapi.paypal.model.output.DowodWewnetrznyModel;
import pl.finapi.paypal.model.output.ExchangeRateDifferenceReportLine;
import pl.finapi.paypal.model.output.ExchangeRateDifferenceReportModel;
import pl.finapi.paypal.model.output.ExchangeRateDifferenceReportModels;
import pl.finapi.paypal.model.output.PaypalFeeInvoiceDates;
import pl.finapi.paypal.model.output.PaypalFeeInvoiceModel;
import pl.finapi.paypal.model.output.PaypalFeeReportModel;
import pl.finapi.paypal.model.output.TrescOperacji;
import pl.finapi.paypal.model.roznice.AmountAtRate;
import pl.finapi.paypal.source.report.PaypalReport;
import pl.finapi.paypal.source.report.PaypalReportData;
import pl.finapi.paypal.source.report.ReportToTransactionSummaryConverter;
import pl.finapi.paypal.util.Calculator;
import pl.finapi.paypal.util.CollectionUtil;
import pl.finapi.paypal.util.MonthSpeaker;
import pl.finapi.paypal.util.NumberUtil;
import pl.finapi.paypal.util.PaypalReportChunker;
import pl.finapi.paypal.util.TimeUtil;
import pl.finapi.paypal.util.TransactionLineFilter;
import pl.finapi.paypal.util.TransactionSummaryFilter;

@Component
public class ReportFacade {

	private final Calculator calculator;
	private final CsvModelConverter lineModelConverter;
	private final CollectionUtil collectionUtil;
	private final ExchangeRateService exchangeRateService;
	private final TransactionLineFilter transactionLineFilter;
	private final StanWalutyCalculator stanWalutyCalculator;
	private final TimeUtil timeUtil;
	private final ReportToTransactionSummaryConverter converter;
	private final PaypalReportChunker reportChunker;
	private final MonthSpeaker monthSpeaker;
	private final NumberUtil numberUtil;

	@Autowired
	public ReportFacade(Calculator calculator, CsvModelConverter lineModelConverter, CollectionUtil collectionUtil, TransactionSummaryFilter transactionFilter,
			ExchangeRateService exchangeRateService, TransactionLineFilter transactionLineFilter, StanWalutyCalculator stanWalutyCalculator, TimeUtil timeUtil,
			ReportToTransactionSummaryConverter converter, PaypalReportChunker reportChunker, MonthSpeaker monthSpeaker, NumberUtil numberUtil) {
		this.calculator = calculator;
		this.lineModelConverter = lineModelConverter;
		this.collectionUtil = collectionUtil;
		this.exchangeRateService = exchangeRateService;
		this.transactionLineFilter = transactionLineFilter;
		this.stanWalutyCalculator = stanWalutyCalculator;
		this.timeUtil = timeUtil;
		this.converter = converter;
		this.reportChunker = reportChunker;
		this.monthSpeaker = monthSpeaker;
		this.numberUtil = numberUtil;
	}

	public List<DocumentModels> createModels(PaypalReport report, EmailAddress emailAddress, City city, AccountantInfo accountantInfo, BuyerInfo buyerInfo) {

		PaypalReportData reportData = converter.parse(report);

		List<PaypalReportData> monthChunks = reportChunker.divideIntoMonthChunks(reportData);

		List<DocumentModels> list = new ArrayList<>();

		// just for initial saldo and stan waluty checking
		Map<Currency, ExchangeRateDifferenceReportModels> currencyToLastExchangeRateDifferenceReportModelsMap = new HashMap<>();

		for (PaypalReportData oneMonthPaypalReportData : monthChunks) {

			PaypalFeeReportModel oneMonthPaypalFeeReportModel = createPaypalFeeReportModel(oneMonthPaypalReportData, buyerInfo);

			Map<Currency, PaypalReportData> currencyToOneMonthReportDatas = reportChunker.divideIntoCurrencyChunks(oneMonthPaypalReportData);

			Map<Currency, ExchangeRateDifferenceReportModels> currencyToExchangeRateDifferenceReportModelsMap = new HashMap<>();

			for (Entry<Currency, PaypalReportData> entry : currencyToOneMonthReportDatas.entrySet()) {

				// saldoAndStanWaluty is:
				// a) for the first month : first transaction saldo minus first transaction amount
				// b) for any other month: the last line of previous ExchangeRateDifferenceReportModel
				SaldoAndStanWaluty foreignSaldoAndStanWaluty = findForeignSaldoAndStanWaluty(reportData, currencyToLastExchangeRateDifferenceReportModelsMap,
						entry.getKey());

				ExchangeRateDifferenceReportModels exchangeRateDifferenceReportModels = calculateExchangeRateDifferenceReportModels(foreignSaldoAndStanWaluty,
						emailAddress, city, oneMonthPaypalReportData, entry.getKey(), entry.getValue(), accountantInfo);
				currencyToExchangeRateDifferenceReportModelsMap.put(entry.getKey(), exchangeRateDifferenceReportModels);

				// just for initial saldo and stan waluty checking
				currencyToLastExchangeRateDifferenceReportModelsMap.put(entry.getKey(), exchangeRateDifferenceReportModels);
			}

			PaypalFeeInvoiceModel paypalFeeInvoiceModel = createPaypalFeeInvoiceModel(oneMonthPaypalFeeReportModel);
			list.add(new DocumentModels(oneMonthPaypalFeeReportModel, paypalFeeInvoiceModel, currencyToExchangeRateDifferenceReportModelsMap));
		}
		return list;
	}

	private SaldoAndStanWaluty findForeignSaldoAndStanWaluty(PaypalReportData reportData,
			Map<Currency, ExchangeRateDifferenceReportModels> currencyToLastExchangeRateDifferenceReportModelsMap, Currency currency) {
		if (currencyToLastExchangeRateDifferenceReportModelsMap.get(currency) == null) {
			return calculateInitialSaldoAndStanWaluty(currency, reportData.getTransactionSummaries());
		} else {
			return extractLastMonthFinalSaldoAndStanWaluty(currencyToLastExchangeRateDifferenceReportModelsMap.get(currency));
		}
	}

	private PaypalFeeInvoiceModel createPaypalFeeInvoiceModel(PaypalFeeReportModel reportModel) {
		PaypalFeeInvoiceDates paypalFeeInvoiceDates = createPaypalFeeInvoiceDates(reportModel);
		return new PaypalFeeInvoiceModel(reportModel.getTransactionFeeInPlnSum(), paypalFeeInvoiceDates.getTransactionMonthYearAsString(),
				reportModel.getBuyerInfo(), paypalFeeInvoiceDates.getCreationDay());
	}

	private PaypalFeeInvoiceDates createPaypalFeeInvoiceDates(PaypalFeeReportModel reportModel) {
		DateTime endDateTime = reportModel.getTransactionDateRange().getEndDateTime();
		Day warsawDay = timeUtil.toLastDayOfSameMonth(endDateTime);
		String transactionMonthYearAsString = monthSpeaker.asMonthAndYearSpaceDelimited(warsawDay);
		return new PaypalFeeInvoiceDates(warsawDay, transactionMonthYearAsString);
	}

	public PaypalFeeReportModel createPaypalFeeReportModel(PaypalReportData report, BuyerInfo buyerInfo) {
		List<TransactionLine> lines = filterZeroFeeTransactions(toTransactionLines(report));
		DateTimeRange dateTimeRange = calculator.findDateTimeRange(lines);
		return new PaypalFeeReportModel(lines, calculator.sum(lines), dateTimeRange, buyerInfo);
	}

	private List<TransactionLine> filterZeroFeeTransactions(List<TransactionLine> transactionLines) {
		List<TransactionLine> nonZero = new ArrayList<>();
		for (TransactionLine transactionLine : transactionLines) {
			if (transactionLine.isFeeAvailable()) {
				nonZero.add(transactionLine);
			}
		}
		return nonZero;
	}

	public ExchangeRateDifferenceReportModels createExchangeRateDifferenceReportModel(
			PaypalReportDataWithSaldoAndStanWaluty paypalReportDataWithSaldoAndStanWaluty, EmailAddress emailAddress, City city, Currency currency,
			AccountantInfo accountantInfo) {

		List<TransactionLine> lines = toTransactionLines(paypalReportDataWithSaldoAndStanWaluty.getPaypalReportData());
		StanWaluty initialStanWaluty = paypalReportDataWithSaldoAndStanWaluty.getSaldoAndStanWaluty().getStanWaluty();
		Amount initialSaldoInForeignCurrency = paypalReportDataWithSaldoAndStanWaluty.getSaldoAndStanWaluty().getSaldo();
		// TODO: hardcoded beginning of the month PLN saldo
		Amount initialSaldoInPLNCurrency = numberUtil.zeroAmount();

		return createExchangeRateDifferenceReportModel(lines, currency, initialSaldoInForeignCurrency, initialSaldoInPLNCurrency, initialStanWaluty, city,
				emailAddress, accountantInfo);
	}

	private ExchangeRateDifferenceReportModels calculateExchangeRateDifferenceReportModels(SaldoAndStanWaluty saldoAndStanWaluty, EmailAddress emailAddress,
			City city, PaypalReportData reportData, Currency currency, PaypalReportData paypalReportData, AccountantInfo wystawilName) {

		PaypalReportDataWithSaldoAndStanWaluty paypalReportDataWithSaldoAndStanWaluty = new PaypalReportDataWithSaldoAndStanWaluty(saldoAndStanWaluty,
				paypalReportData);
		ExchangeRateDifferenceReportModels exchangeRateDifferenceReportModels = createExchangeRateDifferenceReportModel(paypalReportDataWithSaldoAndStanWaluty,
				emailAddress, city, currency, wystawilName);

		return exchangeRateDifferenceReportModels;
	}

	private SaldoAndStanWaluty extractLastMonthFinalSaldoAndStanWaluty(ExchangeRateDifferenceReportModels exchangeRateDifferenceReportModels) {
		List<ExchangeRateDifferenceReportLine> transactionLines = exchangeRateDifferenceReportModels.getExchangeRateDifferenceReportModel()
				.getTransactionLines();
		ExchangeRateDifferenceReportLine line = collectionUtil.getLastBut1(transactionLines);
		StanWaluty stanWaluty = line.getStanWaluty();
		Amount saldo = stanWaluty.sum();
		SaldoAndStanWaluty s = new SaldoAndStanWaluty(saldo, stanWaluty);
		return s;
	}

	private SaldoAndStanWaluty calculateInitialSaldoAndStanWaluty(Currency currency, List<TransactionSummary> transactionSummaries) {

		// initial saldo = first transaction saldo minus first transaction amount
		// initial stan waluty = initial saldo at exchange rate of the previous (to first transaction day) day

		TransactionSummary firstTransaction = getFirstForeignTransactionInCurrency(currency, transactionSummaries);
		Amount saldoAfterFirstTransaction = firstTransaction.getSaldo();
		Amount firstTransactionAmount = firstTransaction.getNettoAmount();
		Amount initialSaldo = saldoAfterFirstTransaction.minus(firstTransactionAmount);

		List<AmountAtRate> stanWalutyList = new ArrayList<>();
		Currency firstTransactionCurrency = firstTransaction.getCurrency();
		Amount exchangeRate;
		if (firstTransactionCurrency.isForeign()) {
			exchangeRate = getExchangeRate(firstTransactionCurrency, firstTransaction.getDateTime());
		} else {
			exchangeRate = ExchangeRate.PLN.getExchangeRate();
		}
		stanWalutyList.add(new AmountAtRate(initialSaldo, exchangeRate));

		StanWaluty stanWaluty = new StanWaluty(stanWalutyList);
		SaldoAndStanWaluty saldoAndStanWaluty = new SaldoAndStanWaluty(initialSaldo, stanWaluty);
		return saldoAndStanWaluty;
	}

	private TransactionSummary getFirstForeignTransactionInCurrency(Currency currency, List<TransactionSummary> transactionSummaries) {
		for (TransactionSummary transactionSummary : transactionSummaries) {
			if (transactionSummary.getCurrency() == currency) {
				return transactionSummary;
			}
		}
		throw new RuntimeException("no transaction found in currency " + currency);
	}

	private Amount getExchangeRate(Currency currency, DateTime dateTime) {
		Day day = timeUtil.toWarsawDay(dateTime);
		return exchangeRateService.findExchangeRate(currency, day).getValue().getExchangeRate().getExchangeRate();
	}

	private ExchangeRateDifferenceReportModels createExchangeRateDifferenceReportModel(List<TransactionLine> transactionLines, Currency currency,
			Amount initialSaldoInForeignCurrency, Amount initialSaldoInPLNCurrency, StanWaluty initialStanWaluty, City city, EmailAddress emailAddress,
			AccountantInfo accountantInfo) {

		List<ExchangeRateDifferenceReportLine> lines = new ArrayList<>();

		Amount saldoInForeignCurrencyBeforeTransaction = initialSaldoInForeignCurrency;
		Amount saldoInPLNCurrencyBeforeTransaction = initialSaldoInPLNCurrency;

		// 2
		//
		// pierwszy rekord to saldo poczatkowe BEZ DATY
		// ostatni rekord to saldo koncowe BEZ DATY
		ExchangeRateDifferenceReportLine firstLine = new ExchangeRateDifferenceReportLine();
		firstLine.setTransactionDateTime(collectionUtil.getFirst(transactionLines).getTransactionDateTime());
		firstLine.setTrescOperacji(TrescOperacji.SALDO_POCZATKOWE);
		firstLine.setStanWaluty(initialStanWaluty);

		lines.add(firstLine);

		// 3 set initial previous ExchangeRateDifferenceReportLine (that will change inside the loop)
		ExchangeRateDifferenceReportLine previousLine = firstLine;

		// 4

		// iterate through all transactions

		for (TransactionLine transactionLine : transactionLines) {

			// 1 ignoruj rekordy ktore:
			//
			// a) maja typ "Wypłać środki na rachunek bankowy" i saldo PLN
			// b) maja typ "transakcje niepotwierdzone"
			// List<TransactionLine> filteredTransactionLines = transactionLineFilter.filterOutNonSellRelatedTransactions(transactionLines);
			if (transactionLineFilter.isNonSellRelated(transactionLine)) {
				// skip, BUT if it's withdraw to PLN bank account we need to remember new PLN saldo
				if (transactionLine.getTransactionType() == CsvTransactionType.WITHDRAW_FUNDS_TO_BANK_ACCOUNT && transactionLine.getCurrency().isPLN()) {
					saldoInPLNCurrencyBeforeTransaction = transactionLine.getSaldo();
				}
				continue;
			}

			Amount saldoAfterTransaction = transactionLine.getSaldo();
			boolean shouldSetNewPreviousLine = true;

			ExchangeRateDifferenceReportLine line = new ExchangeRateDifferenceReportLine();

			StanWaluty peviousStanWaluty = previousLine.getStanWaluty();

			// PRZELICZENIE or PRZELEW are actually "currency conversion". It's always two lines:
			// 1st line - is taking from origin saldo
			// 2nd line - is putting to destination saldo

			// PRZELICZENIE WALUTY - 1st part
			// a) from foreign currency to pln
			if (saldoAfterTransaction.isLessThan(saldoInForeignCurrencyBeforeTransaction) && transactionLine.isCurrencyConversionToPLNInForeignSaldo()) {
				line.setTransactionDateTime(transactionLine.getTransactionDateTime());
				line.setTrescOperacji(TrescOperacji.SPRZEDAZ_WALUTY);
				line.setAmountFlow(transactionLine.getNettoAmount());
				line.setStanWaluty(peviousStanWaluty);
				// after this transaction line, there will come a corresponding currency conversion line but in PLN currency
				// once it comes we will know how many PLN we got from transactionLine.getNettoAmount()
			}
			// b) from pln to foreign currency
			else if (transactionLine.getNettoAmount().isNegative() && transactionLine.isCurrencyConversionToForeignCurrencyInPLNSaldo()) {
				line.setTransactionDateTime(transactionLine.getTransactionDateTime());
				line.setTrescOperacji(TrescOperacji.KUPNO_WALUTY);
				line.setAmountFlow(transactionLine.getNettoAmount());
				line.setStanWaluty(peviousStanWaluty);
			}
			// c) from foreign currency A _TO_ foreign currency B
			else if (transactionLine.isCurrencyConversionToForeignCurrencyAInForeignCurrencyBSaldo()) {
				// this transaction is present in two ReportData so we do this check not to calculate the same twice
				if (currency == transactionLine.getCurrency()) {
					// here we just:
					// 1) DECREASE stanWaluty in one foreign currency
					// 2) empty sposobObliczania (no roznice kursowe despite foreign saldo change)

					// it's TO type so amount is negative
					assertNegative(transactionLine.getNettoAmount());

					line.setTransactionDateTime(transactionLine.getTransactionDateTime());
					line.setTrescOperacji(TrescOperacji.OBA_SALDA_WALUTOWE);

					// dummy - let's hope paypalExchangeRate will not be used by stanWalutyCalculator
					// if we end up with negative saldo in this currency (which we don't anticipate) we are screwed
					Amount paypalExchangeRate = new Amount(BigDecimal.ZERO);

					// sposob obliczania is ignored as there are no roznicekursowe at this point
					Pair<StanWaluty, SposobObliczania> stanWalutyAndSposobObliczaniaPair = stanWalutyCalculator
							.calculateStanWalutyAndSposobObliczaniaPairForForeignCurrencySale(new AmountAtRate(transactionLine.getNettoAmount(),
									paypalExchangeRate), peviousStanWaluty);

					line.setStanWaluty(stanWalutyAndSposobObliczaniaPair.getLeft());
					line.setSposobObliczania(SposobObliczania.EMPTY);

					line.setExchangeRate(ExchangeRate.NO_RATE);

				} else if (currency == transactionLine.getName().getAssociatedCurrency()) {
					Amount paypalExchangeRate = new Amount(new BigDecimal(1));
					Pair<StanWaluty, SposobObliczania> stanWalutyAndSposobObliczaniaPair = stanWalutyCalculator
							.calculateStanWalutyAndSposobObliczaniaPairForForeignCurrencySale(new AmountAtRate(transactionLine.getNettoAmount(),
									paypalExchangeRate), peviousStanWaluty);

					line.setStanWaluty(stanWalutyAndSposobObliczaniaPair.getLeft());
					line.setSposobObliczania(SposobObliczania.EMPTY);

				} else {
					throw new RuntimeException("associated currency " + transactionLine.getName().getAssociatedCurrency() + ", currentCurrency " + currency
							+ ", transaction " + transactionLine);
				}
				throw new RuntimeException("not implemented");
			}
			// PRZELICZENIE WALUTY - 2nd part
			// only now we can calculate the Paypal exchange rate applied
			// a) from foreign currency to pln
			else if (transactionLine.isCurrencyConversionFromForeignCurrencyInPLNSaldo()) {
				Amount previousAmountInForeignCurrency = previousLine.getAmountFlow(); // in foreign currency
				Amount currentAmountInPln = transactionLine.getNettoAmount();// in PLN
				Amount paypalExchangeRate = currentAmountInPln.divideBy(previousAmountInForeignCurrency).negate();

				Pair<StanWaluty, SposobObliczania> stanWalutyAndSposobObliczaniaPair = stanWalutyCalculator
						.calculateStanWalutyAndSposobObliczaniaPairForForeignCurrencySale(
								new AmountAtRate(previousAmountInForeignCurrency, paypalExchangeRate), peviousStanWaluty);
				previousLine.setStanWaluty(stanWalutyAndSposobObliczaniaPair.getLeft());
				previousLine.setSposobObliczania(stanWalutyAndSposobObliczaniaPair.getRight());
				IdentyfikatorKursu identyfikatorKursu = new IdentyfikatorKursu("Paypal", IdentyfikatorKursuSource.PAYPAL, timeUtil.toWarsawDay(transactionLine
						.getTransactionDateTime()));
				previousLine.setExchangeRate(new ExchangeRate(Currency.PLN, currency, paypalExchangeRate, identyfikatorKursu));

				shouldSetNewPreviousLine = false;
			}
			// b) from pln to foreign currency
			else if (transactionLine.isCurrencyConversionFromPLNInForeignSaldo()) {
				Amount previousAmountInPln = previousLine.getAmountFlow(); // in PLN
				Amount currentAmountInForeignCurrency = transactionLine.getNettoAmount();// in foreign
				Amount paypalExchangeRate = previousAmountInPln.divideBy(currentAmountInForeignCurrency).negate();

				Pair<StanWaluty, SposobObliczania> stanWalutyAndSposobObliczaniaPair = stanWalutyCalculator
						.calculateStanWalutyAndSposobObliczaniaPairForForeignCurrencySale(new AmountAtRate(currentAmountInForeignCurrency, paypalExchangeRate),
								peviousStanWaluty);
				previousLine.setStanWaluty(stanWalutyAndSposobObliczaniaPair.getLeft());
				previousLine.setSposobObliczania(stanWalutyAndSposobObliczaniaPair.getRight());
				IdentyfikatorKursu identyfikatorKursu = new IdentyfikatorKursu("Paypal", IdentyfikatorKursuSource.PAYPAL, timeUtil.toWarsawDay(transactionLine
						.getTransactionDateTime()));
				previousLine.setExchangeRate(new ExchangeRate(Currency.PLN, currency, paypalExchangeRate, identyfikatorKursu));
				// extra line when compare with currency conversion case
				previousLine.setAmountFlow(transactionLine.getNettoAmount());

				shouldSetNewPreviousLine = false;
			}
			// c) FROM foreign currency A to foreign currency B
			else if (transactionLine.isCurrencyConversionFromForeignCurrencyAInForeignCurrencyBSaldo()) {
				// this transaction is present in two ReportData so we do this check not to calculate the same twice
				if (currency == transactionLine.getCurrency()) {
					// here we just:
					// 1) INCREASE stanWaluty in one foreign currency
					// 2) empty sposobObliczania (no roznice kursowe despite foreign saldo change)

					// it's FROM type so amount is positive
					assertPositive(transactionLine.getNettoAmount());

					line.setTransactionDateTime(transactionLine.getTransactionDateTime());
					line.setTrescOperacji(TrescOperacji.OBA_SALDA_WALUTOWE);

					// dummy - let's hope paypalExchangeRate will not be used by stanWalutyCalculator
					// if we end up with negative saldo in this currency (which we don't anticipate) we are screwed
					Amount paypalExchangeRate = new Amount(BigDecimal.ZERO);

					// sposob obliczania is ignored as there are no roznicekursowe at this point
					Pair<StanWaluty, SposobObliczania> stanWalutyAndSposobObliczaniaPair = stanWalutyCalculator
							.calculateStanWalutyAndSposobObliczaniaPairForForeignCurrencySale(new AmountAtRate(transactionLine.getNettoAmount(),
									paypalExchangeRate), peviousStanWaluty);

					line.setStanWaluty(stanWalutyAndSposobObliczaniaPair.getLeft());
					line.setSposobObliczania(SposobObliczania.EMPTY);

					IdentyfikatorKursu identyfikatorKursu = new IdentyfikatorKursu("Paypal", IdentyfikatorKursuSource.PAYPAL,
							timeUtil.toWarsawDay(transactionLine.getTransactionDateTime()));
					line.setExchangeRate(new ExchangeRate(Currency.PLN, currency, paypalExchangeRate, identyfikatorKursu));

				} else if (currency == transactionLine.getName().getAssociatedCurrency()) {
					// nothing, already completely handled in PRZELICZENIE WALUTY - 1st part, c step
				} else {
					throw new RuntimeException("associated currency " + transactionLine.getName().getAssociatedCurrency() + ", currentCurrency " + currency
							+ ", transaction " + transactionLine);
				}
				throw new RuntimeException("not implemented");
			}
			// PRZELEW - 1st part
			// a) from foreign to pln
			else if (saldoAfterTransaction.isLessThan(saldoInForeignCurrencyBeforeTransaction) && transactionLine.isPrzelewToPLNInForeignSaldo()) {
				line.setTransactionDateTime(transactionLine.getTransactionDateTime());
				line.setTrescOperacji(TrescOperacji.SPRZEDAZ_WALUTY);
				line.setAmountFlow(transactionLine.getNettoAmount());
				line.setStanWaluty(peviousStanWaluty);
				// after this transaction line, there will come a corresponding currency conversion line but in PLN currency
				// once it comes we will know how many PLN we got from transactionLine.getNettoAmount()
			}
			// b) from pln to foreign
			else if (saldoAfterTransaction.isLessThan(saldoInPLNCurrencyBeforeTransaction) && transactionLine.isPrzelewToForeignCurrencyInPLNSaldo()) {
				line.setTransactionDateTime(transactionLine.getTransactionDateTime());
				line.setTrescOperacji(TrescOperacji.KUPNO_WALUTY);
				line.setAmountFlow(transactionLine.getNettoAmount());
				line.setStanWaluty(peviousStanWaluty);
			}
			// PRZELEW - 2nd part
			// only now we can calculate the Paypal exchange rate applied
			// a) from foreign to pln
			else if (transactionLine.isPrzelewFromForeignInPLNSaldo()) {
				Amount previousAmountInForeignCurrency = previousLine.getAmountFlow(); // in foreign currency
				Amount currentAmountInPln = transactionLine.getNettoAmount();// in PLN
				Amount paypalExchangeRate = currentAmountInPln.divideBy(previousAmountInForeignCurrency).negate();

				Pair<StanWaluty, SposobObliczania> stanWalutyAndSposobObliczaniaPair = stanWalutyCalculator
						.calculateStanWalutyAndSposobObliczaniaPairForForeignCurrencySale(
								new AmountAtRate(previousAmountInForeignCurrency, paypalExchangeRate), peviousStanWaluty);
				previousLine.setStanWaluty(stanWalutyAndSposobObliczaniaPair.getLeft());
				previousLine.setSposobObliczania(stanWalutyAndSposobObliczaniaPair.getRight());
				IdentyfikatorKursu identyfikatorKursu = new IdentyfikatorKursu("Paypal", IdentyfikatorKursuSource.PAYPAL, timeUtil.toWarsawDay(transactionLine
						.getTransactionDateTime()));
				previousLine.setExchangeRate(new ExchangeRate(Currency.PLN, currency, paypalExchangeRate, identyfikatorKursu));

				shouldSetNewPreviousLine = false;
			}
			// b) from pln to foreign
			else if (transactionLine.isPrzelewFromPLNInForeignSaldo()) {
				Amount previousAmountInPln = previousLine.getAmountFlow(); // in PLN
				Amount currentAmountInForeignCurrency = transactionLine.getNettoAmount();// in foreign
				Amount paypalExchangeRate = previousAmountInPln.divideBy(currentAmountInForeignCurrency).negate();

				Pair<StanWaluty, SposobObliczania> stanWalutyAndSposobObliczaniaPair = stanWalutyCalculator
						.calculateStanWalutyAndSposobObliczaniaPairForForeignCurrencySale(new AmountAtRate(currentAmountInForeignCurrency, paypalExchangeRate),
								peviousStanWaluty);
				previousLine.setStanWaluty(stanWalutyAndSposobObliczaniaPair.getLeft());
				previousLine.setSposobObliczania(stanWalutyAndSposobObliczaniaPair.getRight());
				IdentyfikatorKursu identyfikatorKursu = new IdentyfikatorKursu("Paypal", IdentyfikatorKursuSource.PAYPAL, timeUtil.toWarsawDay(transactionLine
						.getTransactionDateTime()));
				previousLine.setExchangeRate(new ExchangeRate(Currency.PLN, currency, paypalExchangeRate, identyfikatorKursu));
				// extra line when compare with currency conversion case
				previousLine.setAmountFlow(transactionLine.getNettoAmount());

				shouldSetNewPreviousLine = false;
			}
			// what?
			else if (saldoAfterTransaction.isMoreThan(saldoInForeignCurrencyBeforeTransaction) && transactionLine.isCurrencyConversionToPLNInForeignSaldo()
					&& transactionLine.getName().equals("???")) {
				if (true) {
					throw new RuntimeException("???");
				}
				line.setTransactionDateTime(transactionLine.getTransactionDateTime());
				line.setTrescOperacji(TrescOperacji.KUPNO_WALUTY);
				line.setAmountFlow(transactionLine.getNettoAmount());

				line.setStanWaluty(stanWalutyCalculator.calculateStanWalutyForForeignCurrencyIncome(new AmountAtRate(transactionLine.getNettoAmount(),
						transactionLine.getExchangeRate().getExchangeRate()), previousLine.getStanWaluty()));
				line.setExchangeRate(transactionLine.getExchangeRate());

			}
			// simple case of increasing saldo
			else if (saldoAfterTransaction.isMoreThan(saldoInForeignCurrencyBeforeTransaction)) {
				line.setTransactionDateTime(transactionLine.getTransactionDateTime());
				line.setTrescOperacji(TrescOperacji.WPLYW_SRODKOW);
				line.setAmountFlow(transactionLine.getNettoAmount());
				line.setStanWaluty(stanWalutyCalculator.calculateStanWalutyForForeignCurrencyIncome(new AmountAtRate(transactionLine.getNettoAmount(),
						transactionLine.getExchangeRate().getExchangeRate()), previousLine.getStanWaluty()));
				line.setExchangeRate(transactionLine.getExchangeRate());
			}
			// strange: saldo decreases and isPlatnoscOtzymana - this is in case when Paypal takes their fee but do not update saldo
			// we're going to have "Aktualizacja otrzymanego przelewu bankowego" soon
			else if (saldoAfterTransaction.isLessThan(saldoInForeignCurrencyBeforeTransaction) && isPlatnoscOtrzymana(transactionLine.getTransactionType())) {
				shouldSetNewPreviousLine = false;
			} else if (saldoAfterTransaction.isEqual(saldoInForeignCurrencyBeforeTransaction) && isPlatnoscOtrzymanaCancelledOrPending(transactionLine)) {
				shouldSetNewPreviousLine = false;
			} else if (saldoAfterTransaction.isLessThan(saldoInForeignCurrencyBeforeTransaction)) {
				line.setTransactionDateTime(transactionLine.getTransactionDateTime());
				line.setTrescOperacji(TrescOperacji.WYPLYW_SRODKOW);
				line.setAmountFlow(transactionLine.getNettoAmount());
				Pair<StanWaluty, SposobObliczania> stanWalutyAndSposobObliczaniaPair = stanWalutyCalculator
						.calculateStanWalutyAndSposobObliczaniaPairForForeignCurrencySale(new AmountAtRate(transactionLine.getNettoAmount(), transactionLine
								.getExchangeRate().getExchangeRate()), peviousStanWaluty);
				line.setStanWaluty(stanWalutyAndSposobObliczaniaPair.getLeft());
				line.setSposobObliczania(stanWalutyAndSposobObliczaniaPair.getRight());
				line.setExchangeRate(transactionLine.getExchangeRate());
			} else {
				shouldSetNewPreviousLine = false;
				if (transactionLine.getTransactionType() == CsvTransactionType.CANCELLED_FEE
						|| transactionLine.getTransactionType() == CsvTransactionType.PAYMENT_FROM_EBAY_RECEIVED) {
					// is ok
				} else if (noChangeInSaldo(saldoInForeignCurrencyBeforeTransaction, saldoAfterTransaction)
						&& transactionLine.getTransactionType().isPaymentReceived()) {
					// transakcja ("PayPal Express  Płatnoć otrzymana", "Nastšpił zwrot pieniędzy") sugeruje, ze cos sie stalo z saldem,
					// jednak saldo sie nie zmienia jeszcze w tej transakcji od razu - powinna za jakis czas pojawic sie matching
					// transaction np ("Aktualizacja otrzymanego czeku elektronicznego", "Nastšpił zwrot pieniędzy") i w tej transakcji
					// dopiero zmienia sie saldo
				} else {
					throw new RuntimeException("unsupported transaction " + transactionLine);
				}
				// "Wypłać środki na rachunek bankowy" albo transakcje
				// niepotwierdzone, ale
				// "Wypłać środki na rachunek bankowy"
				// jest juz odfiltrowane wczesniej poniewaz mamy po prostu takie
				// filtrowanie wczesniej zahardkodowanie

			}
			if (shouldSetNewPreviousLine) {
				// 5 add current line to list
				lines.add(line);
				// 7 update previousLine
				previousLine = line;
			}

			// 6 set previous transaction saldo so that the next transcaction knows it
			if (transactionLine.getCurrency() == Currency.PLN) {
				saldoInPLNCurrencyBeforeTransaction = transactionLine.getSaldo();
			} else {
				saldoInForeignCurrencyBeforeTransaction = transactionLine.getSaldo();
			}
		}

		// 7
		//
		// ostatni rekord to saldo koncowe BEZ DATY
		ExchangeRateDifferenceReportLine lastLine = new ExchangeRateDifferenceReportLine();
		DateTime lastDateTimeOfSameMonth = timeUtil.toLastDateTimeOfSameMonth(collectionUtil.getLast(transactionLines).getTransactionDateTime());
		lastLine.setTransactionDateTime(lastDateTimeOfSameMonth);
		// saldoKoncowe line has the same stanwaluty as last transaction line
		lastLine.setStanWaluty(collectionUtil.getLast(lines).getStanWaluty());
		lastLine.setTrescOperacji(TrescOperacji.SALDO_KONCOWE);
		lines.add(lastLine);

		// 8 go over all just created lines and calculate sum of positives and negatives
		Pair<Amount, Amount> negativeAndPositivePair = calculateSumOfNegativeAndPositiveRozniceKursowe(lines);

		ExchangeRateDifferenceReportModel differenceReport = new ExchangeRateDifferenceReportModel(lines, currency, negativeAndPositivePair.getRight(),
				negativeAndPositivePair.getLeft(), timeUtil.calculateDateRange(lines));

		// 9
		DowodWewnetrznyModel dowodWewnetrznyModelForNegative = new DowodWewnetrznyModel(currency, timeUtil.toWarsawDay(lastDateTimeOfSameMonth),
				negativeAndPositivePair.getLeft(), city, emailAddress, accountantInfo);
		DowodWewnetrznyModel dowodWewnetrznyModelForPositive = new DowodWewnetrznyModel(currency, timeUtil.toWarsawDay(lastDateTimeOfSameMonth),
				negativeAndPositivePair.getRight(), city, emailAddress, accountantInfo);
		return new ExchangeRateDifferenceReportModels(differenceReport, dowodWewnetrznyModelForNegative, dowodWewnetrznyModelForPositive);
	}

	private void assertNegative(Amount amount) {
		if (!amount.isNegative()) {
			throw new RuntimeException("expected negative but was " + amount);
		}
	}

	private void assertPositive(Amount amount) {
		if (!amount.isPositive()) {
			throw new RuntimeException("expected positive but was " + amount);
		}
	}

	private boolean noChangeInSaldo(Amount saldoInForeignCurrencyBeforeTransaction, Amount saldoAfterTransaction) {
		return saldoInForeignCurrencyBeforeTransaction.equals(saldoAfterTransaction);
	}

	private Pair<Amount, Amount> calculateSumOfNegativeAndPositiveRozniceKursowe(List<ExchangeRateDifferenceReportLine> lines) {
		BigDecimal sumOfPositives = BigDecimal.ZERO;
		BigDecimal sumOfNegatives = BigDecimal.ZERO;

		for (ExchangeRateDifferenceReportLine line : lines) {
			if (line.getTrescOperacji().isSaldoPoczatkoweLubKoncowe() || line.getSposobObliczania() == null) {
			} else {
				BigDecimal rozniceKursowe = line.getSposobObliczania().calculateResult().getAmount();
				if (rozniceKursowe.signum() == -1) {
					sumOfNegatives = sumOfNegatives.add(rozniceKursowe);
				} else {
					sumOfPositives = sumOfPositives.add(rozniceKursowe);
				}
			}
		}
		return new ImmutablePair<Amount, Amount>(new Amount(sumOfNegatives), new Amount(sumOfPositives));
	}

	private boolean isPlatnoscOtrzymana(CsvTransactionType transactionType) {
		return transactionType == CsvTransactionType.PAYMENT_RECEIVED || transactionType == CsvTransactionType.EXPRESS_CHECKOUT_PAYMENT_RECEIVED
				|| transactionType == CsvTransactionType.MOBILE_EXPRESS_CHECKOUT_PAYMENT_RECEIVED;
	}

	private boolean isPlatnoscOtrzymanaCancelledOrPending(TransactionLine transactionLine) {
		return isPlatnoscOtrzymana(transactionLine.getTransactionType())
				&& (isTransactionCancelled(transactionLine.getTransactionStatus()) || isTransactionPending(transactionLine.getTransactionStatus()));
	}

	private boolean isTransactionCancelled(CsvTransactionStatus transactionStatus) {
		return transactionStatus == CsvTransactionStatus.CANCELED;
	}

	private boolean isTransactionPending(CsvTransactionStatus transactionStatus) {
		return transactionStatus == CsvTransactionStatus.PENDING;
	}

	private List<TransactionLine> toTransactionLines(PaypalReportData report) {

		// filter implementation is dummy!
		// List<TransactionSummary> transactionSummaries = transactionFilter.filterPayment(allTransactions);

		// List<TransactionSummary> paymentLines = transactionFilter.filterPayment(transactionSummaries);

		List<TransactionLine> lines = lineModelConverter.toLineModel(report.getTransactionSummaries(), exchangeRateService);
		return lines;
	}

}

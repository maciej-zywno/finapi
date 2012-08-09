package pl.finapi.paypal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import pl.finapi.paypal.model.Amount;
import pl.finapi.paypal.model.SposobObliczania;
import pl.finapi.paypal.model.StanWaluty;
import pl.finapi.paypal.model.roznice.AmountAtRate;

@Component
public class StanWalutyCalculator {

	// FIFO method
	// the response pair represents two columns: "Stan waluty" and "Sposób obliczania różnic kursowych"
	// transaction is "Sprzedaz waluty" and is negative
	public Pair<StanWaluty, SposobObliczania> calculateStanWalutyAndSposobObliczaniaPairForForeignCurrencySale(AmountAtRate transaction, StanWaluty stanWaluty) {

		// 1 roznice sie nie realizuja
		// a) stanWaluty < 0 && transactionAmount < 0 (np saldo jest ujemne z powodu pobranie przez paypal oplaty przy saldzie zerowym , a
		// nastepnie paypal pobiera jeszcze raz jakies oplaty jeszcze)
		// b) stanWaluty > 0 && transactionAmount > (simple case of consecutive sales)
		// 2 roznice sie realizuja
		// a) stanWaluty < 0 && transactionAmount > 0 (saldo jest ujemne i nastepuje sprzedaz towaru(wplywa kwota w euro))
		// b) stanWaluty > 0 && transactionAmount < 0 (simple case: saldo dodatnie i nastepuje przewalutowanie na PLN)

		// 1a and 1b case - throw exception
		if (bothPositive(transaction, stanWaluty) || bothNegative(transaction, stanWaluty)) {
			throw new RuntimeException("roznice kursowe nie realizuja sie");
		}

		// 2a
		boolean isStanWalutyNonPositive = stanWaluty.isNonPositive();
		boolean isTransactionPositive = transaction.isPositive();
		if (isStanWalutyNonPositive && isTransactionPositive) {
			Pair<StanWaluty, SposobObliczania> pair = calculateStanWalutyPositiveAndTransactionNegative(stanWaluty, transaction);
			return pair;
		}

		// 2b simple case
		if (stanWaluty.isNonNegative() && transaction.isNegative()) {
			Pair<StanWaluty, SposobObliczania> pair = calculateStanWalutyNegativeAndTransactionPositive(stanWaluty, transaction);
			return pair;
		}

		throw new RuntimeException("illegal state");

	}

	private Pair<StanWaluty, SposobObliczania> calculateStanWalutyNegativeAndTransactionPositive(StanWaluty stanWaluty, AmountAtRate transaction) {
		// we will modify the list which is this method argument so in case the method argument it's immutable we use our local copy
		List<AmountAtRate> localWalutyList = new ArrayList<>(stanWaluty.getStanWalutyList());

		List<AmountAtRate> calculations = new ArrayList<>();

		// transaction < 0 so we negate is for our calculations
		BigDecimal left = transaction.getAmount().getAmount();
		left = left.negate();

		Iterator<AmountAtRate> iterator = localWalutyList.iterator();

		// at first iterate through current stanWaluty until full transaction amount is "taken"
		while (iterator.hasNext() && largerThanZero(left)) {

			AmountAtRate stanWalutyAmount = iterator.next();

			if (isLargerOrEqual(left, stanWalutyAmount.getAmount().getAmount())) {
				left = left.subtract(stanWalutyAmount.getAmount().getAmount());
				calculations.add(stanWalutyAmount);
				// Remove current stanWaluty record totally
				iterator.remove();
			} else {
				// Decrease current StanWaluty. As StanWaluty is immutable we need to remove the list element and add a new element but with
				// a decreased quantity.
				iterator.remove();
				localWalutyList.add(0,
						new AmountAtRate(new Amount(stanWalutyAmount.getAmount().getAmount().subtract(left)), stanWalutyAmount.getExchangeRate()));
				calculations.add(new AmountAtRate(new Amount(left), stanWalutyAmount.getExchangeRate()));
				// there is nothing left
				left = BigDecimal.ZERO;
			}
		}

		// however transaction could be as large that current stanWaluty was not enough
		if (largerThanZero(left)) {
			localWalutyList.add(new AmountAtRate(new Amount(left.negate()), transaction.getExchangeRate()));
			calculations.add(new AmountAtRate(new Amount(left), transaction.getExchangeRate()));
		}

		return new ImmutablePair<StanWaluty, SposobObliczania>(new StanWaluty(localWalutyList), new SposobObliczania(calculations, transaction.negate()));
	}

	private Pair<StanWaluty, SposobObliczania> calculateStanWalutyPositiveAndTransactionNegative(StanWaluty stanWaluty, AmountAtRate transaction) {
		// we will modify the list which is this method argument so in case the method argument it's immutable we use our local copy
		List<AmountAtRate> localWalutyList = new ArrayList<>(stanWaluty.getStanWalutyList());

		List<AmountAtRate> calculations = new ArrayList<>();

		BigDecimal left = transaction.getAmount().getAmount();

		Iterator<AmountAtRate> iterator = localWalutyList.iterator();

		// at first iterate through current stanWaluty until full transaction amount is "taken"
		while (iterator.hasNext() && largerThanZero(left)) {

			AmountAtRate stanWalutyAmount = iterator.next();

			if (isLargerOrEqual(left, stanWalutyAmount.getAmount().getAmount().negate())/* negate */) {
				left = left.subtract(stanWalutyAmount.getAmount().getAmount().negate());
				calculations.add(stanWalutyAmount);
				// Remove current stanWaluty record totally
				iterator.remove();
			} else {
				// Decrease current StanWaluty. As StanWaluty is immutable we need to remove the list element and add a new element but with
				// a decreased quantity.
				iterator.remove();
				localWalutyList.add(0,
						new AmountAtRate(new Amount(stanWalutyAmount.getAmount().getAmount().negate().subtract(left)), stanWalutyAmount.getExchangeRate()));
				calculations.add(new AmountAtRate(new Amount(left), stanWalutyAmount.getExchangeRate()));
				// there is nothing left
				left = BigDecimal.ZERO;
			}
		}

		// however transaction could be as large that current stanWaluty was not enough
		if (largerThanZero(left)) {
			localWalutyList.add(new AmountAtRate(new Amount(left), transaction.getExchangeRate()));
			calculations.add(new AmountAtRate(new Amount(left.negate()), transaction.getExchangeRate()));
		}

		return new ImmutablePair<StanWaluty, SposobObliczania>(new StanWaluty(localWalutyList), new SposobObliczania(calculations, transaction.negate()));
	}

	private boolean bothPositive(AmountAtRate transaction, StanWaluty stanWaluty) {
		boolean stanWalutyPositive = stanWaluty.isPositive();
		boolean transactionPositive = transaction.getAmount().isPositive();
		return stanWalutyPositive && transactionPositive;
	}

	private boolean bothNegative(AmountAtRate transaction, StanWaluty stanWaluty) {
		boolean stanWalutyNegative = stanWaluty.isNegative();
		boolean transactionNegative = transaction.getAmount().isNegative();
		return stanWalutyNegative && transactionNegative;
	}

	public StanWaluty calculateStanWalutyForForeignCurrencyIncome(AmountAtRate transaction, StanWaluty stanWaluty) {

		// we will modify the list so in case it's immutable we use our local copy
		List<AmountAtRate> localWalutyList = new ArrayList<>();
		boolean transactionAddedToExistingStanWalutyTransaction = false;
		for (AmountAtRate amountAtRate : stanWaluty.getStanWalutyList()) {
			if (amountAtRate.getExchangeRate().equals(transaction.getExchangeRate())) {
				localWalutyList.add(sum(amountAtRate, transaction));
				transactionAddedToExistingStanWalutyTransaction = true;
			} else {
				localWalutyList.add(amountAtRate);
			}
		}
		if (!transactionAddedToExistingStanWalutyTransaction) {
			localWalutyList.add(transaction);
		}
		return new StanWaluty(localWalutyList);
	}

	private AmountAtRate sum(AmountAtRate amountAtRate, AmountAtRate transaction) {
		return new AmountAtRate(amountAtRate.getAmount().sum(transaction.getAmount()), amountAtRate.getExchangeRate());
	}

	private boolean isLargerOrEqual(BigDecimal first, BigDecimal second) {
		return first.compareTo(second) != -1;
	}

	private boolean largerThanZero(BigDecimal value) {
		return value.compareTo(BigDecimal.ZERO) == +1;
	}

}

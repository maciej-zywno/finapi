package pl.finapi.paypal;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import pl.finapi.paypal.model.Amount;
import pl.finapi.paypal.model.SposobObliczania;
import pl.finapi.paypal.model.StanWaluty;
import pl.finapi.paypal.model.roznice.AmountAtRate;
import pl.finapi.paypal.util.NumberUtil;

public class StanWalutyCalculatorTest {

	private final NumberUtil numberUtil = new NumberUtil();
	private final StanWalutyCalculator calculator = new StanWalutyCalculator();

	@Test
	public void testSimpleCase() {

		AmountAtRate transaction = new AmountAtRate(numberUtil.asAmount(-66.28), numberUtil.asAmount(4.3830));

		List<AmountAtRate> stanWalutyList = new ArrayList<>();
		stanWalutyList.add(new AmountAtRate(numberUtil.asAmount(10.31), numberUtil.asAmount(4.4597)));
		stanWalutyList.add(new AmountAtRate(numberUtil.asAmount(19.01), numberUtil.asAmount(4.4753)));
		stanWalutyList.add(new AmountAtRate(numberUtil.asAmount(36.96), numberUtil.asAmount(4.5135)));

		Pair<StanWaluty, SposobObliczania> result = calculator.calculateStanWalutyAndSposobObliczaniaPairForForeignCurrencySale(transaction, new StanWaluty(
				stanWalutyList));
		Assert.assertTrue(list(result.getLeft().getStanWalutyList()), result.getLeft().getStanWalutyList().isEmpty());
		Assert.assertEquals(-7.37, result.getRight().calculateResult().getAmount().doubleValue(), 0.01);
	}

	@Test
	public void testCalculationsResultsInNegativeSaldo() {

		AmountAtRate transaction = new AmountAtRate(numberUtil.asAmount(-8.28), numberUtil.asAmount(3.9735));

		List<AmountAtRate> stanWalutyList = new ArrayList<>();
		stanWalutyList.add(new AmountAtRate(numberUtil.asAmount(3.52), numberUtil.asAmount(3.9909)));

		Pair<StanWaluty, SposobObliczania> result = calculator.calculateStanWalutyAndSposobObliczaniaPairForForeignCurrencySale(transaction, new StanWaluty(
				stanWalutyList));
		Assert.assertEquals(1, result.getLeft().getStanWalutyList().size());
		Assert.assertEquals(new AmountAtRate(numberUtil.asAmount(-4.76), numberUtil.asAmount(3.9735)), result.getLeft().getStanWalutyList().get(0));
		Amount rozniceKursowe = result.getRight().calculateResult();
		Assert.assertEquals(-0.06, rozniceKursowe.getAmount().doubleValue(), 0.01);
	}

	@Test
	public void testEmptyStartingStanWalutyAndWyplywSrodkow() {

		// transaction 19.82 euro
		AmountAtRate transaction = new AmountAtRate(numberUtil.asAmount(-19.82), numberUtil.asAmount(3.9111));

		// empty starting stanWaluty
		List<AmountAtRate> stanWalutyList = new ArrayList<>();

		// execute
		Pair<StanWaluty, SposobObliczania> result = calculator.calculateStanWalutyAndSposobObliczaniaPairForForeignCurrencySale(transaction, new StanWaluty(
				stanWalutyList));

		// VERIFY
		// 1) one stan waluty record
		Assert.assertEquals(1, result.getLeft().getStanWalutyList().size());
		Assert.assertEquals(new AmountAtRate(numberUtil.asAmount(-19.82), numberUtil.asAmount(3.9111)), result.getLeft().getStanWalutyList().get(0));
		// 2) no roznice kursowe calculations
		Amount rozniceKursowe = result.getRight().calculateResult();
		Assert.assertEquals("roznice kursowe should be 0.0", 0.0, rozniceKursowe.getAmount().doubleValue(), 0.01);
	}

	@Test
	public void testNegativeStartingStanWalutyAndWplywSrodkow() {

		// transaction 19.88 euro
		AmountAtRate transaction = new AmountAtRate(numberUtil.asAmount(19.88), numberUtil.asAmount(4.0433));

		// starting stanWaluty
		List<AmountAtRate> stanWalutyList = new ArrayList<>();
		stanWalutyList.add(new AmountAtRate(numberUtil.asAmount(-19.82), numberUtil.asAmount(3.9111)));

		// execute
		Pair<StanWaluty, SposobObliczania> result = calculator.calculateStanWalutyAndSposobObliczaniaPairForForeignCurrencySale(transaction, new StanWaluty(
				stanWalutyList));

		// VERIFY
		// 1) one stan waluty record
		Assert.assertEquals(1, result.getLeft().getStanWalutyList().size());
		AmountAtRate actualAmountAtRate = result.getLeft().getStanWalutyList().get(0);
		Assert.assertEquals(0.06, actualAmountAtRate.getAmount().getAmount().doubleValue(), 0.01);
		Assert.assertEquals(4.0433, actualAmountAtRate.getExchangeRate().getAmount().doubleValue(), 0.0001);
		// 2) no roznice kursowe calculations
		Amount rozniceKursowe = result.getRight().calculateResult();
		Assert.assertEquals("roznice kursowe should be -2.62", -2.62, rozniceKursowe.getAmount().doubleValue(), 0.01);
	}

	private String list(List<AmountAtRate> amountsAtRate) {
		String text = "";
		for (AmountAtRate amountAtRate : amountsAtRate) {
			text += amountAtRate.getAmount() + ", ";
		}
		return text;
	}

	static {
		ToStringBuilder.setDefaultStyle(ToStringStyle.SHORT_PREFIX_STYLE);
	}
}

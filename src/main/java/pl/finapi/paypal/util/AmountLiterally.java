package pl.finapi.paypal.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.finapi.paypal.model.Amount;

@Component
public class AmountLiterally {

	private final NumberSpeaker numberSpeaker;
	private final NumberUtil numberUtil;

	@Autowired
	public AmountLiterally(NumberSpeaker numberSpeaker, NumberUtil numberUtil) {
		this.numberSpeaker = numberSpeaker;
		this.numberUtil = numberUtil;
	}

	public String amountLiterally(Amount amount) {
		Amount amountAbs = new Amount(amount.getAmount().abs());

		BigDecimal rounded = amountAbs.getAmount().setScale(2, RoundingMode.HALF_UP);
		int zloty = rounded.intValue();
		BigDecimal subtract = rounded.subtract(new BigDecimal(zloty));
		int groszy = subtract.multiply(new BigDecimal(100)).intValue();
		String amountLiterally = StringUtils.strip(numberSpeaker.speakNumber(zloty)) + " zł " + numberUtil.expandToTwoDigits(groszy) + "/100" + " gr";
		return amount.isPositive() ? amountLiterally : "minus " + amountLiterally;
	}

}

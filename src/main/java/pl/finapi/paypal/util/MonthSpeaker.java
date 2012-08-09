package pl.finapi.paypal.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import pl.finapi.paypal.model.Day;

@Component
public class MonthSpeaker {

	private final Map<Integer, String> monthIndexToMonthPolishName = new HashMap<>();
	{
		monthIndexToMonthPolishName.put(1, "styczeń");
		monthIndexToMonthPolishName.put(2, "luty");
		monthIndexToMonthPolishName.put(3, "marzec");
		monthIndexToMonthPolishName.put(4, "kwiecień");
		monthIndexToMonthPolishName.put(5, "maj");
		monthIndexToMonthPolishName.put(6, "czerwiec");
		monthIndexToMonthPolishName.put(7, "lipiec");
		monthIndexToMonthPolishName.put(8, "sierpień");
		monthIndexToMonthPolishName.put(9, "wrzesień");
		monthIndexToMonthPolishName.put(10, "październik");
		monthIndexToMonthPolishName.put(11, "listopad");
		monthIndexToMonthPolishName.put(12, "grudzień");
	}

	public String asMonthAndYearSpaceDelimited(Day day) {
		// "styczeń 2012"
		return monthIndexToMonthPolishName.get(day.getMonth()) + " " + day.getYear();
	}

}

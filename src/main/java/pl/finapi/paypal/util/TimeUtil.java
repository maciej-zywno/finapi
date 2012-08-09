package pl.finapi.paypal.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import pl.finapi.paypal.model.DateTimeRange;
import pl.finapi.paypal.model.Day;
import pl.finapi.paypal.model.DayRange;
import pl.finapi.paypal.model.output.ExchangeRateDifferenceReportLine;

@Component
public class TimeUtil {

	private final DateFormat paypalRequestDayFormat_WarsawTimeZone;
	private final DateFormat yearMonthDayDashDelimitedFormat_WarsawTimeZone;
	private final DateTimeZone warsawDateTimeZone;

	@Autowired
	public TimeUtil(@Value("#{paypalRequestDayFormat_WarsawTimeZone}") DateFormat paypalRequestDayFormat_WarsawTimeZone,
			@Value("#{yearMonthDayDashDelimitedFormat_WarsawTimeZone}") DateFormat yearMonthDayDashDelimitedFormat_WarsawTimeZone,
			@Value("#{warsawDateTimeZone}") DateTimeZone warsawDateTimeZone) {
		this.paypalRequestDayFormat_WarsawTimeZone = paypalRequestDayFormat_WarsawTimeZone;
		this.yearMonthDayDashDelimitedFormat_WarsawTimeZone = yearMonthDayDashDelimitedFormat_WarsawTimeZone;
		this.warsawDateTimeZone = warsawDateTimeZone;
	}

	public Date parseWarsawDay(String date) {
		try {
			return paypalRequestDayFormat_WarsawTimeZone.parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public String formatDateToPaypalRequestDayFormat(DateTime dateTime) {
		return paypalRequestDayFormat_WarsawTimeZone.format(dateTime.toDate());
	}

	public String formatDateToDashDelimitedDayFormat(DateTime dateTime) {
		return yearMonthDayDashDelimitedFormat_WarsawTimeZone.format(dateTime.toDate());
	}

	public DateTime toWarsawDateTime(Date date) {
		return new DateTime(date, warsawDateTimeZone);
	}

	public DateTimeRange parseWarsawDayRange(String startDay, String endDay) {
		DateTime startDateTime = new DateTime(parseWarsawDay(startDay));
		DateTime endDateTime = new DateTime(parseWarsawDay(endDay));
		return new DateTimeRange(startDateTime, endDateTime);
	}

	public Date toWarsawDateTime(Day day) {
		return new DateTime(day.getYear(), day.getMonth(), day.getDay(), 0, 0, 0, warsawDateTimeZone).toDate();
	}

	public Day minusDay(Day day, int howManyDays) {
		DateTime refDateTime = new DateTime(day.getYear(), day.getMonth(), day.getDay(), 0, 0, 0, warsawDateTimeZone);
		DateTime resultDateTime = refDateTime.minusDays(howManyDays);
		return toWarsawDay(resultDateTime);
	}

	public Day toWarsawDay(DateTime dateTime) {
		DateTime warsawDateTime = new DateTime(dateTime, warsawDateTimeZone);
		return new Day(warsawDateTime.getYear(), warsawDateTime.getMonthOfYear(), warsawDateTime.getDayOfMonth());
	}

	public String formatDotDelimited(Day day) {
		// "dd.MM.yyyy"
		return addLeadingZero(day.getDay()) + "." + addLeadingZero(day.getMonth()) + "." + day.getYear();
	}

	public String formatDashDelimited(Day day) {
		// "yyyy-MM-dd"
		return day.getYear() + "-" + addLeadingZero(day.getMonth()) + "-" + addLeadingZero(day.getDay());
	}

	private String addLeadingZero(int number) {
		return number < 10 ? "0" + number : Integer.toString(number);
	}

	public DayRange toWarsawDayRange(DateTimeRange dateTimeRange) {
		Day startDay = toWarsawDay(dateTimeRange.getStartDateTime());
		Day endDay = toWarsawDay(dateTimeRange.getEndDateTime());
		DayRange dayRange = new DayRange(startDay, endDay);
		return dayRange;
	}

	public String formatDotDelimited(DayRange dayRange) {
		String startDayAsString = formatDotDelimited(dayRange.getStartDay());
		String endDayAsString = formatDotDelimited(dayRange.getEndDay());
		String dayRangeAsString = startDayAsString + "-" + endDayAsString;
		return dayRangeAsString;
	}

	public String formatWarsawDayRangeDotDelimited(DateTimeRange dateTimeRange) {
		DayRange dayRange = toWarsawDayRange(dateTimeRange);
		String dayRangeAsString = formatDotDelimited(dayRange);
		return dayRangeAsString;
	}

	public DateTimeRange calculateDateRange(List<ExchangeRateDifferenceReportLine> lines) {
		DateTime startDateTime = new DateTime(Long.MAX_VALUE);
		DateTime endDateTime = new DateTime(0);
		for (ExchangeRateDifferenceReportLine line : lines) {
			if (line.getTransactionDateTime().isAfter(endDateTime)) {
				endDateTime = line.getTransactionDateTime();
			}
			if (line.getTransactionDateTime().isBefore(startDateTime)) {
				startDateTime = line.getTransactionDateTime();
			}
		}
		return new DateTimeRange(startDateTime, endDateTime);
	}

	public String dayRangeDotFormatted(DateTimeRange transactionDateRange) {
		String startDayString = formatDotDelimited(toWarsawDay(transactionDateRange.getStartDateTime()));
		String endDayString = formatDotDelimited(toWarsawDay(transactionDateRange.getEndDateTime()));
		return startDayString + " - " + endDayString;
	}

	public DateTime toLastDateTimeOfSameMonth(DateTime dateTime) {
		DateTime someDayInNextMonth = dateTime.plusMonths(1);
		DateTime firstDayOfNextMonth = new DateTime(someDayInNextMonth.getYear(), someDayInNextMonth.getMonthOfYear(), 1, 0, 0, 0, someDayInNextMonth.getZone());
		DateTime lastDayOfMonth = firstDayOfNextMonth.minusDays(1);
		return lastDayOfMonth;
	}

	public DateTime nextMonthStartDateTime(DateTime dateTime) {
		DateTime someDayInNextMonth = dateTime.plusMonths(1);
		DateTime firstDayOfNextMonth = new DateTime(someDayInNextMonth.getYear(), someDayInNextMonth.getMonthOfYear(), 1, 0, 0, 0, someDayInNextMonth.getZone());
		return firstDayOfNextMonth;
	}

	public Day toLastDayOfSameMonth(DateTime dateTime) {
		DateTime lastDayOfSameMonth = toLastDateTimeOfSameMonth(dateTime);
		return toWarsawDay(lastDayOfSameMonth);
	}

}

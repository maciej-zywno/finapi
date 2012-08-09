package pl.finapi.paypal.util;

import org.springframework.stereotype.Component;

@Component
public class NumberSpeaker {

	private final String[] teens = { "", "jeden", "dwa", "trzy", "cztery", "pięć", "sześć", "siedem", "osiem", "dziewięć", "dziesięć",
			"jedenaście", "dwanaście", "trzynaście", "czternaście", "piętnaście", "szesnaście", "siedemnaście", "osiemnaście",
			"dziewiętnaście" };
	private final String _20 = "dwadzieścia";
	private final String _30 = "trzydzieści";
	private final String _40 = "czterdzieści";
	private final String _1e1 = "dziesiąt";
	private final String _100 = "sto";
	private final String _200 = "dwieście";
	private final String _300 = "trzysta";
	private final String _400 = "czterysta";
	private final String _1e2 = "set";
	private final String _1000 = "tysiąc";

	private final String[] _1e3suff = { "ące", "ęcy" };
	private final String[] _1e6suff = { "y", "ów" };

	private final String _1e3 = "tysi";

	private final String _1e6 = "milion";
	private final String _1e9 = "miliard";
	private final String _1e12 = "bilion";
	private final String _1e15 = "biliard";
	private final String _1e18 = "trylion";

	private final int secSize = 3; // po 3 cyfry w sekcji

	public String speakNumber(long value) {
		StringBuffer buffer = new StringBuffer();
		String stringValue = String.valueOf(value);
		int length = stringValue.length();
		int steps = length / secSize;
		int remain = length % secSize;
		if (length < secSize) {
			steps = 0;
		}
		if (remain != 0) {
			steps++;
		}
		// Bierzemy poszczególne sekcje po 3 cyfry
		for (int i = 1; i <= steps; i++) {
			int begin = length - (i * secSize);
			if (begin < 0) {
				begin = 0;
			}
			int end = length - ((i - 1) * secSize);
			String subValue = stringValue.substring(begin, end);
			// tworzymy sekcje wraz z sufiksami
			buffer.insert(0, decode(subValue) + " " + determineSuffix(subValue, i) + " ");
		}
		return new String(buffer);
	}

	public String speakNumber(int value) {
		return speakNumber((long) value);
	}

	public String speakNumber(byte value) {
		return speakNumber(new Byte(value).longValue());
	}

	public String speakNumber(String value) {
		return speakNumber(Long.parseLong(value));
	}

	private String decode(String number) {
		String retval;
		int num = Integer.parseInt(number);

		if (num < 20) {
			retval = (teens[num]);
		} else if ((num >= 20) && (num < 30)) {
			retval = (_20 + " " + teens[num - 20]);
		} else if ((num >= 30) && (num < 40)) {
			retval = (_30 + " " + teens[num - 30]);
		} else if ((num >= 40) && (num < 50)) {
			retval = (_40 + " " + teens[num - 40]);
		} else if ((num >= 50) && (num < 100)) {
			int i = num / 10;
			int j = num % 10;
			retval = (teens[i] + _1e1 + " " + teens[j]);
		} else if ((num >= 100) && (num < 200)) {
			retval = (_100 + " " + decode(num % 100));
		} else if ((num >= 200) && (num < 300)) {
			retval = (_200 + " " + decode(num % 200));
		} else if ((num >= 300) && (num < 400)) {
			retval = (_300 + " " + decode(num % 300));
		} else if ((num >= 400) && (num < 500)) {
			retval = (_400 + " " + decode(num % 400));
		} else if ((num >= 500) && (num < 1000)) {
			int i = num / 100;
			int j = num % 100;
			retval = (teens[i] + _1e2 + " " + decode(j));
		} else
			retval = null;
		return retval;
	}

	private String decode(int number) {
		return decode(String.valueOf(number));
	}

	private String determineSuffix(String number, int position) {
		String suffix = "";
		if (number.equals("000")) {
			return suffix;
		}
		// pozycja 2 - tysiące, 3 - miliony, 4 - miliardy itd.
		switch (position) {
		case 1:
			return suffix;
		case 2: // tysiące
			if (number.endsWith("1") && (number.length() == 1))
				suffix = _1000;
			if (!(number.endsWith("1") && number.length() == 1)) {
				suffix = _1e3;
				suffix += suffixHelper(number, _1e3suff);
			}
			return suffix;
		case 3: // miliony
			suffix = _1e6;
			break;
		case 4: // miliardy
			suffix = _1e9;
			break;
		case 5: // biliony
			suffix = _1e12;
			break;
		case 6: // biliardy
			suffix = _1e15;
			break;
		case 7: // tryliony
			suffix = _1e18;
			break;
		}
		// od miliona w górę odmiana regularna
		if (!(number.endsWith("1") && number.length() == 1))
			suffix += suffixHelper(number, _1e6suff);
		return suffix;
	}

	private String suffixHelper(String number, String[] suffixes) {

		if (number.equals("")) {
			return "";
		}
		if ((number.endsWith("11")) || (number.endsWith("12")) || (number.endsWith("13")) || (number.endsWith("14"))) {
			return suffixes[1];
		} else {
			if ((number.endsWith("2")) || (number.endsWith("3")) || (number.endsWith("4"))) {
				return suffixes[0];
			} else {
				return suffixes[1];
			}
		}
	}

	public void main(String[] args) {
		System.out.println("263=" + Long.MAX_VALUE + " " + speakNumber(Long.MAX_VALUE));
		System.out.println("231=" + Integer.MAX_VALUE + " " + speakNumber(Integer.MAX_VALUE));
		System.out.println("27=" + Byte.MAX_VALUE + " " + speakNumber(Byte.MAX_VALUE));
		System.out.println(speakNumber(args[0]));
	}
}
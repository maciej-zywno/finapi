package pl.finapi.paypal.util;

public class Assert {

	public static void notSame(Object o1, Object o2) {
		if (o1.equals(o2)) {
			throw new RuntimeException("should not be the same: object1 = " + o1 + ", object2 = " + o2);
		}
	}

}

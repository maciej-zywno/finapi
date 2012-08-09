package pl.finapi.paypal.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class CollectionUtil {

	public <T> Set<T> asSet(@SuppressWarnings("unchecked") T... objects) {
		return new HashSet<>(Arrays.asList(objects));
	}

	public <T> List<T> asList(@SuppressWarnings("unchecked") T... objects) {
		return Arrays.asList(objects);
	}

	public <T> List<T> sum(@SuppressWarnings("unchecked") List<T>... lists) {
		List<T> sum = new ArrayList<T>();
		for (List<T> list : lists) {
			sum.addAll(list);
		}
		return sum;
	}

	public <T> T getFirst(List<T> elements) {
		if (elements.isEmpty()) {
			throw new IllegalArgumentException("list is empty");
		}
		return elements.get(0);
	}

	public <T> T getLast(List<T> elements) {
		if (elements.isEmpty()) {
			throw new IllegalArgumentException("list is empty");
		}
		return elements.get(elements.size() - 1);
	}

	public <T> T getLastBut1(List<T> elements) {
		if (elements.size() < 2) {
			throw new IllegalArgumentException("list must have at least 2 elements, but has " + elements.size());
		}
		return elements.get(elements.size() - 2);
	}
}

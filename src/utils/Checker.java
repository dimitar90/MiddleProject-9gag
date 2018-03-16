package utils;

import java.util.List;

public class Checker {
	public static boolean isValidDateLenght(List<String> data, int expectedLength) {
		return data.size() == expectedLength;
	}
}

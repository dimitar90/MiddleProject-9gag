package utils;

import java.util.List;

public class Checker {
	public static final int MIN_USERNAME_LENGTH = 5;
	public static final int MAX_USERNAME_LENGTH = 30;
	public static final int MIN_PASSWORD_LENGTH = 6;
	public static final int MAX_PASSWORD_LENGTH = 20;
	public static final String EMAIL_MANDATORY_CHAR = "@";
	public static final int EMAIL_MAX_LENGTH = 50;
	public static final int EMAIL_MIN_LENGTH = 6;

	public static boolean isValidDateLenght(List<String> data, int expectedLength) {
		return data.size() == expectedLength;
	}

	public static boolean isValidUsername(String username) {
		return username != null
				&& (username.trim().length() >= MIN_USERNAME_LENGTH && username.trim().length() <= MAX_USERNAME_LENGTH);
	}

	public static boolean isValidPassword(String password) {
		return password != null
				&& (password.trim().length() >= MIN_PASSWORD_LENGTH && password.trim().length() <= MAX_PASSWORD_LENGTH);
	}

	public static boolean isValidEmail(String email) {
		return email != null && 
				!email.startsWith(EMAIL_MANDATORY_CHAR) && 
				!email.endsWith(EMAIL_MANDATORY_CHAR) && 
				email.contains(EMAIL_MANDATORY_CHAR) && 
				email.trim().length() >= EMAIL_MIN_LENGTH &&
				email.trim().length() <= EMAIL_MAX_LENGTH;
	}
}

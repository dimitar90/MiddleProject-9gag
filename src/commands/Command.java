package commands;

import java.util.List;

import utils.Checker;

public abstract class Command implements Executable {
	public static final String INVALID_DATA = "Invalid data lenght";
	public static final String INVALID_USERNAME = String.format("Username must be between %d and %d symbols!",
			Checker.MIN_USERNAME_LENGTH, Checker.MAX_USERNAME_LENGTH);
	public static final String INVALID_PASSWORD = String.format("Password must be between %d and %d symbols!",
			Checker.MIN_PASSWORD_LENGTH, Checker.MAX_PASSWORD_LENGTH);;

	private List<String> data;

	public Command(List<String> data) {
		this.data = data;
	}

	protected List<String> getData() {
		return this.data;
	}

	public abstract String execute() throws Exception;
}

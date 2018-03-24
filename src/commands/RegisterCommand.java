package commands;

import java.sql.SQLException;
import java.util.List;

import exceptions.UserException;
import models.User;
import repositories.UserRepository;
import utils.Checker;
import utils.Session;

public class RegisterCommand extends Command {
	private static final int ARGUMENTS_LENGTH = 3;
	private static final String SUCCESS_MESSAGE = "You are registered successfully";
	private static final String FAILED_LOGIN_MESSAGE = "Logout first!";
	private static final String INVALID_EMAIL = String.format(
			"The email must be between %d and %d symbols and should contains \"%s\"", 
			Checker.EMAIL_MIN_LENGTH,
			Checker.EMAIL_MAX_LENGTH, 
			Checker.EMAIL_MANDATORY_CHAR);

	// register | username password email
	public RegisterCommand(List<String> data) {
		super(data);
	}

	@Override
	public String execute() throws UserException, SQLException {
		if (!Checker.isValidDateLenght(this.getData(), ARGUMENTS_LENGTH)) {
			throw new UserException(Command.INVALID_DATA);
		}

		if (Session.getInstance().getUser() != null) {
			throw new UserException(FAILED_LOGIN_MESSAGE);
		}

		String username = this.getData().get(0);
		if (!Checker.isValidUsername(username)) {
			throw new UserException(Command.INVALID_USERNAME);
		}

		String password = this.getData().get(1);
		if (!Checker.isValidPassword(password)) {
			throw new UserException(Command.INVALID_PASSWORD);
		}

		String email = this.getData().get(2);
		if (!Checker.isValidPassword(email)) {
			throw new UserException(INVALID_EMAIL);
		}

		User user = UserRepository.getInstance().addUser(username, password, email);

		return SUCCESS_MESSAGE;
	}

}

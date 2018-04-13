package commands;

import java.sql.SQLException;
import java.util.List;

import exceptions.UserException;
import models.User;
import repositories.UserDao;
import utils.Checker;
import utils.Session;

public class LoginCommand extends Command {
	private static final int ARGUMENTS_LENGTH = 2;
	private static final String SUCCES_LOGIN_MESSAGE = "You are logged successfully";
	private static final String FAILED_LOGIN_MESSAGE = "Logout first!";
	public static final String INVALID_USER_ARGUMENTS = "Invalid username or password!";

	//login | username password
	public LoginCommand(List<String> data) {
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
		
		User user = UserDao.getInstance().login(username, password);
		
		return (user != null) ? SUCCES_LOGIN_MESSAGE : INVALID_USER_ARGUMENTS;
	}
}

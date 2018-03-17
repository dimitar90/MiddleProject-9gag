package commands;

import java.util.List;

import exceptions.UserException;
import models.User;
import repositories.UserRepository;
import utils.Checker;
import utils.Session;

public class LoginCommand extends Command {
	private static final int ARGUMENTS_LENGTH = 2;
	private static final String SUCCES_LOGIN_MESSAGE = "You are logged successfully";
	private static final String FAILED_LOGIN_MESSAGE = "Logout first!";//“ук не тр ли да е login
	private static final String INVALID_USER_ARGUMENTS = "Invalid username or password!";
	
	public LoginCommand(List<String> data) {
		super(data);
	}

	@Override
	public String execute() throws UserException {
		if (!Checker.isValidDateLenght(this.getData(), ARGUMENTS_LENGTH)) {
			throw new UserException(Command.INVALID_DATA);
		}
		
		if (Session.getInstance().getUser() != null) {
			throw new UserException(FAILED_LOGIN_MESSAGE);
		} 
		
		String username = this.getData().get(0);
		String password = this.getData().get(1);
		
		User user = UserRepository.getInstance().login(username, password);
		
		return (user != null) ? SUCCES_LOGIN_MESSAGE : INVALID_USER_ARGUMENTS;
	}
}

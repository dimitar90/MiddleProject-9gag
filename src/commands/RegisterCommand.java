package commands;

import java.util.List;

import exceptions.UserException;
import models.User;
import repositories.UserRepository;
import utils.Checker;
import utils.Session;

public class RegisterCommand extends Command{
	private static final int ARGUMENTS_LENGTH = 3;
	private static final String ALREADY_EXIST = "Username already exist!";
	private static final String PASSWORD_MISMATCH = "Password mismatch!";
	private static final String SUCCESS_MESSAGE = "You are registered successfully";
	private static final String FAILED_LOGIN_MESSAGE = "Logout first!";
	
	public RegisterCommand(List<String> data) {
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
		String confirmPassword = this.getData().get(2);
		
		if (UserRepository.getInstance().isExistByUsername(username)) {
			throw new UserException(ALREADY_EXIST);
		}
		
		if (!password.equals(confirmPassword)) {
			throw new UserException(PASSWORD_MISMATCH);
		}
		
		//create with factory
		User user = new User(username, password);
		UserRepository.getInstance().addUser(user);
		
		return SUCCESS_MESSAGE;
	}

}

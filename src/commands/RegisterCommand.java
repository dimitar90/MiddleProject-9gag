package commands;

import java.util.List;

import exceptions.UserException;
import models.User;
import repositories.UserRepository;
import utils.Checker;
import utils.Session;

public class RegisterCommand extends Command{
	private static final int ARGUMENTS_LENGTH = 3;
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
		//“ук не е ли по добре да е void метода ?
		User user = UserRepository.getInstance().addUser(username, password, confirmPassword);
		
		return SUCCESS_MESSAGE;
	}

}

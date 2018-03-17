package commands;

import java.util.List;

import exceptions.UserException;
import utils.Checker;
import utils.Session;

public class LogoutCommand extends Command{
	private static final int ARGUMENTS_LENGTH = 0;
	private static final String SUCCES_LOGIN_MESSAGE = "You are logout successfully";
	private static final String FAILED_LOGIN_MESSAGE = "Login first!";
	
	public LogoutCommand(List<String> data) {
		super(data);
	}

	@Override
	public String execute() throws UserException {
		if (!Checker.isValidDateLenght(this.getData(), ARGUMENTS_LENGTH)) {
			throw new UserException(Command.INVALID_DATA);
		}
		
		if (Session.getInstance().getUser() == null) {
			throw new UserException(FAILED_LOGIN_MESSAGE);
		}
		
		Session.getInstance().setUser(null);
		
		return SUCCES_LOGIN_MESSAGE;
	}

}

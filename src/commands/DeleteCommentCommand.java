package commands;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import exceptions.CommentException;
import exceptions.UserException;
import repositories.CommentRepository;
import utils.Checker;
import utils.Session;

public class DeleteCommentCommand extends Command {
	private static final int ARGUMENTH_LENGTH = 1;
	private static final String FAILED_LOGIN_MESSAGE = "Login first!";
	private static final String SUCCESSFULLY_MESSAGE = "Comment delete successfully.";

	// example
	// delete comment | commentId
	public DeleteCommentCommand(List<String> data) {
		super(data);
	}
	
	@Override
	public String execute() throws Exception {
		if (!Checker.isValidDateLenght(this.getData(), ARGUMENTH_LENGTH)) {
			throw new CommentException(Command.INVALID_DATA);
		}
		
		if (Session.getInstance().getUser() == null) {
			throw new UserException(FAILED_LOGIN_MESSAGE);
		}
		
		int commentId = Integer.parseInt(this.getData().get(0));
		CommentRepository.getInstance().delete(commentId);
		
		return SUCCESSFULLY_MESSAGE;
	}

}

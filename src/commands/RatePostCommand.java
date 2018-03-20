package commands;

import java.io.IOException;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import exceptions.CommentException;
import exceptions.PostException;
import exceptions.SerializeException;
import exceptions.UserException;
import repositories.PostRepository;
import utils.Checker;
import utils.Session;

public class RatePostCommand extends Command {
	private static final int ARGUMENTS_LENGTH = 2;
	private static final String MEESAGE_NO_USER = "If u want to rated a post u have to be logged";
	private static final String SUCCESSFULLY_MESSAGE = "You have rated the post successfully.";
	
	// rate post | postId postGrade(rate must be -1 or 1)
	public RatePostCommand(List<String> data) {
		super(data);
	}

	@Override
	public String execute()
			throws UserException, IOException, CommentException, SerialException, PostException, SerializeException {
		if (Session.getInstance().getUser() == null) {
			throw new PostException(MEESAGE_NO_USER);
		}

		if (!Checker.isValidDateLenght(this.getData(), ARGUMENTS_LENGTH)) {
			throw new PostException(Command.INVALID_DATA);
		}
		
		int postId = Integer.parseInt(this.getData().get(0));
		int grade = Integer.parseInt(this.getData().get(1));
		
		PostRepository.getInstance().addGradeToPost(postId, grade);
		
		return SUCCESSFULLY_MESSAGE;
	}

}

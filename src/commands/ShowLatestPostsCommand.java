package commands;

import java.io.IOException;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import exceptions.CommentException;
import exceptions.PostException;
import exceptions.UserException;
import repositories.PostDao;
import utils.Checker;

public class ShowLatestPostsCommand extends Command{
	private static final String SUCCESFULLY_MESSAGE = "Posts shows successfully!";
	private static final int ARGUMENTS_LENGTH = 0;

	//command example
	//show latest posts
	public ShowLatestPostsCommand(List<String> data) {
		super(data);
	}

	@Override
	public String execute()
			throws UserException, IOException, CommentException, SerialException, PostException {
		if (!Checker.isValidDateLenght(this.getData(), ARGUMENTS_LENGTH)) {
			throw new PostException(Command.INVALID_DATA);
		}
		
		PostDao.getInstance().listAllPostsSortedByDate(false);
		
		return SUCCESFULLY_MESSAGE;
	}
}

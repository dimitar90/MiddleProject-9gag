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

public class ShowTagPostsCommand extends Command {
	private static final String SUCCESFULLY_MESSAGE = "Posts shows successfully!";
	private static final int ARGUMENTS_LENGTH = 1;

	// command example
	// show tag posts | tagName
	public ShowTagPostsCommand(List<String> data) {
		super(data);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String execute()
			throws UserException, IOException, CommentException, SerialException, PostException, SerializeException {
		// тука няма нужда от проверка за логнат юзър понеже и гостите могат да
		// разглеждат
		if (!Checker.isValidDateLenght(this.getData(), ARGUMENTS_LENGTH)) {
			throw new PostException(Command.INVALID_DATA);
		}

		String tagName = this.getData().get(0);
		
		PostRepository.getInstance().listPostsByTagName(tagName);

		return SUCCESFULLY_MESSAGE;
	}

}

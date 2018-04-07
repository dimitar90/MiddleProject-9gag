package commands;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import exceptions.CommentException;
import exceptions.PostException;
import exceptions.SerializeException;
import exceptions.UserException;
import repositories.CommentRepository;
import repositories.PostRepository;
import utils.Checker;
import utils.Session;

public class EditCommentCommand extends Command {

	private static final int ARGUMENTS_LENGTH = 2;
	private static final String FAILED_POST_MESSAGE = "If you want to edit the post, please login first";

	// edit comment post | id of comment, new content;
	public EditCommentCommand(List<String> data) {
		super(data);
	}

	@Override
	public String execute() throws Exception {

		if (!Checker.isValidDateLenght(this.getData(), ARGUMENTS_LENGTH)) {
			throw new PostException(Command.INVALID_DATA);
		}

		if (Session.getInstance().getUser() == null) {
			throw new UserException(FAILED_POST_MESSAGE);
		}

		int commentId = Integer.parseInt(this.getData().get(0));
		String content = this.getData().get(1);

		String result = CommentRepository.getInstance().editComment(commentId, content);

		return result;
	}
}

package commands;

import java.io.IOException;
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

public class EditCommentCommand extends Command{

	private static final int ARGUMENTS_LENGTH = 3;
	private static final String FAILED_POST_MESSAGE = "If you want to edit the post, please login first";
	private static final String SUCCESSFULLY_CREATE_COMMENT_MESSAGE = "Successfully edited post";
//edit comment post | нов текст;id на поста;id на коментара
	public EditCommentCommand(List<String> data) {
		super(data);
	}

	@Override
	public String execute()
			throws UserException, IOException, CommentException, SerialException, PostException, SerializeException {
		
		if (!Checker.isValidDateLenght(this.getData(), ARGUMENTS_LENGTH)) {
			throw new PostException(Command.INVALID_DATA);
		}
		
		if (Session.getInstance().getUser() == null) {
			throw new UserException(FAILED_POST_MESSAGE);
		}
		
		String content = this.getData().get(0);
		int postId = Integer.parseInt(this.getData().get(1));
		int commentId = Integer.parseInt(this.getData().get(2));
		
		//Подаваме  промяната и postID(поста по който искаме да променяме) и commentID (коментара който ще променяме) на репото
		CommentRepository.getInstance().editCommentOfCurrentPost(postId,commentId,content);
		return SUCCESSFULLY_CREATE_COMMENT_MESSAGE;
	}
}

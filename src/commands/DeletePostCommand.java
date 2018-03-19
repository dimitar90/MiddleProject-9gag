package commands;

import java.io.IOException;
import java.util.List;

import exceptions.PostException;
import exceptions.UserException;
import models.Comment;
import repositories.CommentRepository;
import repositories.PostRepository;
import utils.Checker;
import utils.Session;

public class DeletePostCommand extends Command{

	private static final int ARGUMENT_LENGTH = 1;
	private static final String FAILED_LOGIN_MESSAGE = "Login first";
	private static final String SUCCESSFULLY_MESSAGE = "Delete post successfully";

	public DeletePostCommand(List<String> data) {
		super(data);
	}

	@Override
	public String execute() throws UserException, IOException, PostException {
		if (!Checker.isValidDateLenght(this.getData(), ARGUMENT_LENGTH)) {
			throw new PostException(Command.INVALID_DATA);
		}
		
		if (Session.getInstance().getUser() == null) {
			throw new UserException(FAILED_LOGIN_MESSAGE);
		}
				
		// извличаме ID-то на текущият пост
		int postId = Integer.parseInt(this.getData().get(0));
		//трием всички коментари в този пост 
		CommentRepository.getInstance().deleteAllCommentsCurrentPostById(postId);
		
		//трием поста
		PostRepository.getInstance().delete(postId);
		
		return SUCCESSFULLY_MESSAGE;
	}
}

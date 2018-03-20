package commands;

import java.io.IOException;
import java.util.List;

import exceptions.CommentException;
import exceptions.UserException;
import models.Comment;
import repositories.CommentRepository;
import utils.Checker;
import utils.Session;

public class AddCommentCommand extends Command{
	private static final int ARGUMENTS_LENGTH = 2;
	private static final String FAILED_LOGIN_MESSAGE = "Login first!";
	private static final String SUCCESSFULLY_CREATE_COMMENT_MESSAGE = "You create comment successfully";
	private static final String MESSAGE_ABOUT_COMMENT = "Say something nice";
	//example
	//add comment | commentContent postId 
	
	public AddCommentCommand(List<String> data) {
		super(data);
	}

	@Override
	public String execute() throws UserException, IOException, CommentException {
		if (!Checker.isValidDateLenght(this.getData(), ARGUMENTS_LENGTH)) {
			throw new CommentException(Command.INVALID_DATA);
		}
		
		if (Session.getInstance().getUser() == null) {
			throw new UserException(FAILED_LOGIN_MESSAGE);
		} 
		
		System.out.println(MESSAGE_ABOUT_COMMENT);
		String content = this.getData().get(0);
		int postId = Integer.parseInt(this.getData().get(1));
		
		Comment comment = CommentRepository.getInstance().add(content, postId);
		
		return SUCCESSFULLY_CREATE_COMMENT_MESSAGE;
	}

}

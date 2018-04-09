package commands;

import java.io.IOException;
import java.util.List;

import exceptions.PostException;
import exceptions.UserException;
import repositories.PostRepository;
import utils.Checker;
import utils.Session;

public class DeletePostCommand extends Command{

	private static final int ARGUMENT_LENGTH = 1;
	private static final String FAILED_LOGIN_MESSAGE = "Login first";

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
				
		int postId = Integer.parseInt(this.getData().get(0));
		
		String result = PostRepository.getInstance().deletePost(postId);
		
		return result;
	}
}

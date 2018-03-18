package commands;

import java.io.IOException;
import java.util.List;

import exceptions.PostException;
import exceptions.UserException;
import models.Post;
import repositories.PostRepository;
import utils.Checker;
import utils.Session;

public class CreatePostCommand extends Command{
	private static final String SUCCESS_MESSAGE = "Create post successfully";
	private static final int ARGUMENTS_LENGTH = 4;
	private static final String MEESAGE_NO_USER = "If u want to create a post u have to be logged";
	public CreatePostCommand(List<String> data) {
		super(data);
	}

	@Override
	public String execute() throws IOException, PostException, UserException {
		if (Session.getInstance().getUser() == null) {
			throw new PostException(MEESAGE_NO_USER);
		}
		
		if (!Checker.isValidDateLenght(this.getData(), ARGUMENTS_LENGTH)) {
			throw new PostException(Command.INVALID_DATA);
		}
		
		String postName = this.getData().get(0);
		String description = this.getData().get(1);
		String url = this.getData().get(2);
		String tagName = this.getData().get(3);
		Post post = PostRepository.getInstance().addPost(postName, description, url,tagName);
		
		return SUCCESS_MESSAGE;
	}

}

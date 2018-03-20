package commands;

import java.util.List;

import exceptions.PostException;
import repositories.PostRepository;
import utils.Checker;

public class ShowSectionPostsCommand extends Command {
	private static final String SUCCESFULLY_MESSAGE = "Posts shows successfully!";
	private static final int ARGUMENTS_LENGTH = 1;
	
	//exmaple command
	//show section posts | nameOfSection
	public ShowSectionPostsCommand(List<String> data) {
		super(data);
	}

	@Override
	public String execute() throws Exception {
		if (!Checker.isValidDateLenght(this.getData(), ARGUMENTS_LENGTH)) {
			throw new PostException(Command.INVALID_DATA);
		}

		String sectionName = this.getData().get(0);
		
		PostRepository.getInstance().listAllPostsBySectionName(sectionName);

		return SUCCESFULLY_MESSAGE;
	}
}

package commands;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import exceptions.PostException;
import exceptions.SectionException;
import exceptions.UserException;
import models.Post;
import repositories.PostRepository;
import utils.Session;

public class CreatePostCommand extends Command{
	private static final String SUCCESS_MESSAGE = "Create post successfully";
	//private static final int ARGUMENTS_LENGTH = 4;
	private static final String MEESAGE_NO_USER = "If u want to create a post u have to be logged";
	private static final String MSG_TO_FOR_FIRST_COMMENT = "Be the first to comment!";
	public CreatePostCommand(List<String> data) {
		super(data);
	}

	//example command
	//create post | postDescription postUrlImage postSectionName oneOrManytagNames(with space separator)
	@Override
	public String execute() throws Exception {
		if (Session.getInstance().getUser() == null) {
			throw new PostException(MEESAGE_NO_USER);
		}
		
		//тука това за сега го закоментирам понеже не се знае колко тага ще се подадат и няма как да знаем дължината
		//на подадените аргументи
//		if (!Checker.isValidDateLenght(this.getData(), ARGUMENTS_LENGTH)) {
//			throw new PostException(Command.INVALID_DATA);
//		}
		
		String description = this.getData().get(0);
		String url = this.getData().get(1);
		String section = this.getData().get(2);
		List<String> tagNames = this.getData().stream().skip(3).collect(Collectors.toList());
		
		//тука вече се приема колекция от тагове, а не само 1 таг
		//реално като се направи html ще е същото ще има 1 text, което ще е със key/атрибут name Tag: 
		//и в него ще могат да се изреждат всички тагове с запетайка и после сплитваме и ги слагаме на поста
		Post post = PostRepository.getInstance().addPost(description, url, section ,tagNames);
		
		if (!post.anyComments()) {
			System.out.println(MSG_TO_FOR_FIRST_COMMENT);
		}
		return SUCCESS_MESSAGE;
	}

}

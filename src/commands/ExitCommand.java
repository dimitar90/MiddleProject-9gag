package commands;

import java.io.IOException;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import exceptions.SerializeException;
import exceptions.UserException;
import models.Section;
import repositories.CommentRepository;
import repositories.PostRepository;
import repositories.SectionRepository;
import repositories.TagRepository;
import repositories.UserRepository;

public class ExitCommand extends Command {

	public ExitCommand(List<String> data) {
		super(data);
	}

	@Override
	public String execute() throws UserException, IOException, SerialException, SerializeException {
		System.exit(0);
		return "Exit successfully.";
	}
}

package commands;

import java.io.IOException;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import exceptions.UserException;
import repositories.CommentRepository;
import repositories.UserRepository;

public class ExitCommand extends Command {

	public ExitCommand(List<String> data) {
		super(data);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String execute() throws UserException, IOException, SerialException {
		UserRepository.getInstance().serialize();
		CommentRepository.getInstance().serialize();
		
		System.exit(0);
		return "Exit successfully.";
	}
}

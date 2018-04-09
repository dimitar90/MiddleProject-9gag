package commands;

import java.io.IOException;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import exceptions.UserException;

public class ExitCommand extends Command {

	public ExitCommand(List<String> data) {
		super(data);
	}

	@Override
	public String execute() throws UserException, IOException, SerialException  {
		System.exit(0);
		return "Exit successfully.";
	}
}

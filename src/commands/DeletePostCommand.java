package commands;

import java.io.IOException;
import java.util.List;

import exceptions.UserException;

public class DeletePostCommand extends Command{

	public DeletePostCommand(List<String> data) {
		super(data);
	}

	@Override
	public String execute() throws UserException, IOException {
		String result = "delete post!";
		return result;
	}
}

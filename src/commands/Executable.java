package commands;

import java.io.IOException;

import exceptions.UserException;

public interface Executable {
	String execute() throws UserException, IOException;
}

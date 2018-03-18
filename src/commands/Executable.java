package commands;

import java.io.IOException;

import javax.sql.rowset.serial.SerialException;

import exceptions.CommentException;
import exceptions.PostException;
import exceptions.SerializeException;
import exceptions.UserException;

public interface Executable {
	String execute() throws UserException, IOException, CommentException, SerialException, PostException, SerializeException;
}

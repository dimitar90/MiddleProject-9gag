package core;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.sql.rowset.serial.SerialException;

import exceptions.CommentException;
import exceptions.InvalidCommandException;
import exceptions.PostException;
import exceptions.UserException;

public interface IInterpreter {
	 String interpretCommand(String command) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, UserException, InvalidCommandException, IOException, CommentException, SerialException, PostException;
}

package commands;

import java.io.IOException;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import exceptions.CommentException;
import exceptions.PostException;
import exceptions.SerializeException;
import exceptions.UserException;

public abstract class Command implements Executable{
	public static final String INVALID_DATA = "Invalid data";
	private List<String> data;

    public Command(List<String> data)
    {
        this.data = data;
    }

    protected List<String> getData() {
    	return this.data;
    }
   
    public abstract String execute() throws UserException, IOException, CommentException, SerialException, PostException, SerializeException;
}

package core;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import commands.Executable;
import exceptions.InvalidCommandException;
import exceptions.UserException;

public class CommandInterpreter implements IInterpreter {
	private static final String PREFFIX = "commands.";
	private static final String POSTFIX = "Command";
	private static final String INVALID_COMMAND_MESSAGE = "Invalid command name!";
	
	@Override
	public String interpretCommand(String input) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, UserException, InvalidCommandException, IOException {
		List<String> data = Arrays.asList(input.split(" "));
		Executable command = this.parseCommand(data);
		String result = command.execute();
		return result;
	}

	private Executable parseCommand(List<String> data) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, InvalidCommandException {
		String className = data.get(0);
		className = PREFFIX + className + POSTFIX;
		//className = PREFFIX + className.substring(0, 1).toUpperCase() + className.substring(1).toLowerCase() + POSTFIX;
		data = data.stream().skip(1).collect(Collectors.toList());
		
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (Exception e) {
			throw new InvalidCommandException(INVALID_COMMAND_MESSAGE);
		} catch (NoClassDefFoundError e) {
			throw new InvalidCommandException(INVALID_COMMAND_MESSAGE);
		}
		
		Constructor<?> ctor = clazz.getConstructor(List.class);
		Executable instance = (Executable)ctor.newInstance(data);
		return instance;
	}
}

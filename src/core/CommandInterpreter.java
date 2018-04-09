package core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import commands.Executable;
import exceptions.InvalidCommandException;

public class CommandInterpreter implements IInterpreter {
	private static final String PREFFIX = "commands.";
	private static final String POSTFIX = "Command";
	private static final String INVALID_COMMAND_MESSAGE = "Invalid command name!";

	@Override
	public String interpretCommand(String input) throws Exception {
		
		List<String> inputParts = Arrays.asList(input.split("\\|"));
		List<String> commandParts = Arrays.asList(inputParts.get(0).trim().split(" "));
		List<String> data = new ArrayList<String>();
		if (inputParts.size() > 1) {
			data = Arrays.asList(inputParts.get(1).trim().split(" "));
		}
		
		Executable command = this.parseCommand(data, commandParts);
		String result = command.execute();
		return result;
	}

	private Executable parseCommand(List<String> data, List<String> commandParts)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, ClassNotFoundException, InvalidCommandException {
		
		String className = PREFFIX;
		for (String part : commandParts) {
			className += part.trim().substring(0, 1).toUpperCase() + part.trim().substring(1).toLowerCase();
		}
		className += POSTFIX;

		Class<?> clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (Exception e) {
			throw new InvalidCommandException(INVALID_COMMAND_MESSAGE);
		} catch (NoClassDefFoundError e) {
			throw new InvalidCommandException(INVALID_COMMAND_MESSAGE);
		}

		Constructor<?> ctor = clazz.getConstructor(List.class);
		Executable instance = (Executable) ctor.newInstance(data);
		return instance;
	}
}

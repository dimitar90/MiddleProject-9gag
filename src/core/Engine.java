package core;

import java.util.Scanner;


public class Engine implements Runnable {
	
	private IInterpreter commandInterpreter;
	private Scanner scanner;

	public Engine() {
		this.commandInterpreter = new CommandInterpreter();
		this.scanner = new Scanner(System.in);
	}

	@Override
	public void run() {
		while (true) {
			try {
				String input = scanner.nextLine();
				String result = commandInterpreter.interpretCommand(input);
				System.out.println(result);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

	}
}

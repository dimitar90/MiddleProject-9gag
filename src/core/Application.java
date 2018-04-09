package core;

import java.sql.SQLException;

import exceptions.SectionException;
import utils.DatabaseLoader;
import utils.Downloader;

public class Application {
	private static final String MESSAGE_ITERRUPT_EXCEPTION = "Thread has been interrupted";

	private static Downloader downloader = new Downloader();

	public static void main(String[] args) throws SQLException {
		try {
			DatabaseLoader.loadDatabase();
		} catch (SectionException e) {
			System.out.println("neshto ne e zaredeno");
			e.printStackTrace();
		}
		
		downloader.setDaemon(true);
		downloader.start();

		Runnable engine = new Engine();
		engine.run();
	}
}

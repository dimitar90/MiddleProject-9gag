package core;

import java.sql.SQLException;

import exceptions.CommentException;
import exceptions.PostException;
import exceptions.SectionException;
import exceptions.UserException;
import utils.DatabaseLoader;
import utils.Downloader;

public class Application {
	private static final String MESSAGE_ITERRUPT_EXCEPTION = "Thread has been interrupted";

	private static Downloader downloader = new Downloader();

	public static void main(String[] args) throws SQLException {
		try {
			DatabaseLoader.loadDatabase();
		} catch (SectionException | CommentException | PostException | UserException e) {
			System.out.println("neshto ne e zaredeno");
			e.printStackTrace();
		}
		
		downloader.setDaemon(true);
		downloader.start();

		Runnable engine = new Engine();
		engine.run();
	}
}

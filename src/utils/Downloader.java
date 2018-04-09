package utils;

import exceptions.PostException;
import repositories.PostRepository;

public class Downloader extends Thread {

	private static final int TIME_TO_UPDATE = 10_000;

	@Override
	public void run() {
		while (true) {

			try {
				Thread.sleep(TIME_TO_UPDATE);
			} catch (InterruptedException e) {
				System.out.println("Program has stopped");
				return;
			}
			try {
				update();
			} catch (PostException e) {
				e.printStackTrace();
			}
		}
	}

	private  void update() throws PostException {
		PostRepository.getInstance().getProcess();
	}

}

package utils;

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
			update();
		}
	}

	private  void update() {
		PostRepository.getInstance().getProcess();
	}

}

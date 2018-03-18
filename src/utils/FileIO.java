package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;


public class FileIO {
	
	public String read(String fileName) throws FileNotFoundException {
		File file = new File(fileName);
		StringBuilder sb = new StringBuilder();

		try (Scanner sc = new Scanner(file)) {
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				sb.append(line);
			}
		}
		
		return sb.toString();
	}
	
	public void write(String content, String fileName) throws IOException {
		File file = new File(fileName);

		try (PrintStream ps = new PrintStream(file)) {
			file.createNewFile();
			ps.println(content);
		}
	}
}

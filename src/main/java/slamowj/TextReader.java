package slamowj;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class TextReader {

	private TextReader() {
	}

	public static String readFromFile(String fileName) throws IOException {
		if (fileName == null) {
			throw new IllegalArgumentException("Filename is null!");
		}
		String text = FileUtils.readFileToString(new File(fileName));

		return text;
	}

}

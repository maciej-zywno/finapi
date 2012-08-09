package pl.finapi.paypal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class CharsetTest1 {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		// String csvFilePath1 = "C:/Documents and Settings/maciek/Desktop/Pobierz-2012.01.csv";
		// String csvFilePath2 = "src/test/resources/paypal/wrzesien-marzec2012-PL.csv";
		String csvFilePath3 = "src/test/resources/paypal/Pobierz-2012.03.16.csv";
		String charsetName1 = "windows-1250";
//		String charsetName2 = "UTF-8";
//		String charsetName3 = "windows-1252";
		for (Charset charset : Charset.availableCharsets().values()) {
			String headerLine = readLines(csvFilePath3, charset.name()).get(0);
			if (headerLine.contains("Imię")) {
				//System.out.println(headerLine);
				System.out.println(charset);
			}
		}
		// print(csvFilePath1, charsetName1);
		// print(csvFilePath2, charsetName2);
		print(csvFilePath3, charsetName1);
//		print(csvFilePath3, charsetName2);
//		print(csvFilePath3, charsetName3);
	}

	private static void print(String csvFilePath, String charsetName) throws FileNotFoundException, IOException {
		List<String> lines = readLines(csvFilePath, charsetName);
		System.out.println(lines);
	}

	private static List<String> readLines(String csvFilePath, String charsetName) throws FileNotFoundException, IOException {
		FileInputStream in = new FileInputStream(csvFilePath);
		Reader reader = new InputStreamReader(in, Charset.forName(charsetName));
		List<String> lines = IOUtils.readLines(reader);
		return lines;
	}
}

package pl.finapi.paypal.source.report;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import com.Ostermiller.util.CSVParse;
import com.Ostermiller.util.CSVParser;

@Component
public class CsvUtil {

	public String[][] toCells(List<String> lines) {
		StringWriter writer = new StringWriter();
		try {
			IOUtils.writeLines(lines, "\n\r", writer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		StringReader reader = new StringReader(writer.toString());
		CSVParse parser = new CSVParser(reader);
		try {
			return parser.getAllValues();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

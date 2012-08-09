package pl.finapi.paypal;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import pl.finapi.paypal.email.EmailFailureMessage;
import pl.finapi.paypal.email.EmailMessage;

@Component
public class FallbackService {

	private final Logger log = Logger.getLogger(this.getClass());

	private final File dirForStoringUnparseableCsvFiles;

	@Autowired
	public FallbackService(@Value("#{dirForStoringUnparseableCsvFiles}") File dirForStoringUnparseableCsvFiles) {
		this.dirForStoringUnparseableCsvFiles = dirForStoringUnparseableCsvFiles;
	}

	public void store(EmailMessage emailMessage) {
		String fileName = System.currentTimeMillis() + "";

		// 1 csv file
		File csvFile = new File(dirForStoringUnparseableCsvFiles, fileName + ".csv");
		try {
			FileUtils.writeByteArrayToFile(csvFile, emailMessage.getCsvFileAsBytes());
		} catch (IOException e) {
			log.error(e);
		}

		// 2 text file with exception message
		if (emailMessage instanceof EmailFailureMessage) {
			File fileWithcorrespondingExceptionText = new File(dirForStoringUnparseableCsvFiles, fileName + "-exception.txt");
			try {
				FileUtils.writeByteArrayToFile(fileWithcorrespondingExceptionText, emailMessage.getCsvFileAsBytes());
			} catch (IOException e) {
				log.error(e);
			}
		}
	}
}

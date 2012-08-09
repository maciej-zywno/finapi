package pl.finapi.paypal.email;

import java.io.PrintWriter;
import java.io.StringWriter;

public class EmailFailureMessage implements EmailMessage {

	private final byte[] csvFileAsBytes;
	private final RuntimeException exception;
	private final String originalFilename;

	public EmailFailureMessage(byte[] csvFileAsBytes, String originalFilename, RuntimeException exception) {
		this.csvFileAsBytes = csvFileAsBytes;
		this.originalFilename = originalFilename;
		this.exception = exception;
	}

	public byte[] getCsvFileAsBytes() {
		return csvFileAsBytes;
	}

	public RuntimeException getException() {
		return exception;
	}

	public String getOriginalFilename() {
		return originalFilename;
	}

	public String getMessage() {
		if (getException() == null) {
			return "no exception";
		} else {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			getException().printStackTrace(printWriter);
			return stringWriter.toString();
		}
	}

	@Override
	public String getSubject() {
		return "exception in webapi";
	}

}

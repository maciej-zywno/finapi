package pl.finapi.paypal.email;

public class EmailSuccessMessage implements EmailMessage {

	private final byte[] csvFileAsBytes;
	private final String originalFilename;

	public EmailSuccessMessage(byte[] csvFileAsBytes, String originalFilename) {
		this.csvFileAsBytes = csvFileAsBytes;
		this.originalFilename = originalFilename;
	}

	public byte[] getCsvFileAsBytes() {
		return csvFileAsBytes;
	}

	public String getOriginalFilename() {
		return originalFilename;
	}

	@Override
	public String getMessage() {
		return "success";
	}

	@Override
	public String getSubject() {
		return "success";
	}}

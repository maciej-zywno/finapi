package pl.finapi.paypal.email;

public interface EmailMessage {

	String getOriginalFilename();

	byte[] getCsvFileAsBytes();

	String getMessage();

	String getSubject();

}

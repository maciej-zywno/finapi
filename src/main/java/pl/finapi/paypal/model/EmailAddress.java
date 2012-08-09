package pl.finapi.paypal.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class EmailAddress {

	public static final EmailAddress NO_EMAIL_ADDRESS = new EmailAddress("                                               ");
	
	private final String emailAddress;

	public EmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}

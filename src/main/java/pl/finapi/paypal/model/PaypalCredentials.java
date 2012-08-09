package pl.finapi.paypal.model;
public class PaypalCredentials {

	private final String apiPassword;
	private final String apiUsername;
	private final String apiSignature;

	public PaypalCredentials(String apiPassword, String apiUsername, String apiSignature) {
		this.apiPassword = apiPassword;
		this.apiUsername = apiUsername;
		this.apiSignature = apiSignature;
	}

	public String getApiPassword() {
		return apiPassword;
	}

	public String getApiUsername() {
		return apiUsername;
	}

	public String getApiSignature() {
		return apiSignature;
	}
}
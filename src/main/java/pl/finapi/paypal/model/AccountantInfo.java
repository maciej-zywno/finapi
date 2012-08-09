package pl.finapi.paypal.model;

public class AccountantInfo {

	public static final AccountantInfo EMPTY = new AccountantInfo("");

	private final String accountantInfo;

	public AccountantInfo(String accountantInfo) {
		this.accountantInfo = accountantInfo;
	}

	public String getAccountantInfo() {
		return accountantInfo;
	}

}

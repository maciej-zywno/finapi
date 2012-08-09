package pl.finapi.paypal.model.output;

import pl.finapi.paypal.model.AccountantInfo;
import pl.finapi.paypal.model.Amount;
import pl.finapi.paypal.model.City;
import pl.finapi.paypal.model.Currency;
import pl.finapi.paypal.model.Day;
import pl.finapi.paypal.model.EmailAddress;

public class DowodWewnetrznyModel {

	private final Day creationDay;
	private final Amount amount;
	private final City city;
	private final EmailAddress emailAddress;
	private final Currency currency;
	private final AccountantInfo accountantInfo;

	public DowodWewnetrznyModel(Currency currency, Day creationDay, Amount amount, City city, EmailAddress emailAddress, AccountantInfo accountantInfo) {
		this.currency = currency;
		this.creationDay = creationDay;
		this.amount = amount;
		this.city = city;
		this.emailAddress = emailAddress;
		this.accountantInfo = accountantInfo;
	}

	public Currency getCurrency() {
		return currency;
	}

	public Day getCreationDay() {
		return creationDay;
	}

	public Amount getAmount() {
		return amount;
	}

	public City getCity() {
		return city;
	}

	public EmailAddress getEmailAddress() {
		return emailAddress;
	}

	public AccountantInfo getAccountantInfo() {
		return accountantInfo;
	}

}

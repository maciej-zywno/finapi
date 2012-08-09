package pl.finapi.paypal.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class BuyerInfo {

	public static final BuyerInfo EMPTY_BUYER = new BuyerInfo("", "", "", "");

	private final String companyName;
	private final String addressLines1;
	private final String addressLines2;
	private final String nip;

	public BuyerInfo(String companyName, String addressLines1, String addressLines2, String nip) {
		this.companyName = companyName;
		this.addressLines1 = addressLines1;
		this.addressLines2 = addressLines2;
		this.nip = nip;
	}

	public String getCompanyName() {
		return companyName;
	}

	public String getAddressLine1() {
		return addressLines1;
	}

	public String getAddressLine2() {
		return addressLines2;
	}

	public String getNIP() {
		return nip;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}

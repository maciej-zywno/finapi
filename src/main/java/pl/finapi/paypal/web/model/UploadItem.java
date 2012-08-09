package pl.finapi.paypal.web.model;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class UploadItem {

	private CommonsMultipartFile fileData;
	private boolean addCompanyName;
	private String companyName;
	private String city;
	private String zipcode;
	private String address;
	private String nip;
	private String wystawil;
	private String email;

	public boolean isAddCompanyName() {
		return addCompanyName;
	}

	public void setAddCompanyName(boolean addCompanyName) {
		this.addCompanyName = addCompanyName;
	}

	public CommonsMultipartFile getFileData() {
		return fileData;
	}

	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getNip() {
		return nip;
	}

	public void setNip(String nip) {
		this.nip = nip;
	}

	public String getWystawil() {
		return wystawil;
	}

	public void setWystawil(String wystawil) {
		this.wystawil = wystawil;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
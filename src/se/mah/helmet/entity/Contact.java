package se.mah.helmet.entity;

public class Contact {
	private String name;
	private String phoneNbr;
	
	public Contact(String name, String phoneNbr) {
		this.name = name;
		this.phoneNbr = phoneNbr;
	}
	@Override
	public String toString() {
		return "Contact [name=" + name + ", phoneNbr=" + phoneNbr + "]";
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNbr() {
		return phoneNbr;
	}
	public void setPhoneNbr(String phoneNbr) {
		this.phoneNbr = phoneNbr;
	}
}

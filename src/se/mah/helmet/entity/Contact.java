package se.mah.helmet.entity;

import se.mah.helmet.LightJson;

public class Contact implements Jsonable {
	private long id;
	private String name;
	private String phoneNbr;
	
	public Contact(long id, String name, String phoneNbr) {
		this.id = id;
		this.name = name;
		this.phoneNbr = phoneNbr;
	}
	
	public String toJson() {
		LightJson jsonBuilder = LightJson.newInstance();
		jsonBuilder.put("sourceId", String.valueOf(getId()));
		jsonBuilder.put("name", getName());
		jsonBuilder.put("phoneNbr", getPhoneNbr());
		return jsonBuilder.toJson();
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
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
}

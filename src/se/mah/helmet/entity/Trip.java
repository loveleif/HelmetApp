package se.mah.helmet.entity;

import se.mah.helmet.LightJson;

public class Trip implements Jsonable {
	private long id;
	private String name;
	
	public Trip(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toJson() {
		LightJson jsonBuilder = LightJson.newInstance();
		jsonBuilder.put("sourceId", getId().toString());
		jsonBuilder.put("name", getName());
		return jsonBuilder.toJson();
	}
}

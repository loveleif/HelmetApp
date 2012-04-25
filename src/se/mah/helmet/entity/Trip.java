package se.mah.helmet.entity;

import se.mah.helmet.LightJson;

public class Trip implements Jsonable {
	private long sourceId;
	private String name;
	
	public Trip(long id, String name) {
		this.sourceId = id;
		this.name = name;
	}

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long id) {
		this.sourceId = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toJson() {
		LightJson jsonBuilder = LightJson.newInstance();
		jsonBuilder.put("sourceId", getSourceId().toString());
		jsonBuilder.put("name", getName());
		return jsonBuilder.toJson();
	}
}

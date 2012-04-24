package se.mah.helmet.entity;

import se.mah.helmet.LightJson;

/*
 * Class representing an accident alarm.
 */
public class Alarm implements Jsonable {
	private long id;
	private short severity;
	// TODO Include position!
	
	public Alarm(long id, short severity) {
		super();
		this.id = id;
		this.severity = severity;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public short getSeverity() {
		return severity;
	}

	public void setSeverity(short severity) {
		this.severity = severity;
	}

	public String toJson() {
		LightJson jsonBuilder = LightJson.newInstance();
		jsonBuilder.put("sourceId", String.valueOf(getId()));
		jsonBuilder.put("severity", String.valueOf(getSeverity()));
		return jsonBuilder.toJson();
	}
}

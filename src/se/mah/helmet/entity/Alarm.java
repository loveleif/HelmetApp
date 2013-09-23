package se.mah.helmet.entity;

import se.mah.helmet.LightJson;

/**
 * Class representing an accident alarm.
 */
public class Alarm implements Jsonable {
	private long sourceId;
	private short severity;
	// TODO Include position!
	
	public Alarm(long id, short severity) {
		super();
		this.sourceId = id;
		this.severity = severity;
	}

	public long getSourceId() {
		return sourceId;
	}

	public void setSourceId(long id) {
		this.sourceId = id;
	}

	public short getSeverity() {
		return severity;
	}

	public void setSeverity(short severity) {
		this.severity = severity;
	}

	public String toJson() {
		LightJson jsonBuilder = LightJson.newInstance();
		jsonBuilder.put("sourceId", String.valueOf(getSourceId()));
		jsonBuilder.put("severity", String.valueOf(getSeverity()));
		return jsonBuilder.toJson();
	}
}

package se.mah.helmet.entity;

public class Alarm {
	private long id;
	private short severity;
	
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
}

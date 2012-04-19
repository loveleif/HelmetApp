package se.mah.helmet.entity;

import java.util.Date;

public class AccData {
	private long sourceId = -1;
	private Date date;
	private double accX;
	private double accY;
	private double accZ;
	
	public AccData(long sourceId, Date date, double accX, double accY, double accZ) {
		super();
		this.sourceId = sourceId;
		this.date = date;
		this.accX = accX;
		this.accY = accY;
		this.accZ = accZ;
	}
	
	public AccData(long sourceId, long date, double accX, double accY, double accZ) {
		this(sourceId, new Date(date), accX, accY, accZ);
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getAccX() {
		return accX;
	}
	public void setAccX(double accX) {
		this.accX = accX;
	}
	public double getAccY() {
		return accY;
	}
	public void setAccY(double accY) {
		this.accY = accY;
	}
	public double getAccZ() {
		return accZ;
	}
	public void setAccZ(double accZ) {
		this.accZ = accZ;
	}

	public long getSourceId() {
		return sourceId;
	}

	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}
}

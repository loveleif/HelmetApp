package se.mah.helmet.entity;

import java.util.Date;

import se.mah.helmet.LightJson;
import se.mah.helmet.Util;

/**
 * Class representing one accelerometer data sample.
 */
public class AccData implements Jsonable {
	// Id from android sqlite database
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
	
	public String toJson() {
		LightJson jsonBuilder = LightJson.newInstance();
		jsonBuilder.put("sourceId", String.valueOf(getSourceId()));
		jsonBuilder.put("date", Util.getDateFormatISO8601(getDate()));
		jsonBuilder.put("accX", String.valueOf(getAccX()));
		jsonBuilder.put("accY", String.valueOf(getAccY()));
		jsonBuilder.put("accZ", String.valueOf(getAccZ()));
		return jsonBuilder.toJson();
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

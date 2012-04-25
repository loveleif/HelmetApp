package se.mah.helmet.entity;

import se.mah.helmet.LightJson;
import se.mah.helmet.Util;
import android.location.Location;

public class Position extends Location implements Jsonable {
	private long sourceId;

	public Position(String provider) {
		super(provider);
	}

	public String toJson() {
		LightJson jsonBuilder = LightJson.newInstance();
		jsonBuilder.put("sourceId", String.valueOf(getSourceId()));
		jsonBuilder.put("date", String.valueOf(getTime()));
		jsonBuilder.put("longitude", String.valueOf(getLongitude()));
		jsonBuilder.put("latitude", String.valueOf(getLatitude()));
		return jsonBuilder.toJson();
	}

	public long getSourceId() {
		return sourceId;
	}

	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}
}

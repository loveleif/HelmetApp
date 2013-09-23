package se.mah.helmet.storage;

import se.mah.helmet.entity.Position;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;

public class LocDbAdapter extends DbAdapter<Position> {
	private static final String TAG = LocDbAdapter.class.getSimpleName();

	public static final String TABLE_LOC = "loc";

	public static final String KEY_ROWID = "_id";
	public static final String KEY_TIME = "time";
	public static final String KEY_LAT = "latitude";
	public static final String KEY_LONG = "longitude";
	public static final String KEY_ALT = "altitude";
	public static final String KEY_ACCURACY = "accuracy";
	public static final String KEY_SPEED = "speed";
	public static final String KEY_BEARING = "bearing";
	public static final String KEY_PROVIDER = "provider";
	public static final String KEY_TRIP_ID = "trip_id";
	public static final String TABLE_LOC_CREATE = 
			"CREATE TABLE "	+ TABLE_LOC + "("
			+ KEY_ROWID	+ " integer primary key autoincrement, "
			+ KEY_TIME + " integer," 
			+ KEY_LAT + " real," 
			+ KEY_LONG + " real,"
			+ KEY_ALT + " real," 
			+ KEY_ACCURACY + " real," 
			+ KEY_SPEED + " real," 
			+ KEY_BEARING + " real," 
			+ KEY_PROVIDER + " text,"
			+ KEY_TRIP_ID + " integer,"
			+ "foreign key(" + KEY_TRIP_ID + ") references " + TripDbAdapter.TABLE_TRIP + "(" + TripDbAdapter.KEY_ROWID + ")"
			+ ")";

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */
	public LocDbAdapter(Context context) {
		super(context);
	}

	/**
	 * Insert location to database.
	 * 
	 * @param location
	 *            Location to insert
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 *         (see android.database.sqlite.SQLiteDatabase.insert(...))
	 */
	public long insertLocation(long tripId, Location location) {
		// TODO Ta bort
		ContentValues values = new ContentValues(8);
		values.put(KEY_TRIP_ID, tripId);
		values.put(KEY_TIME, location.getTime());
		values.put(KEY_LAT, location.getLatitude());
		values.put(KEY_LONG, location.getLongitude());
		values.put(KEY_ALT, location.hasAltitude());
		values.put(KEY_ACCURACY, location.getAccuracy());
		values.put(KEY_SPEED, location.getSpeed());
		values.put(KEY_BEARING, location.getBearing());
		values.put(KEY_PROVIDER, location.getProvider());

		return getDb().insert(TABLE_LOC, null, values);
	}
	
	@Override
	public String getTableName() {
		return TABLE_LOC;
	}

	@Override
	public String getPrimaryKeyColumnName() {
		return KEY_ROWID;
	}

	@Override
	public Position getObject(Cursor cursor) {
		Position loc = new Position(cursor.getString(cursor.getColumnIndex(KEY_PROVIDER)));
		loc.setAccuracy(cursor.getFloat(cursor.getColumnIndex(KEY_ACCURACY)));
		loc.setAltitude(cursor.getFloat(cursor.getColumnIndex(KEY_ALT)));
		loc.setBearing(cursor.getFloat(cursor.getColumnIndex(KEY_BEARING)));
		loc.setLongitude(cursor.getFloat(cursor.getColumnIndex(KEY_LONG)));
		loc.setLatitude(cursor.getFloat(cursor.getColumnIndex(KEY_LAT)));
		loc.setSpeed(cursor.getFloat(cursor.getColumnIndex(KEY_SPEED)));
		loc.setTime(cursor.getLong(cursor.getColumnIndex(KEY_TIME)));
		loc.setSourceId(cursor.getLong(cursor.getColumnIndex(KEY_ROWID)));
		return loc;
	}

	@Override
	public ContentValues getContentValues(Position loc) {
		ContentValues cv = new ContentValues(7);
		cv.put(KEY_TIME, loc.getTime());
		cv.put(KEY_LAT, loc.getLatitude());
		cv.put(KEY_LONG, loc.getLongitude());
		cv.put(KEY_ALT, loc.getAltitude());
		cv.put(KEY_ACCURACY, loc.getAccuracy());
		cv.put(KEY_SPEED, loc.getSpeed());
		cv.put(KEY_BEARING, loc.getBearing());
		cv.put(KEY_PROVIDER, loc.getProvider());
		cv.put(KEY_ROWID, loc.getSourceId());
		return cv;
	}

	public Cursor getDataForTrip(long tripId) {
		return getDb().query(getTableName(), null,
				KEY_TRIP_ID + "=" + tripId, null, null,
				null, null);
	}
}

package se.mah.helmet.storage;

import java.util.ArrayList;
import java.util.List;

import se.mah.helmet.entity.Trip;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class TripDbAdapter extends DbAdapter {
	private static final String TAG = TripDbAdapter.class.getSimpleName();

	public static final String TABLE_TRIP = "trips";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "name";
	public static final String TABLE_TRIP_CREATE = "CREATE TABLE " + TABLE_TRIP
			+ "(" + KEY_ROWID + " integer primary key autoincrement, "
			+ KEY_NAME + " text not null," + ")";

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */
	public TripDbAdapter(Context context) {
		super(context);
	}

	public long insertTrip(String name) {
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, name);
		return getDb().insert(TABLE_TRIP, null, values);
	}

	/**
	 * Return a Cursor over the list of all trips in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllTripsCursor() {
		return getDb().query(TABLE_TRIP, new String[] { KEY_ROWID, KEY_NAME }, null, null, null, null, null);
	}
	
	public Cursor fetchTripsWhereCursor(String where) {
		return getDb().query(TABLE_TRIP, new String[] { KEY_ROWID, KEY_NAME }, where, null, null, null, null);
	}
	
	public List<Trip> fetchTripsWhere(String where) {
		List<Trip> trips = new ArrayList<Trip>();
		Cursor cursor = fetchTripsWhereCursor(where);
		int colIdxId = cursor.getColumnIndex(KEY_ROWID);
		int colIdxName = cursor.getColumnIndex(KEY_NAME);
		
		while (cursor.moveToNext()) {
			trips.add(new Trip(
					cursor.getLong(colIdxId),
					cursor.getString(colIdxName)));
		}
		return trips;
	}
	
	public Cursor fetchTrip(Long rowId) {
		Cursor cursor = getDb().query(true, TABLE_TRIP, new String[] {
				KEY_ROWID, KEY_NAME }, KEY_ROWID + "=" + rowId,
				null, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		return cursor;
	}
}
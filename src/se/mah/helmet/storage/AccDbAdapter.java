package se.mah.helmet.storage;

import se.mah.helmet.entity.AccData;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Database adapter for storing accelerometer data in a SQLite
 * database.
 */
public class AccDbAdapter extends DbAdapter<AccData> {
	private static final String TAG = AccDbAdapter.class.getSimpleName();

	// Remember to increment versionnumber in DbAdapter when you make changes
	public static final String TABLE_ACC = "acc";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_TIME = "time";
	public static final String KEY_ACCX = "acc_x";
	public static final String KEY_ACCY = "acc_y";
	public static final String KEY_ACCZ = "acc_z";
	public static final String KEY_TRIP_ID = "trip_id";
	public static final String TABLE_ACC_CREATE = 
			"CREATE TABLE " + TABLE_ACC	+ "(" 
			+ KEY_ROWID + " integer primary key autoincrement, "
			+ KEY_TIME + " integer not null," 
			+ KEY_ACCX + " real," 
			+ KEY_ACCY + " real," 
			+ KEY_ACCZ + " real,"
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
	public AccDbAdapter(Context context) {
		super(context);
	}
	
	/**
	 * Insert accelerometer data to database. Time will be current time.
	 * 
	 * @param time
	 * @param accX
	 * @param accY
	 * @param accZ
	 */
	public void insertData(long tripId, double accX, double accY, double accZ) {
		getDb().execSQL(
			"INSERT INTO " + TABLE_ACC +
			" (" + KEY_TRIP_ID + "," + KEY_TIME + "," + KEY_ACCX + "," + KEY_ACCY + "," + KEY_ACCX + ")" +
			" VALUES " +
			" (" + tripId + ",strftime('%s', 'now')," + accX + "," + accY + "," + accZ + ");");
	}

	@Override
	public String getTableName() {
		return TABLE_ACC;
	}

	@Override
	public String getPrimaryKeyColumnName() {
		return KEY_ROWID;
	}

	@Override
	public AccData getObject(Cursor cursor) {
		AccData accData = new AccData(
			cursor.getLong(cursor.getColumnIndex(KEY_ROWID)),
			cursor.getLong(cursor.getColumnIndex(KEY_TIME)),
			cursor.getDouble(cursor.getColumnIndex(KEY_ACCX)),
			cursor.getDouble(cursor.getColumnIndex(KEY_ACCY)),
			cursor.getDouble(cursor.getColumnIndex(KEY_ACCZ)));
		return accData;
	}

	@Override
	public ContentValues getContentValues(AccData accData) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_TIME, accData.getDate().getTime());
		cv.put(KEY_ACCX, accData.getAccX());
		cv.put(KEY_ACCY, accData.getAccY());
		cv.put(KEY_ACCZ, accData.getAccZ());
		return cv;
	}
}

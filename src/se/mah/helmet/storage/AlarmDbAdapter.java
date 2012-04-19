package se.mah.helmet.storage;

import se.mah.helmet.entity.Alarm;
import se.mah.helmet.entity.Trip;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class AlarmDbAdapter extends DbAdapter<Alarm> {
	private static final String TAG = AlarmDbAdapter.class.getSimpleName();

	public static final String TABLE_ALARM = "alarm";

	public static final String KEY_ROWID = "_id";
	public static final String KEY_SEVERITY = "severity";
	public static final String TABLE_ALARM_CREATE = 
			"CREATE TABLE "	+ TABLE_ALARM + "("
			+ KEY_ROWID	+ " integer primary key autoincrement, "
			+ KEY_SEVERITY + " integer," 
			+ ")";

	public AlarmDbAdapter(Context context) {
		super(context);
	}

	@Override
	public String getTableName() {
		return TABLE_ALARM;
	}

	@Override
	public String getPrimaryKeyColumnName() {
		return KEY_ROWID;
	}

	@Override
	public Alarm getObject(Cursor cursor) {
		return new Alarm(
				cursor.getLong(cursor.getColumnIndex(KEY_ROWID)),
				cursor.getShort(cursor.getColumnIndex(KEY_SEVERITY)));
	}

	@Override
	public ContentValues getContentValues(Alarm alarm) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_SEVERITY, alarm.getSeverity());
		return cv;
	}
}

package android.database;

import android.bignerdranch.com.Record;
import android.database.RecordDbSchema.RecordTable;

import java.util.Date;
import java.util.UUID;

public class RecordCursorWrapper extends CursorWrapper {
    public RecordCursorWrapper(Cursor cursor) {
    super(cursor);
}
    public Record getRecord() {
        String uuidString = getString(getColumnIndex(RecordTable.Cols.UUID));
        String title = getString(getColumnIndex(RecordTable.Cols.TITLE));
        long date = getLong(getColumnIndex(RecordTable.Cols.DATE));
        String details = getString(getColumnIndex(RecordTable.Cols.DETAILS));
        String place = getString(getColumnIndex(RecordTable.Cols.PLACE));
        Record record = new Record(UUID.fromString(uuidString));
        record.setTitle(title);
        record.setDate(new Date(date));
        record.setDetails(details);
        record.setPlace(place);

        return record;
    }
}

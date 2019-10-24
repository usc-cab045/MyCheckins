package android.bignerdranch.com;

import android.database.RecordBaseHelper;
import android.database.RecordCursorWrapper;
import android.database.RecordDbSchema;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecordLab {
    private static RecordLab sRecordLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    public static RecordLab get(Context context) {
        if (sRecordLab == null) {
            sRecordLab = new RecordLab(context);
        }
        return sRecordLab;
    }
    private RecordLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new RecordBaseHelper(mContext)
                .getWritableDatabase();



    }
    public void addRecord(Record r) {
        ContentValues values = getContentValues(r);
        mDatabase.insert(RecordDbSchema.RecordTable.NAME, null, values);

    }
    public List<Record> getRecords() {
        List<Record> records = new ArrayList<>();
        RecordCursorWrapper cursor = queryRecords(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                records.add(cursor.getRecord());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return records;

    }
    public Record getRecord(UUID id) {
        RecordCursorWrapper cursor = queryRecords(
                RecordDbSchema.RecordTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getRecord();
        } finally {
            cursor.close();
        }
    }
    public File getPhotoFile(Record record) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, record.getPhotoFilename());
    }
    public void updateRecord(Record record) {
        String uuidString = record.getId().toString();
        ContentValues values = getContentValues(record);
        mDatabase.update(RecordDbSchema.RecordTable.NAME, values,
                RecordDbSchema.RecordTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }
    private RecordCursorWrapper queryRecords(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                RecordDbSchema.RecordTable.NAME,
                null, // columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );
        return new RecordCursorWrapper(cursor);
    }
    private static ContentValues getContentValues(Record record) {
        ContentValues values = new ContentValues();
        values.put(RecordDbSchema.RecordTable.Cols.UUID, record.getId().toString());
        values.put(RecordDbSchema.RecordTable.Cols.TITLE, record.getTitle());
        values.put(RecordDbSchema.RecordTable.Cols.DATE, record.getDate().getTime());
        values.put(RecordDbSchema.RecordTable.Cols.DETAILS, record.getDetails());
        values.put(RecordDbSchema.RecordTable.Cols.PLACE, record.getPlace());
        return values;
    }
    }


package com.tabutech.recordmp4.Others;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import com.tabutech.recordmp4.DatabaseListener.OnDatabaseChangedListener;

import java.util.Comparator;

public class DBHelper extends SQLiteOpenHelper {

    private Context context;

    private static OnDatabaseChangedListener listener;

    public static final String DATABASE_NAME = "recordings_saver";
    public static final int DATABASE_VERSION = 1;

    public DBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        this.context = context;
    }

    public static abstract class DBHelperItem implements BaseColumns {
        public static final String TABLE_NAME = "recordings";

        public static final String COLUMN_NAME_RECORDING_NAME = "recording_name";
        public static final String COLUMN_NAME_RECORDING_FILE_PATH = "file_path";
        public static final String COLUMN_NAME_RECORDING_LENGTH = "length";
        public static final String COLUMN_NAME_TIME_ADDED = "time_added";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBHelperItem.TABLE_NAME + " (" +
                    DBHelperItem._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    DBHelperItem.COLUMN_NAME_RECORDING_NAME + TEXT_TYPE + COMMA_SEP +
                    DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH + TEXT_TYPE + COMMA_SEP +
                    DBHelperItem.COLUMN_NAME_RECORDING_LENGTH + INTEGER_TYPE + COMMA_SEP +
                    DBHelperItem.COLUMN_NAME_TIME_ADDED + INTEGER_TYPE + ")";

    @SuppressWarnings("unused")
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBHelperItem.TABLE_NAME;


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static void setDatabaseListener(OnDatabaseChangedListener listener1){
        listener = listener1;
    }

    public RecordingFile getFileAt(int position){
        SQLiteDatabase database = getReadableDatabase();
        String[] positionItem = {DBHelperItem._ID,
                                    DBHelper.DATABASE_NAME,
                                    DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH,
                                    DBHelperItem.COLUMN_NAME_RECORDING_LENGTH,
                                    DBHelperItem.COLUMN_NAME_TIME_ADDED};

        Cursor c = database.query(DBHelperItem.TABLE_NAME,positionItem,null,null,null,null,null);

        if (c.moveToPosition(position)){
            RecordingFile file = new RecordingFile();
            file.setmId(c.getInt(c.getColumnIndex(DBHelperItem._ID)));
            file.setmName(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_RECORDING_NAME)));
            file.setmFilePath(c.getString(c.getColumnIndex(DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH)));
            file.setmLength(c.getInt(c.getColumnIndex(DBHelperItem.COLUMN_NAME_RECORDING_LENGTH)));
            file.setmTime(c.getLong(c.getColumnIndex(DBHelperItem.COLUMN_NAME_TIME_ADDED)));
            c.close();
            return file;
        }

        return null;
    }

    public  void removeItemWithId(int id){
        SQLiteDatabase db = getWritableDatabase();
        String[] whereArgs = {String.valueOf(id)};
        db.delete(DBHelperItem.TABLE_NAME," _ID=? ",whereArgs);
    }

    public int getCount(){
        SQLiteDatabase db = getReadableDatabase();
        String[] petitions = {DBHelperItem._ID};
        Cursor c = db.query(DBHelperItem.TABLE_NAME,petitions,null,null,null,null,null);
        int count = c.getCount();
        c.close();
        return count;
    }

    public class RecordingComparator implements Comparator<RecordingFile>{

        @Override
        public int compare(RecordingFile o1, RecordingFile o2) {

            Long time1 = o1.getmTime();
            Long time2 = o2.getmTime();
            return time2.compareTo(time1);
        }
    }

    public long addRecordings(String name, String filePath,long length){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DBHelperItem.COLUMN_NAME_RECORDING_NAME,name);
        cv.put(DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH,filePath);
        cv.put(DBHelperItem.COLUMN_NAME_RECORDING_LENGTH,length);
        cv.put(DBHelperItem.COLUMN_NAME_TIME_ADDED,System.currentTimeMillis());
        long itemId = db.insert(DBHelperItem.TABLE_NAME,null,cv);

        if (listener != null){
            listener.onDatabaseEntryAdded();
        }

        return itemId;
    }

    public void renameRecordings(RecordingFile file,String name,String filePath){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBHelperItem.COLUMN_NAME_RECORDING_NAME,name);
        cv.put(DBHelperItem.COLUMN_NAME_RECORDING_FILE_PATH,filePath);

        db.update(DBHelperItem.TABLE_NAME,cv,DBHelperItem.COLUMN_NAME_TIME_ADDED +"="+file.getmId(),null);

        if (listener != null){
            listener.OnDataBaseEntryRenamed();
        }

    }
}

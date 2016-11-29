package ru.oboturov_corp.dogenotes2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DbHelper extends SQLiteOpenHelper {

    private SQLiteDatabase mDb;
    private Cursor mCursor;

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "doge_notes";
    private static final String TABLE_NOTES = "notes";

    static final String TYPE_FOLDER = "1";
    static final String TYPE_NOTE = "0";

    static final String KEY_MAIN_FOLDER = String.valueOf(Integer.MIN_VALUE);
    static final String KEY_ID = "_id";
    static final String KEY_PARENT_FOLDER = "parent_folder";
    static final String KEY_ITEM_TYPE = "item_type";
    static final String KEY_FOLDER_NAME = "folder_name";
    static final String KEY_NOTE_NAME = "note_name";
    static final String KEY_NOTE_TEXT = "note_text";

    static String ARG_OPEN_FOLDER = "open folder";
    static String ARG_OPEN_NOTE = "open note";
    static String ARG_ITEM_TYPE = "item mType";

    DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mDb = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NOTES + "("
                + KEY_ID + " integer primary key autoincrement, "
                + KEY_ITEM_TYPE + " text, "
                + KEY_PARENT_FOLDER + " integer, "
                + KEY_FOLDER_NAME + " text, "
                + KEY_NOTE_NAME + " text,"
                + KEY_NOTE_TEXT + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NOTES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    int getCount(String type, String openFolder) {

        int count;
        mCursor = mDb.query(
                TABLE_NOTES,
                new String[]{KEY_ID},
                KEY_ITEM_TYPE + " = ? and " + KEY_PARENT_FOLDER + " = ?",
                new String[]{type, openFolder},
                null, null, null);
        count = mCursor.getCount();
        mCursor.close();
        return count;
    }

    String getDate(String type, String openFolder, String column, int position) {
        mCursor = mDb.query(
                TABLE_NOTES,
                new String[]{column},
                KEY_ITEM_TYPE + " = ? and " + KEY_PARENT_FOLDER + " = ?",
                new String[]{type, openFolder},
                null, null, null);
        if (mCursor.moveToPosition(position)) {
            String s = mCursor.getString(mCursor.getColumnIndex(column));
            mCursor.close();
            return s;
        }
        return null;
    }

    String getDateForId(String id, String column) {
        mCursor = mDb.query(
                TABLE_NOTES,
                new String[]{column},
                KEY_ID + " = ?",
                new String[]{id},
                null, null, null);
        if (mCursor.moveToFirst()) {
            String s = mCursor.getString(mCursor.getColumnIndex(column));
            mCursor.close();
            return s;
        }
        return null;
    }

    void addItem(String type, String openFolder, String name) {
        ContentValues cv = new ContentValues();
        String nameType = type.equals(TYPE_FOLDER) ? KEY_FOLDER_NAME : KEY_NOTE_NAME;
        cv.put(KEY_ITEM_TYPE, type);
        cv.put(KEY_PARENT_FOLDER, openFolder);
        cv.put(nameType, name);
        mDb.insert(TABLE_NOTES, null, cv);
    }

    void deleteItem(String id) {
        if (getDateForId(id, KEY_ITEM_TYPE).equals(TYPE_FOLDER)) { //если удаляем папку
            mCursor = mDb.query( //курсор выдай ID всех папок внутри
                    TABLE_NOTES,
                    new String[]{KEY_ID},
                    KEY_ITEM_TYPE + " = ? and " + KEY_PARENT_FOLDER + " = ?",
                    new String[]{TYPE_FOLDER, id},
                    null, null, null);
            if (mCursor.getCount() != 0) { //если папки внутри есть
                for (int i = 0; i < mCursor.getCount(); i++) { //вызови метод удаления для каждой папки
                    if (mCursor.moveToPosition(i))
                        deleteItem(mCursor.getString(mCursor.getColumnIndex(KEY_ID)));
                }
                //после удаления внутренних папок, еще раз вызови метод удаления
                //чтобы удалить внутренние заметки, если они есть
                deleteItem(id);
            } else { //если папок внутри нет
                mCursor = mDb.query( //крсор выдай ID всех заметок внутри
                        TABLE_NOTES,
                        new String[]{KEY_ID},
                        KEY_ITEM_TYPE + " = ? and " + KEY_PARENT_FOLDER + " = ?",
                        new String[]{TYPE_NOTE, id},
                        null, null, null);
                if (mCursor.getCount() != 0) { //если заметки есть
                    for (int i = 0; i < mCursor.getCount(); i++) { //удали каждую заметку
                        if (mCursor.moveToPosition(i))
                            mDb.delete(TABLE_NOTES, KEY_ID + " = ?", new String[]{id});
                    }
                    mDb.delete(TABLE_NOTES, KEY_ID + " = ?", new String[]{id}); //и в конце удали саму папку
                } else { //если папка пуста
                    mDb.delete(TABLE_NOTES, KEY_ID + " = ?", new String[]{id}); //удали саму папку
                }
            }
            mCursor.close();
        } else { //если удаляем заметку
            mDb.delete(TABLE_NOTES, KEY_ID + " = ?", new String[]{id}); //удали заметку по id
        }
    }

    void saveNoteToDb(String id, String name, String text) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_NOTE_NAME, name);
        cv.put(KEY_NOTE_TEXT, text);
        mDb.update(TABLE_NOTES, cv, KEY_ID + " = ?", new String[]{id});
    }
}
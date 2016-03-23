package eric.start.testtwo;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;

public class MyContentProvider extends ContentProvider {


    // // Константы для БД
    // БД
    static final String DB_NAME = "mydb";
    static final int DB_VERSION = 1;

    // Таблицы
    static final String FRIENDS_TABLE = "friends";
    static final String AUDIO_TABLE = "audio";
    static final String MESSAGE_TABLE = "message";

    // Поля
    static final String FRIENDS_ID = "_id";
    static final String FRIENDS_NAME = "firstName";
    static final String FRIENDS_EMAIL = "secondName";
    static final String FRIENDS_STATUS = "status";

    static final String MUSIC_ID = "_id";
    static final String MUSIC_ARTIST = "artist";
    static final String MUSIC_TITLE = "title";
    static final String MUSIC_URL = "url";

    static final String MESSAGE_ID = "_id";
    static final String MESSAGE_OUT = "out";
    static final String MESSAGE_USER_ID = "user_id";
    static final String MESSAGE_BODY = "body";

    // Скрипт создания таблицы
    static final String DB_CREATE = "create table " + FRIENDS_TABLE + "("
            + FRIENDS_ID + " integer primary key autoincrement, "
            + FRIENDS_NAME + " text, " + FRIENDS_EMAIL + " text" + FRIENDS_STATUS + " integer" + ");";

    static final String DB_CREATE_MUSIC = "create table " + AUDIO_TABLE + "("
            + MUSIC_ID + " integer primary key autoincrement, "
            + MUSIC_ARTIST + " text, " + MUSIC_TITLE + " text," + MUSIC_URL + " text" + ");";


    static final String DB_CREATE_MESSAGE = "create table " + MESSAGE_TABLE + "("
            + MESSAGE_ID + " integer primary key autoincrement, "
            + MESSAGE_BODY + " text, " + MESSAGE_USER_ID + " integer" + MESSAGE_OUT + " integer" + ");";




    // // Uri
    // authority
    static final String AUTHORITY = "eric.start.provider.TestTwo";

    // path
    static final String FRIENDS_PATH = "friends";
    static final String MUSIC_PATH = "audio";
    static final String MESSAGE_PATH = "message";


    // Общий Uri
    public static final Uri FRIENDS_URI = Uri.parse("content://"
            + AUTHORITY + "/" + FRIENDS_PATH);

    public static final Uri AUDIO_URI = Uri.parse("content://"
            + AUTHORITY + "/" + MUSIC_PATH);

    public static final Uri MESSAGE_URI = Uri.parse("content://"
            + AUTHORITY + "/" + MESSAGE_PATH);


    // флаги uri
    static final int URI_FRIENDS = 1;
    static final int URI_FRIENDS_ID = 4;
    static final int URI_MUSIC = 2;
    static final int URI_MUSIC_ID = 5;
    static final int URI_MESSAGE = 3;


    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, FRIENDS_PATH, URI_FRIENDS);
        uriMatcher.addURI(AUTHORITY, MUSIC_PATH, URI_MUSIC);
        uriMatcher.addURI(AUTHORITY, MESSAGE_PATH, URI_MESSAGE);
        uriMatcher.addURI(AUTHORITY, FRIENDS_PATH + "/#", URI_FRIENDS_ID);
        uriMatcher.addURI(AUTHORITY, MUSIC_PATH + "/#", URI_MUSIC_ID);
    }

    DBHelper dbHelper;
    SQLiteDatabase db;
    Cursor cursor;

    public boolean onCreate() {

        dbHelper = new DBHelper(getContext());
        return true;
    }


    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        String tableDB = "";

        switch (uriMatcher.match(uri)) {
            case URI_FRIENDS: // общий Uri
                tableDB = FRIENDS_TABLE;
                break;
            case URI_FRIENDS_ID:
                tableDB = FRIENDS_TABLE;

                String id = uri.getLastPathSegment();

                if (TextUtils.isEmpty(selection)) {
                    selection = FRIENDS_ID + " = " + id;
                } else {
                    selection = selection + " AND " + FRIENDS_ID + " = " + id;
                }

                break;
            case URI_MUSIC: // общий Uri
                tableDB = AUDIO_TABLE;
                break;
            case URI_MUSIC_ID:
                tableDB = AUDIO_TABLE;

                String idd = uri.getLastPathSegment();

                if (TextUtils.isEmpty(selection)) {
                    selection = MUSIC_ID + " = " + idd;
                } else {
                    selection = selection + " AND " + MUSIC_ID + " = " + idd;
                }

                break;
            case URI_MESSAGE: // общий Uri
                tableDB = MESSAGE_TABLE;
                break;
        }

        db = dbHelper.getWritableDatabase();

        cursor = db.query(tableDB, null, selection,null, null, null, null);

        return cursor;
    }

    public Uri insert(Uri uri, ContentValues values) {

        db = dbHelper.getWritableDatabase();

        long rowID;
        Uri resultUri=Uri.parse("content://eric.start.provider.TestTwo/audio");
        switch(uriMatcher.match(uri)){


            case URI_FRIENDS:
                 rowID = db.insert(FRIENDS_TABLE, null, values);
                 resultUri = ContentUris.withAppendedId(FRIENDS_URI, rowID);

                break;
            case URI_MUSIC:
                rowID = db.insert(AUDIO_TABLE, null, values);
                resultUri = ContentUris.withAppendedId(AUDIO_URI, rowID);
                // уведомляем ContentResolver, что данные по адресу resultUri изменились

                break;
            case URI_MESSAGE:
                rowID = db.insert(MESSAGE_TABLE, null, values);
                resultUri = ContentUris.withAppendedId(MESSAGE_URI, rowID);
                // уведомляем ContentResolver, что данные по адресу resultUri изменились

                break;

        }

        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {

        switch (uriMatcher.match(uri)) {
            case URI_FRIENDS:

                break;
            case URI_FRIENDS_ID:
                String id = uri.getLastPathSegment();

                if (TextUtils.isEmpty(selection)) {
                    selection = FRIENDS_ID + " = " + id;
                } else {
                    selection = selection + " AND " + FRIENDS_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(FRIENDS_TABLE, selection, selectionArgs);



        return cnt;
    }

    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0;
    }

    public String getType(Uri uri) {
        return null;
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
            db.execSQL(DB_CREATE_MUSIC);
            db.execSQL(DB_CREATE_MESSAGE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

}

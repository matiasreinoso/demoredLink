package mreinoso.demo_redlink.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import mreinoso.demo_redlink.model.Album;
import mreinoso.demo_redlink.model.Photo;

public class DemoDB extends SQLiteOpenHelper {

    private static DemoDB sInstance;
    private static Context mContext;
    private static SQLiteDatabase mDatabase;

    private static final String DATABASE_NAME = "DemoDB";
    private static final int DATABASE_VERSION = 1;


    //Table Data
    private static final String ALBUM_TABLE = "albums";
    private static final String PHOTO_TABLE = "photos";

    //Rows
    private static final String KEY_ROWID = "_idRow";
    private static final String KEY_ID = "_id";
    private static final String KEY_USER_ID = "_userId";
    private static final String KEY_TITLE = "_title";
    private static final String KEY_ALBUM_ID = "_albumID";
    private static final String KEY_URL = "url";
    private static final String KEY_THUMBNAIL_URL = "_thumbnailUrl";

    public DemoDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public static synchronized DemoDB getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DemoDB(context.getApplicationContext());
            mDatabase = sInstance.getWritableDatabase();
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + ALBUM_TABLE + "(" + KEY_ROWID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_ID + " TEXT NOT NULL, "
                + KEY_USER_ID + " TEXT NOT NULL, "
                + KEY_TITLE + " TEXT NOT NULL "
                + ");");


        db.execSQL("CREATE TABLE " + PHOTO_TABLE + "(" + KEY_ROWID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_ID + " TEXT NOT NULL, "
                + KEY_ALBUM_ID + " TEXT NOT NULL, "
                + KEY_TITLE + " TEXT NOT NULL, "
                + KEY_URL + " TEXT NOT NULL, "
                + KEY_THUMBNAIL_URL + " TEXT NOT NULL "
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PHOTO_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ALBUM_TABLE);
        onCreate(db);
    }

    public void updateAlbum(Album album) {

        ContentValues cv = new ContentValues();

        cv.put(KEY_ID, String.valueOf(album.getId()));
        cv.put(KEY_TITLE, ((album.getTitle() == null) ? "" : album.getTitle()));
        cv.put(KEY_USER_ID, String.valueOf(album.getUserId()));

        if (mDatabase.update(ALBUM_TABLE, cv,
                KEY_ID + "='" + album.getId() + "'", null) == 0) {
            mDatabase.insertOrThrow(ALBUM_TABLE, null, cv);
        }
    }

    public ArrayList<Photo> getPhoto(String id) {

        ArrayList<Photo> list = new ArrayList<>();

        Cursor c = mDatabase.rawQuery("SELECT * FROM " + PHOTO_TABLE + " WHERE "
                + KEY_ALBUM_ID + " = '" + id
                + "' ORDER BY " + KEY_ROWID + " ASC", null);
        if (c != null) {

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

                Photo data = new Photo();
                data.setAlbumId(c.getInt(1));
                data.setId(c.getInt(2));
                data.setTitle(c.getString(3));
                data.setUrl(c.getString(4));
                data.setThumbnailUrl(c.getString(5));
                list.add(data);
            }
            c.close();
        }
        return list;
    }

    public void updatePhoto(Photo ph) {

        ContentValues cv = new ContentValues();

        cv.put(KEY_ID, String.valueOf(ph.getId()));
        cv.put(KEY_TITLE, ((ph.getTitle() == null) ? "" : ph.getTitle()));
        cv.put(KEY_URL, ((ph.getUrl() == null) ? "" : ph.getUrl()));
        cv.put(KEY_THUMBNAIL_URL, ((ph.getThumbnailUrl() == null) ? "" : ph.getThumbnailUrl()));
        cv.put(KEY_ALBUM_ID, String.valueOf(ph.getAlbumId()));

        if (mDatabase.update(PHOTO_TABLE, cv,
                KEY_ID + "='" + ph.getId() + "'", null) == 0) {
            mDatabase.insertOrThrow(PHOTO_TABLE, null, cv);
        }
    }

    public ArrayList<Album> getAlbum() {


        ArrayList<Album> list = new ArrayList<>();

        Cursor c = mDatabase.rawQuery("SELECT * FROM " + ALBUM_TABLE
                + " ORDER BY " + KEY_ROWID + " ASC", null);
        if (c != null) {

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                Album data = new Album();

                data.setUserId(c.getInt(2));
                data.setId(c.getInt(1));
                data.setTitle(c.getString(3));

                list.add(data);
            }
            c.close();
        }
        return list;

    }

}


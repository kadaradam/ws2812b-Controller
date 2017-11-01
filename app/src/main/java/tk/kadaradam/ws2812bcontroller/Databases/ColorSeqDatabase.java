package tk.kadaradam.ws2812bcontroller.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import tk.kadaradam.ws2812bcontroller.Utils.ColorSequences;

/**
 * Created by Adam on 8/29/2017.
 */

public class ColorSeqDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "color_sequences.db";
    private static final String TABLE_NAME = "ColorSequences";

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_LED_START = "led_start";
    private static final String COLUMN_LED_FINISH = "led_finish";
    private static final String COLUMN_COLOR = "color";

    public ColorSeqDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_LED_START + " INTEGER," +
                COLUMN_LED_FINISH + " INTEGER," +
                COLUMN_COLOR + " INTEGER);"
        );
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long addColorSequence(ColorSequences sequence) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();

        values.put(COLUMN_LED_START, sequence.getLedFrom());
        values.put(COLUMN_LED_FINISH, sequence.getLedTo());
        values.put(COLUMN_COLOR, sequence.getLedColor());

        long id = db.insert(TABLE_NAME, null, values);
        db.close();

        return id;
    }

    public void removeColorSequence(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=" + id);
        db.close();
    }

    public List<ColorSequences> getColorSequences() {
        List<ColorSequences> dbResult = new ArrayList<ColorSequences>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {

                ColorSequences values = new ColorSequences();
                values.setID(cursor.getInt(0));
                values.setLedFrom(cursor.getInt(1));
                values.setLedTo(cursor.getInt(2));
                values.setLedColor(cursor.getInt(3));
                dbResult.add(values);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return dbResult;
    }
}

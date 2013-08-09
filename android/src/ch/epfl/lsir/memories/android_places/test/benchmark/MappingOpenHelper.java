package ch.epfl.lsir.memories.android_places.test.benchmark;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Sebastian Claici
 */
public class MappingOpenHelper extends SQLiteOpenHelper {

    private static final String MAPPING_TABLE_NAME = "LocationMapping";
    private static final String MAPPING_TABLE_CREATE =
            "CREATE TABLE " + MAPPING_TABLE_NAME + " (" +
            "location TEXT, " +
            "activity TEXT);";

    private static final String DATABASE_NAME = "AndroidDB";
    private static final int DATABASE_VERSION = 1;

    public MappingOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MAPPING_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}

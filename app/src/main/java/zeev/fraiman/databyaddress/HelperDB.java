package zeev.fraiman.databyaddress;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class HelperDB extends SQLiteOpenHelper {

    public static final String DB_FILE="all_data.db";
    public static final String DATA_TABLE="All_data";
    public static final String DATA_LAT="Latitude";
    public static final String DATA_LON="Longitude";

    public HelperDB(Context context) {
        super(context, DB_FILE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String st="CREATE TABLE IF NOT EXISTS "+DATA_TABLE;
        st+=" ( "+DATA_LAT+" TEXT, "+DATA_LON+" TEXT);";
        db.execSQL(st);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

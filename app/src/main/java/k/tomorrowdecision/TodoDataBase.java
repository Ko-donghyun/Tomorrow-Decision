package k.tomorrowdecision;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoDataBase extends SQLiteOpenHelper {
    public TodoDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        // 테이블 생성
        database.execSQL("CREATE TABLE todo (_id INTEGER PRIMARY KEY," + " time INTEGER, todo TEXT, textColorCode TEXT, backgroundColorCode TEXT);");
        long firstTime = System.currentTimeMillis();

        // 레코드 추가
        for (int i = 0; i < 48; i++) {
            database.execSQL("INSERT INTO todo VALUES(" + i + ", " + (firstTime - 86400000 + 3600000 * i) + ", '', '#000000', '#FFFFFF');");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS todo");
        onCreate(database);
    }
}
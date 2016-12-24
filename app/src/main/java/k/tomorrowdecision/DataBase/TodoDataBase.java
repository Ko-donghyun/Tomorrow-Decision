package k.tomorrowdecision.DataBase;

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
        database.execSQL("CREATE TABLE todo (time TEXT PRIMARY KEY, todo TEXT, importance INTEGER, textColorCode TEXT, backgroundColorCode TEXT);");

        // 레코드 추가
        long firstTime = System.currentTimeMillis();
        firstTime = firstTime / 3600000;
        firstTime = firstTime * 3600000;
        for (int i = 0; i <= 48; i++) {
            database.execSQL("INSERT INTO todo VALUES(" + (firstTime - 86400000 + 3600000 * i) + ", '', 0, '#000000', '#FFFFFF');");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS todo");
        onCreate(database);
    }
}
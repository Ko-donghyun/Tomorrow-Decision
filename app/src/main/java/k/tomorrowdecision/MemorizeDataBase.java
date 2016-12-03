package k.tomorrowdecision;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MemorizeDataBase extends SQLiteOpenHelper {
    public MemorizeDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        // 테이블 생성
        database.execSQL("CREATE TABLE memorize (_id INTEGER PRIMARY KEY AUTOINCREMENT," + " todo TEXT);");

        // 레코드 추가
        database.execSQL("INSERT INTO memorize VALUES(null, '주기적인 일');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS todo");
        onCreate(database);
    }
}

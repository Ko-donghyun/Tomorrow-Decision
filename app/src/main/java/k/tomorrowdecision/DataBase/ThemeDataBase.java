package k.tomorrowdecision.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ThemeDataBase extends SQLiteOpenHelper {
    public ThemeDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        // 테이블 생성
        createTheme(database);

        // 테이블 생성
        createUserTheme(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS theme");
        createTheme(database);
    }

    private void createTheme(SQLiteDatabase database) {
        // 테이블 생성
        database.execSQL("CREATE TABLE theme (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, sub_title TEXT, " +
                "level_0_tg TEXT, level_0_bg TEXT, level_1_tg TEXT, level_1_bg TEXT, " +
                "level_2_tg TEXT, level_2_bg TEXT, level_3_tg TEXT, level_3_bg TEXT, " +
                "level_4_tg TEXT, level_4_bg TEXT);");

        // 레코드 추가
        database.execSQL("INSERT INTO theme VALUES(1, '커스텀', '개인 취향', " +
                "'#000000', '#FFFFFF', '#000000', '#FFFFFF', " +
                "'#000000', '#FFFFFF', '#000000', '#FFFFFF', " +
                "'#000000', '#FFFFFF');");
        database.execSQL("INSERT INTO theme VALUES(2, '봄 테마', '벚꽃 생각', " +
                "'#D60039', '#F6B1C3', '#F0F0F0', '#F0788C', " +
                "'#F6F6F6', '#DE264C', '#FAFAFA', '#BC0D35', " +
                "'#FFFFFF', '#A20D1E');");
        database.execSQL("INSERT INTO theme VALUES(3, '여름 테마', '짙은 녹음', " +
                "'#127128', '#96ED89', '#F0F0F0', '#45BF55', " +
                "'#F6F6F6', '#168039', '#FAFAFA', '#044D29', " +
                "'#FFFFFF', '#00261C');");
        database.execSQL("INSERT INTO theme VALUES(4, '가을 테마', '낙엽 밟기', " +
                "'#C42B2A', '#FFD462', '#F0F0F0', '#FC7D49', " +
                "'#F6F6F6', '#CF423C', '#FAFAFA', '#7A1631', " +
                "'#FFFFFF', '#3F0B1B');");
        database.execSQL("INSERT INTO theme VALUES(5, '겨울 테마', '눈과 얼음', " +
                "'#3C61BE', '#ADD5F7', '#F0F0F0', '#7FB2F0', " +
                "'#F6F6F6', '#4E7AC7', '#FAFAFA', '#35478C', " +
                "'#FFFFFF', '#16193B');");
    }

    private void createUserTheme(SQLiteDatabase database) {
        // 테이블 생성
        database.execSQL("CREATE TABLE user_theme (_id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, theme_id INTEGER);");

        // 레코드 추가
        database.execSQL("INSERT INTO user_theme VALUES(null, 1, 1);");
        database.execSQL("INSERT INTO user_theme VALUES(null, 1, 2);");
        database.execSQL("INSERT INTO user_theme VALUES(null, 1, 3);");
        database.execSQL("INSERT INTO user_theme VALUES(null, 1, 4);");
    }
}
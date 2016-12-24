package k.tomorrowdecision.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import k.tomorrowdecision.DataBase.ThemeDataBase;
import k.tomorrowdecision.Item.ThemeItem;
import k.tomorrowdecision.ListViewAdapter.ThemeListViewAdapter;
import k.tomorrowdecision.R;

public class ThemePickerDialog extends Dialog {

    private Context context;

    private View.OnClickListener okayClickListener;
    private View.OnClickListener closeClickListener;
    private ThemeListViewAdapter themeListViewAdapter;

    ThemeDataBase themeDataBase;
    final String themeDBName = "theme.db";
    final int themeDBVersion = 1;

    public static SharedPreferences themePreference;
    private int preThemeIndex;
    private int newThemeIndex;

    TextView newTheme;

    String[] importanceColorCodes;
    TextView importance_1;
    TextView importance_2;
    TextView importance_3;
    TextView importance_4;
    TextView importance_5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.theme_picker_dialog);

        themeDataBase = new ThemeDataBase(context, themeDBName, null, themeDBVersion);

        ListView themeListView = (ListView) findViewById(R.id.theme_list_view);
        themeListViewAdapter = new ThemeListViewAdapter();
        getThemeData();


        themeListView.setAdapter(themeListViewAdapter);
        themeListView.setDividerHeight(0);
        Button okayButton = (Button) findViewById(R.id.okay_button);
        Button closeButton = (Button) findViewById(R.id.close_button);

        themePreference = context.getSharedPreferences("theme", Activity.MODE_PRIVATE);
        preThemeIndex = themePreference.getInt("theme", 1);
        preThemeIndex -= 1;
        newThemeIndex = preThemeIndex;

        TextView preTheme = (TextView) findViewById(R.id.pre_theme);
        newTheme = (TextView) findViewById(R.id.new_theme);
        preTheme.setText(themeListViewAdapter.getItem(preThemeIndex).getTitle());
        newTheme.setText(themeListViewAdapter.getItem(preThemeIndex).getTitle());

        importance_1 = (TextView) findViewById(R.id.importance_level_1);
        importance_2 = (TextView) findViewById(R.id.importance_level_2);
        importance_3 = (TextView) findViewById(R.id.importance_level_3);
        importance_4 = (TextView) findViewById(R.id.importance_level_4);
        importance_5 = (TextView) findViewById(R.id.importance_level_5);

        importanceColorCodes = themeListViewAdapter.getItem(preThemeIndex).getImportanceColorCodes();
        importance_1.setTextColor(Color.parseColor(importanceColorCodes[0]));
        importance_1.setBackgroundColor(Color.parseColor(importanceColorCodes[1]));
        importance_2.setTextColor(Color.parseColor(importanceColorCodes[2]));
        importance_2.setBackgroundColor(Color.parseColor(importanceColorCodes[3]));
        importance_3.setTextColor(Color.parseColor(importanceColorCodes[4]));
        importance_3.setBackgroundColor(Color.parseColor(importanceColorCodes[5]));
        importance_4.setTextColor(Color.parseColor(importanceColorCodes[6]));
        importance_4.setBackgroundColor(Color.parseColor(importanceColorCodes[7]));
        importance_5.setTextColor(Color.parseColor(importanceColorCodes[8]));
        importance_5.setBackgroundColor(Color.parseColor(importanceColorCodes[9]));

        themeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                newThemeIndex = position;

                importanceColorCodes = themeListViewAdapter.getItem(position).getImportanceColorCodes();
                importance_1.setTextColor(Color.parseColor(importanceColorCodes[0]));
                importance_1.setBackgroundColor(Color.parseColor(importanceColorCodes[1]));
                importance_2.setTextColor(Color.parseColor(importanceColorCodes[2]));
                importance_2.setBackgroundColor(Color.parseColor(importanceColorCodes[3]));
                importance_3.setTextColor(Color.parseColor(importanceColorCodes[4]));
                importance_3.setBackgroundColor(Color.parseColor(importanceColorCodes[5]));
                importance_4.setTextColor(Color.parseColor(importanceColorCodes[6]));
                importance_4.setBackgroundColor(Color.parseColor(importanceColorCodes[7]));
                importance_5.setTextColor(Color.parseColor(importanceColorCodes[8]));
                importance_5.setBackgroundColor(Color.parseColor(importanceColorCodes[9]));

                newTheme.setText(themeListViewAdapter.getItem(position).getTitle());
            }
        });
        okayButton.setOnClickListener(okayClickListener);
        closeButton.setOnClickListener(closeClickListener);
    }

    public ThemePickerDialog(Context context, View.OnClickListener closeClickListener, View.OnClickListener okayClickListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);

        this.context = context;
        this.okayClickListener = okayClickListener;
        this.closeClickListener = closeClickListener;
    }

    public ThemeItem getSelectedThemeItem() {
        return themeListViewAdapter.getItem(newThemeIndex);
    }

    public int getSelectedTheme() {
        return (newThemeIndex + 1);
    }

    private void getThemeData() {
        SQLiteDatabase sqLiteDatabase = themeDataBase.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("" +
                "SELECT t.*, ut._id AS usable " +
                "FROM theme AS t " +
                "LEFT OUTER JOIN user_theme AS ut " +
                "ON t._id = ut.theme_id;", null);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                String[] importanceColorCodes = {cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12)};
                int usable = 0;
                if (cursor.getString(13) != null) {
                    usable = 1;
                }
                themeListViewAdapter.addItem(cursor.getInt(0), cursor.getString(1), cursor.getString(2), importanceColorCodes, usable);
            } while (cursor.moveToNext());

            cursor.close();
        }
        sqLiteDatabase.close();
    }

}
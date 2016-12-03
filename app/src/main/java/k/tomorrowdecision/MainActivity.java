package k.tomorrowdecision;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    InputMethodManager imm;
    // 할 일 디비
    TodoDataBase todoDataBase;
    final String todoDBName = "Todo.db";
    final int todoDBVersion = 1;
    MemorizeDataBase memorizeDataBase;
    final String memorizeDBName = "memorize.db";
    final int memorizeDBVersion = 1;
    MustDoDataBase mustDoDataBase;
    final String mustDoDBName = "mustDo.db";
    final int mustDoDBVersion = 1;

    ViewFlipper mainViewFlipper;

    ListView memorizeListView;
    ListView mustDoListView;
    ListView todoListView;

    TodoListViewAdapter todoListViewAdapter;
    TextListViewAdapter mustDoListViewAdapter;
    TextListViewAdapter memorizeListViewAdapter;

    String setTextColor = "#000000";
    String setBackgroundColor = "#FFFFFF";
    String timeText = "";
    int itemPosition;

    Boolean clickFlag = true;
    Button cancelButton;
    Button doneButton;

    private final long FINISH_INTERVAL_TIME = 1000;
    private long backPressedTime = 0;

    TextView todoTime;
    EditText todo;
    LinearLayout todoBackground;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM월 dd일 (E) HH시 mm:ss", Locale.getDefault());

    int[] buttonArray = {
      R.id.color_picker_01, R.id.color_picker_02, R.id.color_picker_03,
      R.id.color_picker_04, R.id.color_picker_05, R.id.color_picker_06,
      R.id.color_picker_07, R.id.color_picker_08, R.id.color_picker_09,
    };

    Button backgroundColorPicker;
    Button textColorPicker;
    String colorPickerSwitch = "text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        // 데이터 베이스 가져오기
        todoDataBase = new TodoDataBase(this, todoDBName, null, todoDBVersion);
        memorizeDataBase = new MemorizeDataBase(this, memorizeDBName, null, memorizeDBVersion);
        mustDoDataBase = new MustDoDataBase(this, mustDoDBName, null, mustDoDBVersion);


        todoListView = (ListView) findViewById(R.id.todo_list_view);
        memorizeListView = (ListView) findViewById(R.id.memorize_list_view);
        mustDoListView = (ListView) findViewById(R.id.must_do_list_view);
        mainViewFlipper = (ViewFlipper) findViewById(R.id.main_view_flipper);
        mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
        mainViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));

        // Adapter 생성
        todoListViewAdapter = new TodoListViewAdapter();
        getTodoData();
        memorizeListViewAdapter = new TextListViewAdapter();
        getMemorizeData();
        mustDoListViewAdapter = new TextListViewAdapter();
        getMustDoData();

        // 리스트뷰 참조 및 Adapter달기
        todoListView.setAdapter(todoListViewAdapter);
        todoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get Item
                mainViewFlipper.showNext();
            }
        });
        todoListView.setSelection(4);

        // 리스트뷰 참조 및 Adapter달기
        memorizeListView.setAdapter(memorizeListViewAdapter);
        memorizeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                todo.setText(memorizeListViewAdapter.getItem(position).getTodo());
            }
        });


        // 리스트뷰 참조 및 Adapter달기
        mustDoListView.setAdapter(mustDoListViewAdapter);
        mustDoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                todo.setText(mustDoListViewAdapter.getItem(position).getTodo());
            }
        });

    private void getTodoData() {
        SQLiteDatabase sqLiteDatabase = todoDataBase.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM todo", null);

        cursor.moveToFirst();
        do {
            Date data = new Date(Long.parseLong(cursor.getString(0)));
            String stringDate = simpleDateFormat.format(data);
            todoListViewAdapter.addItem(cursor.getString(0), stringDate, cursor.getString(1), cursor.getString(2), cursor.getString(3));
        } while( cursor.moveToNext() );

        cursor.close();
        sqLiteDatabase.close();
    }

    private void getMemorizeData() {
        SQLiteDatabase sqLiteDatabase = memorizeDataBase.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM memorize", null);

        cursor.moveToFirst();
        do {
            memorizeListViewAdapter.addItem(cursor.getInt(0), cursor.getString(1));
        } while( cursor.moveToNext() );

        cursor.close();
        sqLiteDatabase.close();
    }

    private void getMustDoData() {
        SQLiteDatabase sqLiteDatabase = mustDoDataBase.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM must_do", null);

        cursor.moveToFirst();
        do {
            mustDoListViewAdapter.addItem(cursor.getInt(0), cursor.getString(1));
        } while( cursor.moveToNext() );

        cursor.close();
        sqLiteDatabase.close();
    }
    }
}

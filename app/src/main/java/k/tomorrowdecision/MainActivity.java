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
import android.view.Window;
import android.view.WindowManager;
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
    String itemAddDialogSwitch = "memorize";

    TextView memorizeArrange;
    TextView mustdoArrange;

    private ItemAddDialog itemAddDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        // 데이터 베이스 가져오기
        todoDataBase = new TodoDataBase(this, todoDBName, null, todoDBVersion);
        memorizeDataBase = new MemorizeDataBase(this, memorizeDBName, null, memorizeDBVersion);
        mustDoDataBase = new MustDoDataBase(this, mustDoDBName, null, mustDoDBVersion);

        cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFlag = false;
                hideKeyboard(todo);
                mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_right_in));
                mainViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_right_out));
                mainViewFlipper.showPrevious();
                clickFlag = true;
            }
        });

        doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFlag = false;
                saveTodo(itemPosition);
                clickFlag = true;
            }
        });

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

        todoTime = (TextView) findViewById(R.id.todo_time);
        todo = (EditText) findViewById(R.id.todo_item);
        todoBackground = (LinearLayout) findViewById(R.id.todo_background);
        memorizeArrange = (TextView) findViewById(R.id.memorize_arrange);
        memorizeArrange.setOnClickListener(itemAddClickListener);
        mustdoArrange = (TextView) findViewById(R.id.mustdo_arrange);
        mustdoArrange.setOnClickListener(itemAddClickListener);

        // 리스트뷰 참조 및 Adapter달기
        todoListView.setAdapter(todoListViewAdapter);
        todoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                todoTime.setText(todoListViewAdapter.getItem(position).getTimeText());
                todoBackground.setBackgroundColor(Color.parseColor(todoListViewAdapter.getItem(position).getBackgroundColorCode()));
                todo.setText(todoListViewAdapter.getItem(position).getTodo());
                todo.setTextColor(Color.parseColor(todoListViewAdapter.getItem(position).getTextColorCode()));
                timeText = todoListViewAdapter.getItem(position).getTime();
                itemPosition = position;

                mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_left_in));
                mainViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_left_out));
                mainViewFlipper.showNext();
            }
        });
        todoListView.setSelection(21);

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


        for (int i = 0; i < 9; i++) {
            findViewById(buttonArray[i]).setOnClickListener(colorPickerListener);
        }
        backgroundColorPicker = (Button) findViewById(R.id.background_color_picker);
        backgroundColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorPickerSwitch = "background";
                backgroundColorPicker.setBackgroundColor(Color.parseColor("#efefef"));
                textColorPicker.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        });
        textColorPicker = (Button) findViewById(R.id.text_color_picker);
        textColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorPickerSwitch = "text";
                backgroundColorPicker.setBackgroundColor(Color.parseColor("#FFFFFF"));
                textColorPicker.setBackgroundColor(Color.parseColor("#efefef"));
            }
        });


    }

    @Override
    public void onRestart() {
        super.onRestart();
        Intent restartIntent = new Intent(MainActivity.this, Loading.class);
        startActivity(restartIntent);
        finish();
    }

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

    private void saveTodo(int itemPosition) {
        SQLiteDatabase todoDatabase = todoDataBase.getWritableDatabase();

        int backgroundColor = Color.TRANSPARENT;
        Drawable background = todoBackground.getBackground();
        if (background instanceof ColorDrawable)
            backgroundColor = ((ColorDrawable) background).getColor();

        String backgroundHexCode = String.format("#%06X", (0xFFFFFF & backgroundColor));
        String textHexCode = String.format("#%06X", (0xFFFFFF & todo.getCurrentTextColor()));

        ContentValues values = new ContentValues();

        values.put("todo", todo.getText().toString());
        values.put("textColorCode", textHexCode);
        values.put("backgroundColorCode", backgroundHexCode);
        todoDatabase.update("todo", values, "time='" + timeText + "'", null);

        todoDatabase.close();

        todoListViewAdapter.updateItem(itemPosition, todo.getText().toString(), textHexCode, backgroundHexCode);
        todoListViewAdapter.notifyDataSetChanged();
        hideKeyboard(todo);

        mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_right_in));
        mainViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_right_out));
        mainViewFlipper.showPrevious();
    }

    private long saveMemorizeItem(String todo) {
        SQLiteDatabase memorizeDatabase = memorizeDataBase.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("todo", todo);
        long id = memorizeDatabase.insert("memorize", null, values);

        memorizeDatabase.close();

        return id;
    }

    private long saveMustDoItem(String todo) {
        SQLiteDatabase mustDoDatabase = mustDoDataBase.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("todo", todo);
        long id = mustDoDatabase.insert("must_do", null, values);

        mustDoDatabase.close();

        return id;
    }

    Button.OnClickListener colorPickerListener = new View.OnClickListener() {
        public void onClick(View v) {
            int color = Color.TRANSPARENT;
            Drawable background = v.getBackground();
            if (background instanceof ColorDrawable)
                color = ((ColorDrawable) background).getColor();

            String stringColor = String.format("#%06X", (0xFFFFFF & color));

            if (colorPickerSwitch.equals("text")) {
                todo.setTextColor(color);
            } else {
                todoBackground.setBackgroundColor(color);
            }
        }
    };

    private View.OnClickListener dialogCancelClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            String item = itemAddDialog.getItem();

            itemAddDialog.dismiss();
        }
    };

    private View.OnClickListener dialogDoneClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (itemAddDialogSwitch.equals("Memorize")) {
                long id = saveMemorizeItem(itemAddDialog.getItem().toString());
                memorizeListViewAdapter.addItem(id, itemAddDialog.getItem().toString());
                memorizeListViewAdapter.notifyDataSetChanged();
            } else {
                long id = saveMustDoItem(itemAddDialog.getItem().toString());
                mustDoListViewAdapter.addItem(id, itemAddDialog.getItem().toString());
                mustDoListViewAdapter.notifyDataSetChanged();
            }
            itemAddDialog.dismiss();
        }
    };

    TextView.OnClickListener itemAddClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            String title = "";
            String subTitle = "";
                switch (v.getId()) {
                case R.id.memorize_arrange:
                    title = "Memorize";
                    subTitle = "주기적으로 하는 일에 대해서 추가하세요";
                    break;
                case R.id.mustdo_arrange:
                    title = "Must Do";
                    subTitle = "꼭 해야만 하는 일에 대해서 추가하세요";
                    break;
            }
            itemAddDialogSwitch = title;
            itemAddDialog = new ItemAddDialog(MainActivity.this, title, subTitle, dialogCancelClickListener, dialogDoneClickListener);
            itemAddDialog.show();
            itemAddDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            //하드웨어 뒤로가기 버튼에 따른 이벤트 설정
            case KeyEvent.KEYCODE_BACK:
                if (mainViewFlipper.getDisplayedChild() == 1) {
                    mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_right_in));
                    mainViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_right_out));
                    mainViewFlipper.showPrevious();
                } else {
                    long tempTime = System.currentTimeMillis();
                    long intervalTime = tempTime - backPressedTime;

                    if ( 0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime ) {
                        super.onBackPressed();
                    }
                    else {
                        backPressedTime = tempTime;
                        Toast.makeText(MainActivity.this, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void hideKeyboard(View view) {
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showKeyboard(View view) {
        imm.showSoftInput(view, 0);
    }
}

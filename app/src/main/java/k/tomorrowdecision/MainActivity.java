package k.tomorrowdecision;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import k.tomorrowdecision.DataBase.MemorizeDataBase;
import k.tomorrowdecision.DataBase.MustDoDataBase;
import k.tomorrowdecision.DataBase.TodoDataBase;
import k.tomorrowdecision.Dialog.ColorPickerDialog;
import k.tomorrowdecision.Dialog.InformationDialog;
import k.tomorrowdecision.Dialog.ItemAddDialog;
import k.tomorrowdecision.Dialog.ItemDeleteDialog;
import k.tomorrowdecision.Dialog.SettingDialog;
import k.tomorrowdecision.Dialog.ThemePickerDialog;
import k.tomorrowdecision.Item.ThemeItem;
import k.tomorrowdecision.ListViewAdapter.TextListViewAdapter;
import k.tomorrowdecision.ListViewAdapter.TodoListViewAdapter;

public class MainActivity extends AppCompatActivity {
    InputMethodManager imm;
    // 할 일 디비
    TodoDataBase todoDataBase;
    final String todoDBName = "Todo.db";
    final int todoDBVersion = 2;
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
    ImageView cancelButton;
    ImageView doneButton;

    private final long FINISH_INTERVAL_TIME = 1000;
    private long backPressedTime = 0;

    TextView todoTime;
    EditText todo;
    LinearLayout todoBackground;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM월 dd일 (E) HH시", Locale.getDefault());

    int[] buttonArray = {
      R.id.color_picker_01, R.id.color_picker_02, R.id.color_picker_03,
      R.id.color_picker_04, R.id.color_picker_05, R.id.color_picker_06,
      R.id.color_picker_07, R.id.color_picker_08, R.id.color_picker_09,
    };
    String[] colorCodeArray = {
            "#ff0000", "#ff9900", "#ffe600", "#3cff00", "#00ffe1", "#0011ff", "#6600ff", "#fb00ff", "#000000"
    };
    public static SharedPreferences[] colorPickerPreferences = new SharedPreferences[9];
    public static SharedPreferences.Editor[] colorPickerEditors = new SharedPreferences.Editor[9];
    Button colorButtons[] = new Button[9];
    int buttonIndex;

    Button backgroundColorPicker;
    Button textColorPicker;
    String colorPickerSwitch = "text";
    String itemAddDialogSwitch = "memorize";
    String itemDeleteDialogSwitch = "memorize";

    ImageView memorizeArrange;
    ImageView mustdoArrange;

    private ItemAddDialog itemAddDialog;
    private ItemDeleteDialog itemDeleteDialog;
    int deleteItemPosition;

    private ColorPickerDialog colorPickerDialog;

    private InformationDialog informationDialog;

    Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        tracker = application.getDefaultTracker();
        tracker.setScreenName("MainActivity");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        // 데이터 베이스 가져오기
        todoDataBase = new TodoDataBase(this, todoDBName, null, todoDBVersion);
        memorizeDataBase = new MemorizeDataBase(this, memorizeDBName, null, memorizeDBVersion);
        mustDoDataBase = new MustDoDataBase(this, mustDoDBName, null, mustDoDBVersion);

        cancelButton = (ImageView) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickFlag) {
                    clickFlag = false;
                    hideKeyboard(todo);
                    mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_right_in));
                    mainViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_right_out));
                    mainViewFlipper.showPrevious();
                    clickFlag = true;
                }
            }
        });

        doneButton = (ImageView) findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat hourFormat = new SimpleDateFormat("HH", Locale.getDefault());
                int hour = Integer.parseInt(hourFormat.format(new Date(System.currentTimeMillis())));

                if (clickFlag && (hour < 2 || 20 <= hour)) {
                    clickFlag = false;
                    saveTodo(itemPosition);
                    clickFlag = true;
                } else {
                    informationDialog = new InformationDialog(MainActivity.this, dialogOkayClickListener);
                    informationDialog.show();
                }
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
        memorizeArrange = (ImageView) findViewById(R.id.memorize_arrange);
        memorizeArrange.setOnClickListener(itemAddClickListener);
        mustdoArrange = (ImageView) findViewById(R.id.mustdo_arrange);
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
                tracker.send(new HitBuilders.EventBuilder().setCategory("EditPage").setAction("Press Button").setLabel("Move EditPage").build());
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
        memorizeListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                itemDeleteDialogSwitch = "Memorize";
                deleteItemPosition = position;
                itemDeleteDialog = new ItemDeleteDialog(MainActivity.this,
                        memorizeListViewAdapter.getItem(position).getId(),
                        "'" + memorizeListViewAdapter.getItem(position).getTodo() + "'이(가) 삭제 됩니다.",
                        dialogDeleteCancelClickListener, dialogDeleteDoneClickListener);
                itemDeleteDialog.show();
                return false;
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
        mustDoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                itemDeleteDialogSwitch = "Must Do";
                deleteItemPosition = position;
                itemDeleteDialog = new ItemDeleteDialog(MainActivity.this,
                        mustDoListViewAdapter.getItem(position).getId(),
                        "'" + mustDoListViewAdapter.getItem(position).getTodo() + "'이(가) 삭제 됩니다.",
                        dialogDeleteCancelClickListener, dialogDeleteDoneClickListener);
                itemDeleteDialog.show();
                return false;
            }
        });


        for (int i = 0; i < 9; i++) {
            colorButtons[i] = (Button) findViewById(buttonArray[i]);
            colorButtons[i].setOnClickListener(colorPickerListener);
            colorButtons[i].setOnLongClickListener(changeColorPickerListener);
            colorPickerPreferences[i] = getSharedPreferences("color_" + Integer.toString(i), Activity.MODE_PRIVATE);
            colorButtons[i].setBackgroundColor(Color.parseColor(colorPickerPreferences[i].getString("color_" + Integer.toString(i), colorCodeArray[i])));
        }

        backgroundColorPicker = (Button) findViewById(R.id.background_color_picker);
        backgroundColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorPickerSwitch = "background";
                backgroundColorPicker.setBackgroundColor(Color.parseColor("#efefef"));
                textColorPicker.setBackgroundColor(Color.parseColor("#FFFFFF"));
                tracker.send(new HitBuilders.EventBuilder().setCategory("EditPage").setAction("Press Button").setLabel("background Color Choice Click").build());
            }
        });
        textColorPicker = (Button) findViewById(R.id.text_color_picker);
        textColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorPickerSwitch = "text";
                backgroundColorPicker.setBackgroundColor(Color.parseColor("#FFFFFF"));
                textColorPicker.setBackgroundColor(Color.parseColor("#efefef"));
                tracker.send(new HitBuilders.EventBuilder().setCategory("EditPage").setAction("Press Button").setLabel("text Color Choice Click").build());
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop(){
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
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

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                memorizeListViewAdapter.addItem(cursor.getInt(0), cursor.getString(1));
            } while (cursor.moveToNext());

            cursor.close();
        }

        sqLiteDatabase.close();
    }

    private void getMustDoData() {
        SQLiteDatabase sqLiteDatabase = mustDoDataBase.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM must_do", null);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                mustDoListViewAdapter.addItem(cursor.getInt(0), cursor.getString(1));
            } while (cursor.moveToNext());

            cursor.close();
        }
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
        tracker.send(new HitBuilders.EventBuilder().setCategory("EditPage").setAction("Press Button").setLabel("saveTodo").build());
    }

    private long saveMemorizeItem(String todo) {
        SQLiteDatabase memorizeDatabase = memorizeDataBase.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("todo", todo);
        long id = memorizeDatabase.insert("memorize", null, values);

        memorizeDatabase.close();
        tracker.send(new HitBuilders.EventBuilder().setCategory("AddItem").setAction("Press Button").setLabel("memorize item add button").build());
        return id;
    }

    private long saveMustDoItem(String todo) {
        SQLiteDatabase mustDoDatabase = mustDoDataBase.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("todo", todo);
        long id = mustDoDatabase.insert("must_do", null, values);

        mustDoDatabase.close();
        tracker.send(new HitBuilders.EventBuilder().setCategory("AddItem").setAction("Press Button").setLabel("mustdo item add button").build());
        return id;
    }

    private void deleteMemorizeItem(long id) {
        SQLiteDatabase mustDoDatabase = memorizeDataBase.getWritableDatabase();
        mustDoDatabase.delete("memorize", "_id=" + id, null);
        mustDoDatabase.close();
        tracker.send(new HitBuilders.EventBuilder().setCategory("DeleteItem").setAction("Press Button").setLabel("memorize item delete button").build());
    }

    private void deleteMustDoItem(long id) {
        SQLiteDatabase mustDoDatabase = mustDoDataBase.getWritableDatabase();
        mustDoDatabase.delete("must_do", "_id=" + id, null);
        mustDoDatabase.close();
        tracker.send(new HitBuilders.EventBuilder().setCategory("DeleteItem").setAction("Press Button").setLabel("mustdo item delete button").build());
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

    Button.OnLongClickListener changeColorPickerListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            int viewId = v.getId();
            for (int i = 0; i < 9; i++) {
                if (viewId == buttonArray[i]) {
                    buttonIndex = i;
                    break;
                }
            }
            colorPickerDialog = new ColorPickerDialog(MainActivity.this, dialogColorPickerCancelClickListener, dialogColorPickerDoneClickListener);
            colorPickerDialog.show();
            tracker.send(new HitBuilders.EventBuilder().setCategory("Color Picker").setAction("Press Button").setLabel("colorPicker open button").build());
            return false;
        }
    };

    private View.OnClickListener dialogCancelClickListener = new View.OnClickListener() {
        public void onClick(View v) {
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

    private View.OnClickListener dialogDeleteCancelClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            itemDeleteDialog.dismiss();
        }
    };


    private View.OnClickListener dialogOkayClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            informationDialog.dismiss();
        }
    };


    private View.OnClickListener dialogDeleteDoneClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (itemDeleteDialogSwitch.equals("Memorize")) {
                deleteMemorizeItem(itemDeleteDialog.getItemId());
                memorizeListViewAdapter.removeItem(deleteItemPosition);
                memorizeListViewAdapter.notifyDataSetChanged();
            } else {
                deleteMustDoItem(itemDeleteDialog.getItemId());
                mustDoListViewAdapter.removeItem(deleteItemPosition);
                mustDoListViewAdapter.notifyDataSetChanged();
            }
            itemDeleteDialog.dismiss();
        }
    };

    private View.OnClickListener dialogColorPickerCancelClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            colorPickerDialog.dismiss();
        }
    };

    private View.OnClickListener dialogColorPickerDoneClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            int color = colorPickerDialog.getColor();
            String stringColor = String.format("#%06X", (0xFFFFFF & color));

            colorButtons[buttonIndex].setBackgroundColor(Color.parseColor(stringColor));

            colorPickerEditors[buttonIndex] = colorPickerPreferences[buttonIndex].edit();
            colorPickerEditors[buttonIndex].putString("color_" + Integer.toString(buttonIndex), stringColor);
            colorPickerEditors[buttonIndex].apply();
            colorPickerDialog.dismiss();
            tracker.send(new HitBuilders.EventBuilder().setCategory("Color Picker").setAction("Press Button").setLabel("colorPicker change color button").build());
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
                        WidgetProvider widgetProvider = new WidgetProvider();
                        widgetProvider.updateWidget(MainActivity.this);
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

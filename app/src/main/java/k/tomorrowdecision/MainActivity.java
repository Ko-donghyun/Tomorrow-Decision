package k.tomorrowdecision;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.app.Service;
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
import android.widget.CompoundButton;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import k.tomorrowdecision.DataBase.MemorizeDataBase;
import k.tomorrowdecision.DataBase.MustDoDataBase;
import k.tomorrowdecision.DataBase.TodoDataBase;
import k.tomorrowdecision.Dialog.ColorPickerDialog;
import k.tomorrowdecision.Dialog.InformationDialog;
import k.tomorrowdecision.Dialog.ItemAddDialog;
import k.tomorrowdecision.Dialog.ItemDeleteDialog;
import k.tomorrowdecision.Dialog.ListViewThemePickerDialog;
import k.tomorrowdecision.Dialog.MyTimeZoneDialog;
import k.tomorrowdecision.Dialog.SettingDialog;
import k.tomorrowdecision.Dialog.ThemePickerDialog;
import k.tomorrowdecision.Item.ThemeItem;
import k.tomorrowdecision.ListViewAdapter.TextListViewAdapter;
import k.tomorrowdecision.ListViewAdapter.TodoListViewAdapter;
import k.tomorrowdecision.Widget.ThemeWidgetProvider;
import k.tomorrowdecision.Widget.WidgetProvider;

public class MainActivity extends AppCompatActivity {
    InputMethodManager imm;
    // 할 일 디비
    TodoDataBase todoDataBase;
    final String todoDBName = "Todo.db";
    final int todoDBVersion = 3;
    MemorizeDataBase memorizeDataBase;
    final String memorizeDBName = "memorize.db";
    final int memorizeDBVersion = 1;
    MustDoDataBase mustDoDataBase;
    final String mustDoDBName = "mustDo.db";
    final int mustDoDBVersion = 1;

    ViewFlipper mainViewFlipper;
    ViewFlipper editItemViewFlipper;

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

    TextView successiveTodo;
    TextView successiveNumberTextView;
    int successiveNumber;

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

    int[] importanceButtonArray = {
            R.id.importance_1, R.id.importance_2, R.id.importance_3, R.id.importance_4, R.id.importance_5
    };
    String[] importanceColorCodeArray = new String[10];
    public static SharedPreferences[] importanceColorPreferences = new SharedPreferences[10];
    public static SharedPreferences.Editor[] importanceColorEditors = new SharedPreferences.Editor[10];
    Button importanceButtons[] = new Button[5];
    int importanceIndex;

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
    ThemePickerDialog themePickerDialog;
    MyTimeZoneDialog myTimeZoneDialog;
    ListViewThemePickerDialog listViewThemePickerDialog;

    private InformationDialog informationDialog;

    private ImageView settingButton;
    private SettingDialog settingDialog;

    Tracker tracker;

    public static SharedPreferences themePreference;
    public static SharedPreferences.Editor themeEditor;
    private int theme;

    public static SharedPreferences listViewThemePreference;
    public static SharedPreferences.Editor listViewThemeEditor;
    private String listViewTheme;

    public static SharedPreferences listViewThemeBackgroundColorPreference;
    public static SharedPreferences.Editor listViewThemeBackgroundColorEditor;
    String listViewThemeBackgroundColor;
    LinearLayout listViewBackground;

    public static SharedPreferences timeZonePreference;
    public static SharedPreferences.Editor timeZoneEditor;
    private int timeZone;

    public static SharedPreferences alarmPreference;
    public static SharedPreferences.Editor alarmEditor;
    private boolean alarm;
    MyAlarmManager myAlarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        themePreference = getSharedPreferences("theme", Activity.MODE_PRIVATE);
        theme = themePreference.getInt("theme", 1);

        listViewThemePreference = getSharedPreferences("listViewTheme", Activity.MODE_PRIVATE);
        listViewTheme = listViewThemePreference.getString("listViewTheme", "normal");

        listViewThemeBackgroundColorPreference = getSharedPreferences("listViewThemeBackgroundColor", Activity.MODE_PRIVATE);
        listViewThemeBackgroundColor = listViewThemeBackgroundColorPreference.getString("listViewThemeBackgroundColor", "#e9e9e9");

        listViewBackground = (LinearLayout) findViewById(R.id.todo_list_background);
        listViewBackground.setBackgroundColor(Color.parseColor(listViewThemeBackgroundColor));

        timeZonePreference = getSharedPreferences("timeZone", Activity.MODE_PRIVATE);
        timeZone = timeZonePreference.getInt("timeZone", 22);

        alarmPreference = getSharedPreferences("alarm", Activity.MODE_PRIVATE);
        alarm = alarmPreference.getBoolean("alarm", false);
        myAlarmManager = new MyAlarmManager(getApplicationContext());

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        tracker = application.getDefaultTracker();
        tracker.setScreenName("MainActivity");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        // 데이터 베이스 가져오기
        todoDataBase = new TodoDataBase(this, todoDBName, null, todoDBVersion);
        memorizeDataBase = new MemorizeDataBase(this, memorizeDBName, null, memorizeDBVersion);
        mustDoDataBase = new MustDoDataBase(this, mustDoDBName, null, mustDoDBVersion);

        todoListView = (ListView) findViewById(R.id.todo_list_view);
        memorizeListView = (ListView) findViewById(R.id.memorize_list_view);
        mustDoListView = (ListView) findViewById(R.id.must_do_list_view);
        mainViewFlipper = (ViewFlipper) findViewById(R.id.main_view_flipper);
        editItemViewFlipper = (ViewFlipper) findViewById(R.id.edit_item_view_flipper);
        mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
        mainViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));

        for (int i = 0; i < 5; i++) {
            importanceButtons[i] = (Button) findViewById(importanceButtonArray[i]);
            importanceButtons[i].setOnClickListener(importanceChangeListener);
        }

        for (int i = 0; i < 5; i++) {
            importanceColorPreferences[i * 2] = getSharedPreferences("level_" + Integer.toString(i) + "_tg", Activity.MODE_PRIVATE);
            importanceColorPreferences[(i * 2) + 1] = getSharedPreferences("level_" + Integer.toString(i) + "_bg", Activity.MODE_PRIVATE);
            importanceColorCodeArray[(i * 2)] = importanceColorPreferences[(i * 2)].getString("level_" + Integer.toString(i) + "_tg", "#000000");
            importanceColorCodeArray[(i * 2) + 1] = importanceColorPreferences[(i * 2) + 1].getString("level_" + Integer.toString(i) + "_bg", "#FFFFFF");
        }

        for (int i = 0; i < 9; i++) {
            colorButtons[i] = (Button) findViewById(buttonArray[i]);
            colorButtons[i].setOnClickListener(colorPickerListener);
            colorButtons[i].setOnLongClickListener(changeColorPickerListener);
            colorPickerPreferences[i] = getSharedPreferences("color_" + Integer.toString(i), Activity.MODE_PRIVATE);
            colorButtons[i].setBackgroundColor(Color.parseColor(colorPickerPreferences[i].getString("color_" + Integer.toString(i), colorCodeArray[i])));
        }

        // Adapter 생성
        int listViewLayout = settingListViewLayout();
        todoListViewAdapter = new TodoListViewAdapter(getApplicationContext(), listViewLayout, theme, importanceColorCodeArray);

        memorizeListViewAdapter = new TextListViewAdapter();
        mustDoListViewAdapter = new TextListViewAdapter();
        getTodoData();
        getMemorizeData();
        getMustDoData();

        settingButton = (ImageView) findViewById(R.id.setting);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingDialog = new SettingDialog(MainActivity.this, okayClickListener, settingThemeClickListener, settingListViewThemeClickListener, settingMyTimeZoneClickListener, timeZone, settingAlarmClickListener, alarm);
                settingDialog.show();
            }
        });
        settingButton.setVisibility(View.VISIBLE);

        cancelButton = (ImageView) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainViewFlipper.getDisplayedChild() == 1) {
                    hideKeyboard(todo);
                    mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_right_in));
                    mainViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_right_out));
                    mainViewFlipper.showPrevious();
                    settingButton.setVisibility(View.VISIBLE);
                }
            }
        });

        doneButton = (ImageView) findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeZone = timeZonePreference.getInt("timeZone", 22);

                SimpleDateFormat hourFormat = new SimpleDateFormat("HH", Locale.getDefault());
                int hour = Integer.parseInt(hourFormat.format(new Date(System.currentTimeMillis())));

                if (clickFlag && isEditableTime(hour, timeZone) && mainViewFlipper.getDisplayedChild() == 1) {
                    clickFlag = false;
                    if (theme == 1) {

                        saveCustomTodo(itemPosition, timeText, successiveNumber);

                    } else {

                        saveImportanceTodo(itemPosition, timeText, successiveNumber);

                    }
                    myAlarmManager.Alarm(timeText, todoListViewAdapter.getItem(itemPosition).getTimeText(), todoListViewAdapter.getItem(itemPosition).getTodo(), itemPosition);

                    clickFlag = true;
                    settingButton.setVisibility(View.VISIBLE);
                } else {
                    informationDialog = new InformationDialog(MainActivity.this, dialogOkayClickListener, directSettingMyTimeZoneClickListener, getEditableTimeRange(timeZone));
                    informationDialog.show();
                }
            }
        });

        successiveTodo = (TextView) findViewById(R.id.successive_todo);
        successiveNumberTextView = (TextView) findViewById(R.id.successive_number);
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
                if (position <= 23) {
                    if (!todoListViewAdapter.getItem(position).getTodo().equals("")) {
                        String title = "Must Do";
                        String subTitle = "지난 일에 대해서 꼭 해야만 하는 일로 추가됩니다.";

                        itemAddDialogSwitch = title;
                        itemAddDialog = new ItemAddDialog(MainActivity.this, title, subTitle, dialogCancelClickListener, dialogDoneClickListener);
                        itemAddDialog.show();
                        itemAddDialog.setEditTextViewItem(todoListViewAdapter.getItem(position).getTodo());
                        itemAddDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                } else {
                    successiveNumberTextView.setText("+1");
                    successiveNumber = 1;

                    todoTime.setText(todoListViewAdapter.getItem(position).getTimeText());
                    todo.setText(todoListViewAdapter.getItem(position).getTodo());
                    timeText = todoListViewAdapter.getItem(position).getTime();
                    itemPosition = position;

                    theme = themePreference.getInt("theme", 1);
                    if (theme == 1) {
                        todoBackground.setBackgroundColor(Color.parseColor(todoListViewAdapter.getItem(position).getBackgroundColorCode()));
                        todo.setTextColor(Color.parseColor(todoListViewAdapter.getItem(position).getTextColorCode()));
                        editItemViewFlipper.setDisplayedChild(0);
                    } else {
                        todoBackground.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        todo.setTextColor(Color.parseColor("#000000"));
                        for (int i = 0; i < 5; i++) {
                            importanceButtons[i].setBackgroundColor(Color.parseColor("#FFFFFF"));
                        }
                        importanceIndex = todoListViewAdapter.getItem(position).getImportance();
                        importanceButtons[importanceIndex].setBackgroundColor(Color.parseColor("#EFEFEF"));
                        editItemViewFlipper.setDisplayedChild(1);
                    }
                    settingButton.setVisibility(View.INVISIBLE);
                    mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_left_in));
                    mainViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_left_out));
                    mainViewFlipper.showNext();
                    tracker.send(new HitBuilders.EventBuilder().setCategory("EditPage").setAction("Press Button").setLabel("Move EditPage").build());
                }
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


        successiveTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                successiveNumber++;
                if (successiveNumber > 9) {
                    successiveNumber = 1;
                }
                successiveNumberTextView.setText("+" + successiveNumber);
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
        Intent restartIntent = new Intent(MainActivity.this, LoadingActivity.class);
        startActivity(restartIntent);
        finish();
    }

    private int settingListViewLayout() {
        int listViewLayout;
        switch (listViewTheme) {
            case "padding":
                listViewLayout = R.layout.todo_list_item_padding;
                break;
            case "round":
                listViewLayout = R.layout.todo_list_item_round;
                break;
            default:
                listViewLayout = R.layout.todo_list_item;
                break;
        }
        return listViewLayout;
    }

    private void getTodoData() {
        SQLiteDatabase sqLiteDatabase = todoDataBase.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM todo", null);

        cursor.moveToFirst();
        do {
            Date data = new Date(Long.parseLong(cursor.getString(0)));
            String stringDate = simpleDateFormat.format(data);
            todoListViewAdapter.addItem(cursor.getString(0), stringDate, cursor.getString(1), cursor.getInt(2), cursor.getString(3), cursor.getString(4));
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

    private void saveCustomTodo(int itemPosition, String timeText, int successiveNumber) {
        Long time = Long.parseLong(timeText);

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
        for (int i = 0; i < successiveNumber; i++) {
            timeText = time.toString();
            todoDatabase.update("todo", values, "time='" + timeText + "'", null);
            todoListViewAdapter.updateCustomItem(itemPosition, todo.getText().toString(), textHexCode, backgroundHexCode);
            itemPosition++;
            time = time + 3600000;
            if (itemPosition >= 49) {
                break;
            }
        }
        todoDatabase.close();
        todoListViewAdapter.notifyDataSetChanged();
        hideKeyboard(todo);

        mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_right_in));
        mainViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_right_out));
        mainViewFlipper.showPrevious();
        tracker.send(new HitBuilders.EventBuilder().setCategory("EditPage").setAction("Press Button").setLabel("saveCustomTodo").build());
    }

    private void saveImportanceTodo(int itemPosition, String timeText, int successiveNumber) {
        Long time = Long.parseLong(timeText);

        SQLiteDatabase todoDatabase = todoDataBase.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("todo", todo.getText().toString());
        values.put("importance", importanceIndex);
        for (int i = 0; i < successiveNumber; i++) {
            timeText = time.toString();
            todoDatabase.update("todo", values, "time='" + timeText + "'", null);
            todoListViewAdapter.updateThemeItem(itemPosition, todo.getText().toString(), importanceIndex);
            itemPosition++;
            time = time + 3600000;
            if (itemPosition >= 49) {
                break;
            }
        }
        todoDatabase.close();
        todoListViewAdapter.notifyDataSetChanged();
        hideKeyboard(todo);

        mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_right_in));
        mainViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_right_out));
        mainViewFlipper.showPrevious();
        tracker.send(new HitBuilders.EventBuilder().setCategory("EditPage").setAction("Press Button").setLabel("saveImportanceTodo").build());
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

    Button.OnClickListener importanceChangeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            for (int i = 0; i < 5; i++) {
                importanceButtons[i].setBackgroundColor(Color.parseColor("#FFFFFF"));
                if (importanceButtonArray[i] == id) {
                    importanceIndex = i;
                }
            }
            v.setBackgroundColor(Color.parseColor("#EFEFEF"));
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

    private View.OnClickListener directSettingMyTimeZoneClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            informationDialog.dismiss();
            timeZone = timeZonePreference.getInt("timeZone", 22);
            myTimeZoneDialog = new MyTimeZoneDialog(MainActivity.this, myTimeZoneCloseClickListener, myTimeZoneApplyClickListener, timeZone);
            myTimeZoneDialog.show();
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


    private View.OnClickListener okayClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            settingDialog.dismiss();
        }
    };

    private View.OnClickListener themeCloseClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            themePickerDialog.dismiss();
        }
    };

    private View.OnClickListener listViewThemeCloseClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            listViewThemePickerDialog.dismiss();
        }
    };


    private View.OnClickListener listViewThemeApplyClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            listViewThemeBackgroundColor = listViewThemePickerDialog.getListViewThemeBackgroundColor();

            listViewThemeBackgroundColorEditor = listViewThemeBackgroundColorPreference.edit();
            listViewThemeBackgroundColorEditor.putString("listViewThemeBackgroundColor", listViewThemeBackgroundColor);
            listViewThemeBackgroundColorEditor.apply();

            listViewTheme = listViewThemePickerDialog.getListViewLayoutTheme();
            listViewThemeEditor = listViewThemePreference.edit();
            listViewThemeEditor.putString("listViewTheme", listViewTheme);
            listViewThemeEditor.apply();

            int listViewLayout = settingListViewLayout();
            todoListViewAdapter.updateListViewLayout(listViewLayout);

//            todoListViewAdapter = new TodoListViewAdapter(getApplicationContext(), listViewLayout, theme, importanceColorCodeArray);
            todoListView.setAdapter(todoListViewAdapter);
            todoListView.setSelection(21);
//            todoListViewAdapter.notifyDataSetChanged();

            listViewBackground.setBackgroundColor(Color.parseColor(listViewThemeBackgroundColor));

            listViewThemePickerDialog.dismiss();
        }
    };



    private View.OnClickListener themeApplyClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            ThemeItem themeItem = themePickerDialog.getSelectedThemeItem();
            if (themeItem.getUsable() == 1) {
                importanceColorCodeArray = themeItem.getImportanceColorCodes();

                themeEditor = themePreference.edit();
                themeEditor.putInt("theme", themePickerDialog.getSelectedTheme());
                themeEditor.apply();

                for (int i = 0; i < 5; i++) {
                    // 글자 색
                    importanceColorEditors[(i * 2)] = importanceColorPreferences[(i * 2)].edit();
                    importanceColorEditors[(i * 2)].putString("level_" + Integer.toString(i) + "_tg", importanceColorCodeArray[(i * 2)]);
                    importanceColorEditors[(i * 2)].apply();

                    // 배경 색
                    importanceColorEditors[(i * 2) + 1] = importanceColorPreferences[(i * 2) + 1].edit();
                    importanceColorEditors[(i * 2) + 1].putString("level_" + Integer.toString(i) + "_bg", importanceColorCodeArray[(i * 2) + 1]);
                    importanceColorEditors[(i * 2) + 1].apply();
                }
                todoListViewAdapter.updateTheme(themePickerDialog.getSelectedTheme(), importanceColorCodeArray);
                todoListViewAdapter.notifyDataSetChanged();
                themePickerDialog.dismiss();
                tracker.send(new HitBuilders.EventBuilder().setCategory("Theme Picker").setAction("Press Button").setLabel("themePicker apply theme").build());
            } else {
                Toast.makeText(MainActivity.this, "잠금 설정된 테마는 아직 풀 수 없습니다.", Toast.LENGTH_SHORT).show();
            }

        }
    };

    private View.OnClickListener settingThemeClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            settingDialog.dismiss();
            themePickerDialog = new ThemePickerDialog(MainActivity.this, themeCloseClickListener, themeApplyClickListener);
            themePickerDialog.show();
        }
    };

    private View.OnClickListener settingListViewThemeClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            settingDialog.dismiss();
            listViewThemePickerDialog = new ListViewThemePickerDialog(MainActivity.this, listViewTheme, listViewThemeBackgroundColor, listViewThemeCloseClickListener, listViewThemeApplyClickListener);
            listViewThemePickerDialog.show();
        }
    };

    private View.OnClickListener myTimeZoneCloseClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            myTimeZoneDialog.dismiss();
        }
    };

    private View.OnClickListener myTimeZoneApplyClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            timeZoneEditor = timeZonePreference.edit();
            timeZoneEditor.putInt("timeZone", myTimeZoneDialog.getNewTimeZone());
            timeZoneEditor.apply();
            tracker.send(new HitBuilders.EventBuilder().setCategory("setting").setAction("Press Button").setLabel("Change TimeZone").build());

            myTimeZoneDialog.dismiss();
        }
    };

    private View.OnClickListener settingMyTimeZoneClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            settingDialog.dismiss();
            timeZone = timeZonePreference.getInt("timeZone", 22);
            myTimeZoneDialog = new MyTimeZoneDialog(MainActivity.this, myTimeZoneCloseClickListener, myTimeZoneApplyClickListener, timeZone);
            myTimeZoneDialog.show();
        }
    };

    private CompoundButton.OnCheckedChangeListener settingAlarmClickListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                Toast.makeText(MainActivity.this, "지금부터 설정한 일정에 대해서 푸시가 등록됩니다.", Toast.LENGTH_SHORT).show();
                alarmEditor = alarmPreference.edit();
                alarmEditor.putBoolean("alarm", true);
                alarmEditor.apply();
                alarm = true;
            } else {
                Toast.makeText(MainActivity.this, "모든 일정이 해제되었습니다.", Toast.LENGTH_SHORT).show();
                alarmEditor = alarmPreference.edit();
                alarmEditor.putBoolean("alarm", false);
                alarmEditor.apply();
                alarm = false;
                myAlarmManager.AllAlarmCancel();
            }
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

    private boolean isEditableTime(int currentHour, int timeZone) {
        if (((timeZone + 21) % 24) > ((timeZone + 3) % 24)) {
            return (((timeZone + 21) % 24) <= currentHour || ((timeZone + 3) % 24) > currentHour);
        } else {
            return (((timeZone + 21) % 24) <= currentHour && ((timeZone + 3) % 24) > currentHour);
        }
    }

    private String getEditableTimeRange(int timeZone) {
        return Integer.toString((timeZone + 21) % 24) + ":00 ~ " + Integer.toString((timeZone + 3) % 24) + ":00";
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            //하드웨어 뒤로가기 버튼에 따른 이벤트 설정
            case KeyEvent.KEYCODE_BACK:
                if (mainViewFlipper.getDisplayedChild() == 1) {
                    settingButton.setVisibility(View.VISIBLE);
                    mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_right_in));
                    mainViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.push_right_out));
                    mainViewFlipper.showPrevious();
                } else {
                    long tempTime = System.currentTimeMillis();
                    long intervalTime = tempTime - backPressedTime;

                    if ( 0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime ) {
                        WidgetProvider widgetProvider = new WidgetProvider();
                        widgetProvider.updateWidget(MainActivity.this);
                        ThemeWidgetProvider themeWidgetProvider = new ThemeWidgetProvider();
                        themeWidgetProvider.updateWidget(MainActivity.this);
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

    public class MyAlarmManager {
        private Context context;
        PendingIntent[] sender = new PendingIntent[49];

        public MyAlarmManager(Context context) {
            this.context=context;
        }
        public void Alarm(String timeStamp, String timeText, String content, int itemPosition) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Calendar mCalendar = Calendar.getInstance();
            Intent intent;
            Long alarmTime;
            int notifyId;
            int index;

            if (alarm) {
                //알람 예약
                for (int i = 0; i < successiveNumber; i++) {
                    alarmTime = Long.parseLong(todoListViewAdapter.getItem(itemPosition + i).getTime());
                    notifyId = (int) (alarmTime / 3600000);
                    index = notifyId % 49;

                    intent = new Intent("k.tomorrowdecision.ALARM_START");
                    intent.putExtra("timeText", timeText);
                    intent.putExtra("index", index);
                    intent.putExtra("content", content);

                    sender[index] = PendingIntent.getBroadcast(getApplicationContext(), index, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    mCalendar.setTimeInMillis(alarmTime);

                    if (System.currentTimeMillis() > alarmTime || content.equals("")) {
                        System.out.println("알람 취소 : " + index);
                        am.cancel(sender[index]);
                    } else {
                        System.out.println("알람 예약 : " + index);
                        am.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), sender[index]);
                    }
                }
            }
        }
        public void AllAlarmCancel() {
            Intent intent = new Intent("k.tomorrowdecision.ALARM_START");

            for (int i = 0; i < 49; i++) {
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                sender[i] = PendingIntent.getBroadcast(getApplicationContext(), i, intent, 0);
                am.cancel(sender[i]);
            }
        }
    }
}

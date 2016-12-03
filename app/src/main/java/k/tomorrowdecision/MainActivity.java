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

        todoListView = (ListView) findViewById(R.id.todo_list_view);
        memorizeListView = (ListView) findViewById(R.id.memorize_list_view);
        mustDoListView = (ListView) findViewById(R.id.must_do_list_view);
        mainViewFlipper = (ViewFlipper) findViewById(R.id.main_view_flipper);
        mainViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
        mainViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));

        // Adapter 생성
        todoListViewAdapter = new TodoListViewAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        todoListView.setAdapter(todoListViewAdapter);

        // 첫 번째 아이템 추가.

        todoListViewAdapter.addItem("12월 09일 (금) 07시", "몇 글자 까지 가능한지를 판단하자", "#FFf0f0", "#FF4F6F") ;
        todoListViewAdapter.addItem("12월 09일 (금) 08시", "점심 식사", "#F20f00", "#FFFF2F") ;
        todoListViewAdapter.addItem("12월 09일 (금) 09시", "점심 식사", "#FF0000", "#3FFFFF") ;
        todoListViewAdapter.addItem("12월 09일 (금) 10시", "1234567890", "#2F0080", "#6FFF3F") ;
        todoListViewAdapter.addItem("12월 09일 (금) 11시", "ABCDEFGHIJKLMNOPQRSTUVWXYZ", "#110030", "#4F3FFF") ;
        todoListViewAdapter.addItem("12월 09일 (금) 12시", "아야어여오요우유으이키티칲피미니이리히비지디기시", "#220100", "#FFF4FF") ;
        todoListViewAdapter.addItem("12월 09일 (금) 13시", "가나다라마바사아자차카타파하", "#000000", "#FFFFFF") ;
        todoListViewAdapter.addItem("12월 09일 (금) 15시", "가나다라마바사아자차카타파하", "#00403F", "#FF1F1F") ;
        todoListViewAdapter.addItem("12월 09일 (금) 16시", "일과 시작", "#0AA0FF", "#5FFF3F") ;
        todoListViewAdapter.addItem("12월 09일 (금) 17시", "일과 시작", "#02E0FF", "#3F44FF") ;
        todoListViewAdapter.addItem("12월 09일 (금) 18시", "일과 시작", "#0440FF", "#4F4F8F") ;
        todoListViewAdapter.addItem("12월 09일 (금) 19시", "일과 시작", "#4030FF", "#FF5F6F") ;

        todoListViewAdapter.addItem("12월 09일 (금) 08시", "점심 식사", "#000000", "#FFFFFF") ;
        todoListViewAdapter.addItem("12월 09일 (금) 08시", "점심 식사", "#000000", "#FFFFFF") ;
        todoListViewAdapter.addItem("12월 09일 (금) 08시", "점심 식사", "#000000", "#FFFFFF") ;
        todoListViewAdapter.addItem("12월 09일 (금) 08시", "점심 식사", "#000000", "#FFFFFF") ;
        todoListViewAdapter.addItem("12월 09일 (금) 08시", "점심 식사", "#000000", "#FFFFFF") ;
        todoListViewAdapter.addItem("12월 09일 (금) 08시", "점심 식사", "#000000", "#FFFFFF") ;
        todoListViewAdapter.addItem("12월 09일 (금) 08시", "점심 식사", "#000000", "#FFFFFF") ;
        todoListViewAdapter.addItem("12월 09일 (금) 13시", "점심 식사", "#000000", "#FFFFFF") ;
        todoListViewAdapter.addItem("12월 09일 (금) 08시", "점심 식사", "#000000", "#FFFFFF") ;
        todoListViewAdapter.addItem("12월 09일 (금) 08시", "점심 식사", "#000000", "#FFFFFF") ;
        todoListViewAdapter.addItem("12월 09일 (금) 08시", "점심 식사", "#000000", "#FFFFFF") ;
        todoListViewAdapter.addItem("12월 09일 (금) 08시", "점심 식사", "#000000", "#FFFFFF") ;
        todoListViewAdapter.addItem("12월 09일 (금) 08시", "점심 식사", "#000000", "#FFFFFF") ;
        todoListViewAdapter.addItem("12월 09일 (금) 08시", "점심 식사", "#000000", "#FFFFFF") ;

        todoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get Item
                mainViewFlipper.showNext();
            }
        });
        todoListView.setSelection(4);
    }
}

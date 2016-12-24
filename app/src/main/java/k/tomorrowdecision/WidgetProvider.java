package k.tomorrowdecision;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import k.tomorrowdecision.DataBase.TodoDataBase;
import k.tomorrowdecision.Item.TodoItem;

public class WidgetProvider extends AppWidgetProvider {

    TodoDataBase todoDataBase;
    final String todoDBName = "Todo.db";
    final int todoDBVersion = 2;

    public static SharedPreferences lastGetInTimePreference;
    public static SharedPreferences.Editor lastGetInTimeEditor;

    public static String PENDING_ACTION = "k.tomorrowdecision.UPDATE";

    /**
     * 브로드캐스트를 수신할때, Override된 콜백 메소드가 호출되기 직전에 호출됨
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        super.onReceive(context, intent);
        String action = intent.getAction();
        AppWidgetManager widgetMgr = AppWidgetManager.getInstance(context);
        // RENEW
        if (action != null && action.equals(PENDING_ACTION)) {
            int id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            int[] ids = new int[1];
            ids[0] = id;
            this.onUpdate(context, widgetMgr, ids);
        }

    }

    /**
     * 위젯을 갱신할때 호출됨
     *
     * 주의 : Configure Activity를 정의했을때는 위젯 등록시 처음 한번은 호출이 되지 않습니다
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));

        updateWidgetAll(context, appWidgetManager, appWidgetIds);
    }

    public void updateWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
        this.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    /**
     * 위젯이 처음 생성될때 호출됨
     *
     * 동일한 위젯이 생성되도 최초 생성때만 호출됨
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    /**
     * 위젯의 마지막 인스턴스가 제거될때 호출됨
     *
     * onEnabled()에서 정의한 리소스 정리할때
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    /**
     * 위젯이 사용자에 의해 제거될때 호출됨
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    public Long settingTodoDataBase(Context context) {
        long lastGetInTime;
        long currentTime;

        todoDataBase = new TodoDataBase(context, todoDBName, null, todoDBVersion);

        currentTime = System.currentTimeMillis();
        lastGetInTimePreference = context.getSharedPreferences("lastGetInTime", Activity.MODE_PRIVATE);
        lastGetInTime = lastGetInTimePreference.getLong("lastGetInTime", currentTime);

        lastGetInTimeEditor = lastGetInTimePreference.edit();
        lastGetInTimeEditor.putLong("lastGetInTime", currentTime);
        lastGetInTimeEditor.apply();

        SQLiteDatabase todoDatabase = todoDataBase.getWritableDatabase();

        try {
            todoDatabase.beginTransaction();
            // affectedRowCount 의 최대 값은 49
            int affectedRowCount = todoDatabase.delete("todo", "time<?", new String[]{Long.toString(currentTime - 25 * 3600000)});

            // 영향 받은 Row 수가 최대 치 미만이면
            if (affectedRowCount > 0 && affectedRowCount < 49) {
                ContentValues values = new ContentValues();
                for (int i = 0; i < affectedRowCount; i++) {
                    // 시간은 지워진 갯수 만큼 다시 복구 시켜줘야 하는 시점 부터 생성
                    // 마지막 실행 시간의 25시간 이후의 시간부터 지워진 시간만큼 생성해줘야 함
                    values.put("time", ((lastGetInTime / (3600000)) * 3600000) + (25 + i) * 3600000);
                    values.put("todo", "");
                    values.put("textColorCode", "#000000");
                    values.put("backgroundColorCode", "#FFFFFF");
                    todoDatabase.insert("todo", null, values);
                }
                todoDatabase.setTransactionSuccessful();
            }

            // 영향 받은 Row 수가 최대 치 이상이면
            if (affectedRowCount >= 49) {
                ContentValues values = new ContentValues();
                for (int i = 0; i < affectedRowCount; i++) {
                    // 기존의 시간은 다 필요없으니 모두 다 지워졌다고 생각하고
                    // 앱 처음 설치할 때 처럼 현재 시간의 -24시간 이전 부터
                    // 현재 시간의 +25시간 이후 까지 생성
                    values.put("time", (((currentTime / 3600000) * 3600000)) - 86400000 + 3600000 * i);
                    values.put("todo", "");
                    values.put("textColorCode", "#000000");
                    values.put("backgroundColorCode", "#FFFFFF");
                    todoDatabase.insert("todo", null, values);
                }
                todoDatabase.setTransactionSuccessful();
            }

        } finally {
            todoDatabase.endTransaction();
        }

        todoDatabase.close();
        return currentTime;
    }

    public ArrayList<TodoItem> settingTodo(Context context, Long currentTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM월 dd일 (E) HH시", Locale.getDefault());

        ArrayList<TodoItem> listViewItemList = new ArrayList<TodoItem>();
        // 현재 시간의 디비 정보 가져오기
        SQLiteDatabase sqLiteDatabase = todoDataBase.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM todo WHERE time IN (?, ?, ?)",  new String[] {
                Long.toString((currentTime / 3600000) * 3600000 - 3600000),
                Long.toString((currentTime / 3600000) * 3600000),
                Long.toString((currentTime / 3600000) * 3600000 + 3600000),
        });

        cursor.moveToFirst();
        do {
            Date data = new Date(Long.parseLong(cursor.getString(0)));
            String stringDate = simpleDateFormat.format(data);

            TodoItem item = new TodoItem();

            item.setTime(cursor.getString(0));
            item.setTimeText(stringDate);
            item.setTodo(cursor.getString(1));
            item.setTextColorCode(cursor.getString(2));
            item.setBackgroundColorCode(cursor.getString(3));

            listViewItemList.add(item);
        } while( cursor.moveToNext() );

        cursor.close();
        sqLiteDatabase.close();

        return listViewItemList;
    }

    public void settingTodoView(Context context, AppWidgetManager appWidgetManager, int appWidgetId, ArrayList<TodoItem> listViewItemList) {
        // RemoteViews를 이용해 Text설정
        RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout_2by2);
        updateViews.setTextViewText(R.id.todo_item_1, listViewItemList.get(0).getTodo());
        updateViews.setTextColor(R.id.todo_item_1, Color.parseColor("#9A9A9A"));
        updateViews.setInt(R.id.item_layout_1, "setBackgroundColor", Color.parseColor("#D4D4D4"));
        updateViews.setTextViewText(R.id.time_text_2, listViewItemList.get(1).getTimeText());
        updateViews.setTextColor(R.id.time_text_2, Color.parseColor(listViewItemList.get(1).getTextColorCode()));
        updateViews.setTextViewText(R.id.todo_item_2, listViewItemList.get(1).getTodo());
        updateViews.setTextColor(R.id.todo_item_2, Color.parseColor(listViewItemList.get(1).getTextColorCode()));
        updateViews.setInt(R.id.item_layout_2, "setBackgroundColor", Color.parseColor(listViewItemList.get(1).getBackgroundColorCode()));
        updateViews.setTextViewText(R.id.todo_item_3, listViewItemList.get(2).getTodo());
        updateViews.setTextColor(R.id.todo_item_3, Color.parseColor(listViewItemList.get(2).getTextColorCode()));
        updateViews.setInt(R.id.item_layout_3, "setBackgroundColor", Color.parseColor(listViewItemList.get(2).getBackgroundColorCode()));

        // 위젯 업데이트 인텐트 추가
        Intent intent = new Intent(context, WidgetProvider.class);
        intent.setAction(PENDING_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        updateViews.setOnClickPendingIntent(R.id.update, pendingIntent);

        // 앱으로 진입하는 버튼 추가
        Intent intoIntent = new Intent(context, LoadingActivity.class);
        PendingIntent pe = PendingIntent.getActivity(context, 0, intoIntent, 0);
        updateViews.setOnClickPendingIntent(R.id.widget_layout, pe);

        // 업데이트 세팅
        appWidgetManager.updateAppWidget(appWidgetId, updateViews);
    }

    public void updateWidgetAll(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // 디비 정리
        Long currentTime = settingTodoDataBase(context);

        // 디비에서 세가지 아이템 받아오기
        ArrayList<TodoItem> listViewItemList = settingTodo(context, currentTime);

        // 뷰에 반영
        for (int i = 0; i < appWidgetIds.length; i++) {
            settingTodoView(context, appWidgetManager, appWidgetIds[i], listViewItemList);
        }
    }
}
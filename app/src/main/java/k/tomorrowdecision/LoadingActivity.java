package k.tomorrowdecision;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import k.tomorrowdecision.DataBase.TodoDataBase;

public class LoadingActivity extends Activity {

    Intent targetIntent;

    TodoDataBase todoDataBase;
    final String todoDBName = "Todo.db";
    final int todoDBVersion = 2;

    public static SharedPreferences lastGetInTimePreference;
    public static SharedPreferences.Editor lastGetInTimeEditor;

    long lastGetInTime;
    long currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_page);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        todoDataBase = new TodoDataBase(this, todoDBName, null, todoDBVersion);

        currentTime = System.currentTimeMillis();
        lastGetInTimePreference = getSharedPreferences("lastGetInTime", Activity.MODE_PRIVATE);
        lastGetInTime = lastGetInTimePreference.getLong("lastGetInTime", currentTime);

        lastGetInTimeEditor = lastGetInTimePreference.edit();
        lastGetInTimeEditor.putLong("lastGetInTime", currentTime);
        lastGetInTimeEditor.apply();

        TrimTodoDataBase trimTodoDataBase = new TrimTodoDataBase();
        trimTodoDataBase.execute();
    }

    public class TrimTodoDataBase extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            settingTodoData();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            startActivityWithDelay();
        }
    }

    public void startActivityWithDelay(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            public void run() {
                targetIntent = new Intent(LoadingActivity.this, MainActivity.class);
                startActivity(targetIntent);
                finish();
            }
        }, 1000);
    }

    private void settingTodoData() {
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
                    values.put("importance", 0);
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
                    values.put("importance", 0);
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
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }
}

package k.tomorrowdecision.ListViewAdapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import k.tomorrowdecision.R;
import k.tomorrowdecision.Item.TodoItem;

public class TodoListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<TodoItem> listViewItemList = new ArrayList<TodoItem>();
    private int theme;
    private String[] importanceColorCodes = new String[10];

    // TodoListViewAdapter의 생성자
    public TodoListViewAdapter() {

    }

    public TodoListViewAdapter(int theme, String[] importanceColors) {
        updateTheme(theme, importanceColors);
    }

    public String[] getImportanceColorCodes() {
        return importanceColorCodes;
    }

    public String getImportanceColorCode(int index) {
        return importanceColorCodes[index];
    }

    public void setImportanceColorCodes(String[] importanceColorCodes) {
        this.importanceColorCodes = importanceColorCodes;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴 : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴 : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        // "todo_list_item" Layout을 inflate하여 convertView 참조 획득
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.todo_list_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView time = (TextView) convertView.findViewById(R.id.time_text);
        TextView todo = (TextView) convertView.findViewById(R.id.todo_item);
        LinearLayout itemLayout = (LinearLayout) convertView.findViewById(R.id.item_layout);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        TodoItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        time.setText(listViewItem.getTimeText().substring(12, 15));
        todo.setText(listViewItem.getTodo());
        time.setTextSize(14);
        todo.setTextSize(20);

        if (theme == 1) {
            if (position < 24) {
                todo.setTextColor(Color.parseColor("#9A9A9A"));
                time.setTextColor(Color.parseColor("#9A9A9A"));
                itemLayout.setBackgroundColor(Color.parseColor("#D4D4D4"));
            } else {
                todo.setTextColor(Color.parseColor(listViewItem.getTextColorCode()));
                time.setTextColor(Color.parseColor(listViewItem.getTextColorCode()));
                itemLayout.setBackgroundColor(Color.parseColor(listViewItem.getBackgroundColorCode()));
            }
            if (position == 24) {
                time.setText(listViewItem.getTimeText());
                time.setTextSize(20);
                todo.setTextSize(40);
            }
        } else {
            if (position < 24) {
                todo.setTextColor(Color.parseColor("#9A9A9A"));
                time.setTextColor(Color.parseColor("#9A9A9A"));
                itemLayout.setBackgroundColor(Color.parseColor("#D4D4D4"));
            } else {
                todo.setTextColor(Color.parseColor(importanceColorCodes[(listViewItem.getImportance() * 2)]));
                time.setTextColor(Color.parseColor(importanceColorCodes[(listViewItem.getImportance() * 2)]));
                itemLayout.setBackgroundColor(Color.parseColor(importanceColorCodes[(listViewItem.getImportance() * 2) + 1]));
                if (position == 24) {
                    time.setText(listViewItem.getTimeText());
                    time.setTextSize(20);
                    todo.setTextSize(40);
                }
            }
        }
        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴 : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public TodoItem getItem(int position) {
        return listViewItemList.get(position);
    }

    // 아이템 데이터 추가를 위한 함수
    public void addItem(String time, String timeText, String todo, int importance, String textColorCode, String backgroundColorCode) {
        TodoItem item = new TodoItem();

        item.setTime(time);
        item.setTimeText(timeText);
        item.setTodo(todo);
        item.setImportance(importance);
        item.setTextColorCode(textColorCode);
        item.setBackgroundColorCode(backgroundColorCode);

        listViewItemList.add(item);
    }

    // 아이템 데이터 업데이트를 위한 함수
    public void updateCustomItem(int index, String todo, String textColorCode, String backgroundColorCode) {
        listViewItemList.get(index).setTodo(todo);
        listViewItemList.get(index).setTextColorCode(textColorCode);
        listViewItemList.get(index).setBackgroundColorCode(backgroundColorCode);
    }

    // 아이템 데이터 업데이트를 위한 함수
    public void updateThemeItem(int index, String todo, int importance) {
        listViewItemList.get(index).setTodo(todo);
        listViewItemList.get(index).setImportance(importance);
    }

    // 아이템 테마 변수 설정을 위한 함수
    public void updateTheme(int theme, String[] importanceColors) {
        this.theme = theme;
        setImportanceColorCodes(importanceColors);
    }
}
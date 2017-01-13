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

    private Context mContext;
    private LayoutInflater mInflater;
    private int mLayout;

    // TodoListViewAdapter의 생성자
    public TodoListViewAdapter() {

    }

    public TodoListViewAdapter(Context context, int layout, int theme, String[] importanceColors) {
        this.mContext = context;
        this.mLayout = layout;
        this.mInflater = (LayoutInflater) context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
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

        View view = convertView;
        ViewHolder viewHolder = null;
        // 캐시된 뷰가 없을 경우 새로 생성하고 뷰홀더를 생성한다
        if (view == null) {
            view = mInflater.inflate(mLayout, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.itemLayout = (LinearLayout) view.findViewById(R.id.item_layout);
            viewHolder.time = (TextView) view.findViewById(R.id.time_text);
            viewHolder.todo = (TextView) view.findViewById(R.id.todo_item);

            if (mLayout == R.layout.todo_list_item_round)
                viewHolder.lineText = (TextView) view.findViewById(R.id.line_text);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        TodoItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영

        viewHolder.todo.setText(listViewItem.getTodo());
        viewHolder.todo.setTextSize(16);
        viewHolder.time.setText(listViewItem.getTimeText().substring(12, 15));
        if (mLayout == R.layout.todo_list_item_round) {
            viewHolder.time.setTextSize(20);
        } else {
            viewHolder.time.setTextSize(14);
        }

        // 캐시된 뷰가 있을 경우 저장된 뷰홀더를 사용한다

        if (theme == 1) {
            if (position < 24) {
                viewHolder.todo.setTextColor(Color.parseColor("#9A9A9A"));
                viewHolder.time.setTextColor(Color.parseColor("#9A9A9A"));
                viewHolder.itemLayout.setBackgroundColor(Color.parseColor("#D4D4D4"));
                if (mLayout == R.layout.todo_list_item_round)
                    viewHolder.lineText.setBackgroundColor(Color.parseColor("#9A9A9A"));
            } else {
                viewHolder.todo.setTextColor(Color.parseColor(listViewItem.getTextColorCode()));
                viewHolder.time.setTextColor(Color.parseColor(listViewItem.getTextColorCode()));
                viewHolder.itemLayout.setBackgroundColor(Color.parseColor(listViewItem.getBackgroundColorCode()));
                if (mLayout == R.layout.todo_list_item_round)
                    viewHolder.lineText.setBackgroundColor(Color.parseColor(listViewItem.getTextColorCode()));
                if (position == 24) {
                    // 현재 시간 위치
                }
            }
        } else {
            if (position < 24) {
                viewHolder.todo.setTextColor(Color.parseColor("#9A9A9A"));
                viewHolder.time.setTextColor(Color.parseColor("#9A9A9A"));
                viewHolder.itemLayout.setBackgroundColor(Color.parseColor("#D4D4D4"));
                if (mLayout == R.layout.todo_list_item_round)
                    viewHolder.lineText.setBackgroundColor(Color.parseColor("#9A9A9A"));
            } else {
                viewHolder.todo.setTextColor(Color.parseColor(importanceColorCodes[(listViewItem.getImportance() * 2)]));
                viewHolder.time.setTextColor(Color.parseColor(importanceColorCodes[(listViewItem.getImportance() * 2)]));
                viewHolder.itemLayout.setBackgroundColor(Color.parseColor(importanceColorCodes[(listViewItem.getImportance() * 2) + 1]));
                if (mLayout == R.layout.todo_list_item_round)
                    viewHolder.lineText.setBackgroundColor(Color.parseColor(importanceColorCodes[(listViewItem.getImportance() * 2)]));
                if (position == 24) {
                    // 현재 시간 위치
                }
            }
        }

        return view;
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

    public static class ViewHolder {
        public LinearLayout itemLayout;
        public TextView todo;
        public TextView time;
        public TextView lineText;
    }
}
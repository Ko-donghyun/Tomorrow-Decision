package k.tomorrowdecision.ListViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import k.tomorrowdecision.Item.ThemeItem;
import k.tomorrowdecision.R;
import k.tomorrowdecision.Item.TextItem;

public class ThemeListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ThemeItem> themeItemList = new ArrayList<ThemeItem>();

    // TextListViewAdapter 생성자
    public ThemeListViewAdapter() {

    }

    // Adapter에 사용되는 데이터의 개수를 리턴 : 필수 구현
    @Override
    public int getCount() {
        return themeItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴 : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        // "todo_list_item" Layout을 inflate하여 convertView 참조 획득
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.theme_list_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView subTitle = (TextView) convertView.findViewById(R.id.sub_title);
        ImageView usable = (ImageView) convertView.findViewById(R.id.usable);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ThemeItem themeItem = themeItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        title.setText(themeItem.getTitle());
        subTitle.setText(themeItem.getSubTitle());
        if (themeItem.getUsable() == 1) {
            usable.setVisibility(View.INVISIBLE);
        } else {
            usable.setVisibility(View.VISIBLE);
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
    public ThemeItem getItem(int position) {
        return themeItemList.get(position);
    }

    // 아이템 데이터 추가를 위한 함수
    public void addItem(long id, String title, String subTitle, String[] importanceColorCodes, int usable) {
        ThemeItem item = new ThemeItem();

        item.setId(id);
        item.setTitle(title);
        item.setSubTitle(subTitle);
        item.setImportanceColorCodes(importanceColorCodes);
        item.setUsable(usable);

        themeItemList.add(item);
    }

    public void beUsavleThemeItem(int index) {
        themeItemList.get(index).setUsable(1);
    }
}
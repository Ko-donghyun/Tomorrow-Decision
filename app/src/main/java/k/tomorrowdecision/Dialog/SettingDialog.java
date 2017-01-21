package k.tomorrowdecision.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import k.tomorrowdecision.R;

public class SettingDialog extends Dialog {

    private View.OnClickListener settingThemeClickListener;
    private View.OnClickListener okayClickListener;
    private View.OnClickListener settingMyTimeZoneClickListener;
    private View.OnClickListener settingListViewThemeClickListener;
    private int editableTime;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.setting_dialog);

        TextView myEditableTime = (TextView) findViewById(R.id.my_editable_time);
        myEditableTime.setText("(현재 " + Integer.toString(editableTime) + "시)");

        LinearLayout settingTheme = (LinearLayout) findViewById(R.id.setting_theme);
        LinearLayout settingListViewTheme = (LinearLayout) findViewById(R.id.setting_layout_theme);
        LinearLayout settingMyTimeZone = (LinearLayout) findViewById(R.id.setting_my_time_zone);
        Button okayButton = (Button) findViewById(R.id.okay_button);
        SwitchCompat pushSwitch = (SwitchCompat) findViewById(R.id.push_switch);

        settingMyTimeZone.setOnClickListener(settingMyTimeZoneClickListener);
        settingTheme.setOnClickListener(settingThemeClickListener);
        settingListViewTheme.setOnClickListener(settingListViewThemeClickListener);
        okayButton.setOnClickListener(okayClickListener);
        pushSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//                    Toast.makeText(context, Boolean.toString(isChecked), Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(context, Boolean.toString(isChecked), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public SettingDialog(Context context, View.OnClickListener okayClickListener, View.OnClickListener settingThemeClickListener,
                         View.OnClickListener settingListViewThemeClickListener, View.OnClickListener settingMyTimeZoneClickListener, int editableTime) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.editableTime = editableTime;
        this.settingThemeClickListener = settingThemeClickListener;
        this.settingListViewThemeClickListener = settingListViewThemeClickListener;
        this.settingMyTimeZoneClickListener = settingMyTimeZoneClickListener;
        this.okayClickListener = okayClickListener;
    }
}
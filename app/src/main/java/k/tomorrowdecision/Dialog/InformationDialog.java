package k.tomorrowdecision.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import k.tomorrowdecision.R;

public class InformationDialog extends Dialog {

    private View.OnClickListener okayClickListener;
    private View.OnClickListener directSettingMyTimeZoneClickListener;
    private String editableTimeRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.information_dialog);

        TextView editableTime = (TextView) findViewById(R.id.editable_time);
        editableTime.setText(editableTimeRange);

        Button closeButton = (Button) findViewById(R.id.dialog_okay);
        closeButton.setOnClickListener(okayClickListener);

        Button settingMyTimeZoneButton = (Button) findViewById(R.id.direct_setting_my_time_zone);
        settingMyTimeZoneButton.setOnClickListener(directSettingMyTimeZoneClickListener);
    }

    public InformationDialog(Context context, View.OnClickListener okayClickListener,
                             View.OnClickListener directSettingMyTimeZoneClickListener, String editableTimeRange) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.okayClickListener = okayClickListener;
        this.directSettingMyTimeZoneClickListener = directSettingMyTimeZoneClickListener;
        this.editableTimeRange = editableTimeRange;
    }



}

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
import android.widget.Toast;

import k.tomorrowdecision.R;

public class SettingDialog extends Dialog {

    private View.OnClickListener settingThemeClickListener;
    private View.OnClickListener okayClickListener;
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

        ImageView settingThemeButton = (ImageView) findViewById(R.id.setting_theme);
        Button okayButton = (Button) findViewById(R.id.okay_button);
        SwitchCompat pushSwitch = (SwitchCompat) findViewById(R.id.push_switch);

        settingThemeButton.setOnClickListener(settingThemeClickListener);
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

    public SettingDialog(Context context, View.OnClickListener okayClickListener, View.OnClickListener settingThemeClickListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.settingThemeClickListener = settingThemeClickListener;
        this.okayClickListener = okayClickListener;
    }
}
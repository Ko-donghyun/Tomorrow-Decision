package k.tomorrowdecision.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import k.tomorrowdecision.R;

public class ListViewThemePickerDialog extends Dialog {

    private View.OnClickListener listViewThemeCloseClickListener;
    private View.OnClickListener listViewThemeApplyClickListener;
    ColorPickerDialog colorPickerDialog;
    private Context context;
    private String listViewTheme;
    private String listViewThemeBackgroundColor;
    TextView normalTheme;
    TextView paddingTheme;
    TextView roundTheme;
    TextView backgroundPreview;
    LinearLayout backgroundSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.list_view_theme_setting_dialog);

        Button applyButton = (Button) findViewById(R.id.apply_button);
        Button closeButton = (Button) findViewById(R.id.close_button);
        backgroundSetting = (LinearLayout) findViewById(R.id.background_setting);

        applyButton.setOnClickListener(listViewThemeApplyClickListener);
        closeButton.setOnClickListener(listViewThemeCloseClickListener);
        backgroundSetting.setOnClickListener(backgroundColorSettingClickListener);

        normalTheme = (TextView) findViewById(R.id.setting_list_view_theme_1);
        paddingTheme = (TextView) findViewById(R.id.setting_list_view_theme_2);
        roundTheme = (TextView) findViewById(R.id.setting_list_view_theme_3);
        backgroundPreview = (TextView) findViewById(R.id.background_preview);

        normalTheme.setBackgroundColor(Color.parseColor("#ffffff"));
        paddingTheme.setBackgroundColor(Color.parseColor("#ffffff"));
        roundTheme.setBackgroundColor(Color.parseColor("#ffffff"));
        switch (listViewTheme) {
            case "padding":
                paddingTheme.setBackgroundColor(Color.parseColor("#e9e9e9"));
                backgroundSetting.setVisibility(View.VISIBLE);
                break;
            case "round":
                roundTheme.setBackgroundColor(Color.parseColor("#e9e9e9"));
                backgroundSetting.setVisibility(View.VISIBLE);
                break;
            default:
                normalTheme.setBackgroundColor(Color.parseColor("#e9e9e9"));
                backgroundSetting.setVisibility(View.GONE);
                break;
        }
        backgroundPreview.setBackgroundColor(Color.parseColor(listViewThemeBackgroundColor));

        normalTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                normalTheme.setBackgroundColor(Color.parseColor("#e9e9e9"));
                paddingTheme.setBackgroundColor(Color.parseColor("#ffffff"));
                roundTheme.setBackgroundColor(Color.parseColor("#ffffff"));
                backgroundSetting.setVisibility(View.GONE);

                listViewTheme = "normal";
            }
        });
        paddingTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                normalTheme.setBackgroundColor(Color.parseColor("#ffffff"));
                paddingTheme.setBackgroundColor(Color.parseColor("#e9e9e9"));
                roundTheme.setBackgroundColor(Color.parseColor("#ffffff"));
                backgroundSetting.setVisibility(View.VISIBLE);

                listViewTheme = "padding";
            }
        });
        roundTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                normalTheme.setBackgroundColor(Color.parseColor("#ffffff"));
                paddingTheme.setBackgroundColor(Color.parseColor("#ffffff"));
                roundTheme.setBackgroundColor(Color.parseColor("#e9e9e9"));
                backgroundSetting.setVisibility(View.VISIBLE);

                listViewTheme = "round";
            }
        });
    }

    public ListViewThemePickerDialog(Context context, String listViewTheme, String listViewThemeBackgroundColor,
                                     View.OnClickListener listViewThemeCloseClickListener, View.OnClickListener listViewThemeApplyClickListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.listViewTheme = listViewTheme;
        this.listViewThemeBackgroundColor = listViewThemeBackgroundColor;

        this.listViewThemeCloseClickListener = listViewThemeCloseClickListener;
        this.listViewThemeApplyClickListener = listViewThemeApplyClickListener;
    }

    public String getListViewLayoutTheme() {
        return this.listViewTheme;
    }
    public String getListViewThemeBackgroundColor() {
        return this.listViewThemeBackgroundColor;
    }

    private View.OnClickListener backgroundColorSettingClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            colorPickerDialog = new ColorPickerDialog(context, dialogColorPickerCancelClickListener, dialogColorPickerDoneClickListener2);
            colorPickerDialog.show();
        }
    };


    private View.OnClickListener dialogColorPickerCancelClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            colorPickerDialog.dismiss();
        }
    };

    private View.OnClickListener dialogColorPickerDoneClickListener2 = new View.OnClickListener() {
        public void onClick(View v) {
            int color = colorPickerDialog.getColor();
            listViewThemeBackgroundColor = String.format("#%06X", (0xFFFFFF & color));

            backgroundPreview.setBackgroundColor(Color.parseColor(listViewThemeBackgroundColor));

            colorPickerDialog.dismiss();
        }
    };

}
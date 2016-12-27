package k.tomorrowdecision.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import k.tomorrowdecision.R;

public class MyTimeZoneDialog extends Dialog {

    private View.OnClickListener applyClickListener;
    private View.OnClickListener closeClickListener;
    private int editableTime;
    private Context context;
    TextView newTimeZone;
    TextView range;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.my_time_zone_dialog);

        TextView preTimeZone = (TextView) findViewById(R.id.pre_time_zone);
        preTimeZone.setText(Integer.toString(editableTime) + "시");

        range = (TextView) findViewById(R.id.new_time_zone_range);
        range.setText(Integer.toString((editableTime + 21) % 24) + ":00 ~ " + Integer.toString((editableTime + 3) % 24) + ":00");

        newTimeZone = (TextView) findViewById(R.id.new_time_zone);
        newTimeZone.setText(Integer.toString(editableTime) + "시");

        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        numberPicker.setMaxValue(23);
        numberPicker.setMinValue(0);
        numberPicker.setValue(editableTime);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                editableTime = newVal;
                newTimeZone.setText(Integer.toString(editableTime) + "시");
                range.setText(Integer.toString((editableTime + 21) % 24) + ":00 ~ " + Integer.toString((editableTime + 3) % 24) + ":00");
            }
        });

        Button closeButton = (Button) findViewById(R.id.close_button);
        closeButton.setOnClickListener(closeClickListener);
        Button applyButton = (Button) findViewById(R.id.apply_button);
        applyButton.setOnClickListener(applyClickListener);
    }

    public MyTimeZoneDialog(Context context, View.OnClickListener closeClickListener, View.OnClickListener applyClickListener, int editableTime) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.editableTime = editableTime;
        this.closeClickListener = closeClickListener;
        this.applyClickListener = applyClickListener;
    }

    public int getNewTimeZone() {
        return editableTime;
    }
}
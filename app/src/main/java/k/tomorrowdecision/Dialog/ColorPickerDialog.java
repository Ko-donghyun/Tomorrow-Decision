package k.tomorrowdecision.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import k.tomorrowdecision.R;

public class ColorPickerDialog extends Dialog {

    private int color;
    ColorPicker picker;

    private View.OnClickListener cancelClickListener;
    private View.OnClickListener doneClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.color_picker_dialog);

        picker = (ColorPicker) findViewById(R.id.picker);
        SVBar svBar = (SVBar) findViewById(R.id.svbar);
        SaturationBar saturationBar = (SaturationBar) findViewById(R.id.saturationbar);
        ValueBar valueBar = (ValueBar) findViewById(R.id.valuebar);

        picker.addSVBar(svBar);
        picker.addSaturationBar(saturationBar);
        picker.addValueBar(valueBar);
        picker.setShowOldCenterColor(false);

        Button cancelButton = (Button) findViewById(R.id.dialog_color_picker_cancel);
        cancelButton.setOnClickListener(cancelClickListener);
        Button doneButton = (Button) findViewById(R.id.dialog_color_picker_done);
        doneButton.setOnClickListener(doneClickListener);
    }

    public ColorPickerDialog(Context context,
                             View.OnClickListener cancelClickListener,
                             View.OnClickListener doneClickListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.cancelClickListener = cancelClickListener;
        this.doneClickListener = doneClickListener;

    }

    public int getColor() {
        return picker.getColor();
    }

}

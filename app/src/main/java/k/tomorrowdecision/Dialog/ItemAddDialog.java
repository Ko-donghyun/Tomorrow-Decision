package k.tomorrowdecision.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import k.tomorrowdecision.R;

public class ItemAddDialog extends Dialog {

    private EditText itemEditText;
    private String title;
    private String subTitle;

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

        setContentView(R.layout.item_add_dialog);

        TextView titleTextView = (TextView) findViewById(R.id.dialog_title);
        titleTextView.setText(title);
        TextView subTitleTextView = (TextView) findViewById(R.id.dialog_sub_title);
        subTitleTextView.setText(subTitle);
        itemEditText = (EditText) findViewById(R.id.item_text);
        Button cancelButton = (Button) findViewById(R.id.dialog_cancel);
        cancelButton.setOnClickListener(cancelClickListener);
        Button doneButton = (Button) findViewById(R.id.dialog_done);
        doneButton.setOnClickListener(doneClickListener);
    }

    public ItemAddDialog(Context context, String title, String subTitle,
                         View.OnClickListener cancelClickListener,
                         View.OnClickListener doneClickListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.title = title;
        this.subTitle = subTitle;
        this.cancelClickListener = cancelClickListener;
        this.doneClickListener = doneClickListener;

    }

    public EditText getEditTextView() {
        return itemEditText;
    }

    public String getItem() {
        return itemEditText.getText().toString();
    }
}
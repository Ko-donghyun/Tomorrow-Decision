package k.tomorrowdecision.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import k.tomorrowdecision.R;

public class ItemDeleteDialog extends Dialog {

    private long id;
    private String content;

    private View.OnClickListener deleteCancelClickListener;
    private View.OnClickListener deleteDoneClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.item_delete_dialog);

        TextView deleteItemTextView = (TextView) findViewById(R.id.dialog_delete_item);
        deleteItemTextView.setText(content);

        Button cancelButton = (Button) findViewById(R.id.dialog_delete_cancel);
        cancelButton.setOnClickListener(deleteCancelClickListener);
        Button doneButton = (Button) findViewById(R.id.dialog_delete_done);
        doneButton.setOnClickListener(deleteDoneClickListener);
    }

    public ItemDeleteDialog(Context context, long id, String content,
                         View.OnClickListener deleteCancelClickListener,
                         View.OnClickListener deleteDoneClickListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.id = id;
        this.content = content;
        this.deleteCancelClickListener = deleteCancelClickListener;
        this.deleteDoneClickListener = deleteDoneClickListener;

    }

    public long getItemId() {
        return this.id;
    }
}
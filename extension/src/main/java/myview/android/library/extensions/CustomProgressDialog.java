package myview.android.library.extensions;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import my.add.integrte.vnp.integratevnpdependency.R;

public class CustomProgressDialog extends ProgressDialog {

    private final String title;

    public CustomProgressDialog(Context context, String title) {
        super(context, R.style.CustomDialogAD);
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.custom_progress);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(title);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }

    @Override
    public void show() {
        super.show();
    }
}

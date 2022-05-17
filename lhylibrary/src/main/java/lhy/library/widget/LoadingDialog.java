package lhy.library.widget;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import lhy.lhylibrary.R;


public class LoadingDialog extends Dialog {


    public LoadingDialog(Activity context) {
        super(context);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View inflate = getLayoutInflater().inflate(R.layout.dialog_loading, null);
        setContentView(inflate);
        initConfig();
    }

    private void initConfig() {
        setCanceledOnTouchOutside(false);
        setCancelable(true);
        Window window = getWindow();
        WindowManager windowManager = window.getWindowManager();
        WindowManager.LayoutParams attributes = window.getAttributes();
        Display defaultDisplay = windowManager.getDefaultDisplay();
        attributes.width = (int) (defaultDisplay.getWidth() * 0.8);
        window.setBackgroundDrawableResource(R.drawable.shape_corner_white);
        window.setAttributes(attributes);
    }
}

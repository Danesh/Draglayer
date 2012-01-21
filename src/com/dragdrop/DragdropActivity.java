package com.dragdrop;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class DragdropActivity extends Activity {

    boolean vertical = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        if (!vertical) {
            setContentView(R.layout.main);
        } else {
            setContentView(R.layout.mainv);
        }
    }
}
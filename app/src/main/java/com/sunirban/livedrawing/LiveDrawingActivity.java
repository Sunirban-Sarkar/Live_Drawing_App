package com.sunirban.livedrawing;

import android.app.Activity;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Window;

import androidx.annotation.Nullable;

public class LiveDrawingActivity extends Activity {

    private LiveDrawingView mLiveDrawingView;

    @Override @SuppressWarnings("deprecation")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Display display = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            DisplayManager displayManager = (DisplayManager)getSystemService(Activity.DISPLAY_SERVICE);
            display = displayManager.getDisplay(Display.DEFAULT_DISPLAY);
        }else {
            display = getWindowManager().getDefaultDisplay();
        }
        Point size = new Point();
        display.getSize(size);

        mLiveDrawingView = new LiveDrawingView(this, size.x, size.y);
        setContentView(mLiveDrawingView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mLiveDrawingView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mLiveDrawingView.pause();
    }
}
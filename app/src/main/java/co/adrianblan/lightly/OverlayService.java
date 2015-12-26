package co.adrianblan.lightly;

import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * A service which contains an overlay which dims the screen.
 */
public class OverlayService extends Service {

    private View overlayView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        overlayView = new LinearLayout(this);

        overlayView.setFocusable(false);
        overlayView.setClickable(false);
        overlayView.setKeepScreenOn(false);
        overlayView.setLongClickable(false);
        overlayView.setFocusableInTouchMode(false);

        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);

        overlayView.setBackground(getBackgroundDrawable(95));
        windowManager.addView(overlayView, layoutParams);
    }

    private Drawable getBackgroundDrawable(int i) {
        int j = 255 - (int) Math.round(255D * Math.exp(4D * ((double) i / 100D) - 4D));
        return new ColorDrawable(Color.argb(j, 0, 0, 0));}

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(overlayView != null){
            WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            windowManager.removeView(overlayView);
        }
    }
}

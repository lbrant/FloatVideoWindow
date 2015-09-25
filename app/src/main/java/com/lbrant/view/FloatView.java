package com.lbrant.view;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * 作者：dell
 * 时间：2015/9/23 14:42
 * 文件：FloatVideoWindow
 * 描述：
 */
public class FloatView extends FrameLayout implements IFloat {
    private static final String LOG_TAG = "FloatView";

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowLayoutParams;
    private Point mScreenSize;

    public FloatView(Context context) {
        this(context, null);
    }

    public FloatView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mScreenSize = new Point();
    }


    @Override
    public void show() {
        Log.d(LOG_TAG, "show");
        mWindowManager.addView(this, mWindowLayoutParams);
    }

    @Override
    public void dismiss() {
        Log.d(LOG_TAG, "dismiss");
    }


    @Override
    public boolean isWindowVisible() {
        return getWindowVisibility() == View.VISIBLE;
    }

    /**
     * 返回屏幕的信息：宽、高
     *
     * @return :Point x代表屏幕宽，y代表屏幕高
     */
    @Override
    public Point getScreenSize() {
        return mScreenSize;
    }

    /**
     * 返回悬浮窗布局参数信息
     *
     * @return
     */
    @Override
    public WindowManager.LayoutParams getWindowLayoutParams() {
        return mWindowLayoutParams;
    }
}

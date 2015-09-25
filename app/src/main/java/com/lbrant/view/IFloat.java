package com.lbrant.view;

import android.graphics.Point;
import android.view.WindowManager;

/**
 * 作者：dell
 * 时间：2015/9/23 14:43
 * 文件：FloatVideoWindow
 * 描述：
 */
public interface IFloat {

    void show();

    void dismiss();

    boolean isWindowVisible();

    WindowManager.LayoutParams getWindowLayoutParams();

    Point getScreenSize();
}

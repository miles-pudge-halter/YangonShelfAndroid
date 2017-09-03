package com.zwenex.ayoe.yangonshelf;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by aYoe on 7/31/2017.
 */

public class ScrollingFABBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {
    private static final String TAG = "ScrollingFABBehavior";
    Handler mHandler;

    public ScrollingFABBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, final FloatingActionButton child, View target) {
        super.onStopNestedScroll(coordinatorLayout, child, target);

        if (mHandler == null)
            mHandler = new Handler();

//
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                child.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
//                Log.d("FabAnim","startHandler()");
//            }
//        },500);

    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        int fab_bottomMargin = layoutParams.bottomMargin;
        //child -> Floating Action Button
        if (dyConsumed > 0) {
            child.animate().scaleX(0).scaleY(0).setInterpolator(new LinearInterpolator()).setDuration(150).start();
        } else if (dyConsumed < 0) {
            child.animate().scaleX(1).scaleY(1).setInterpolator(new LinearInterpolator()).setDuration(150).start();
        }
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        if(mHandler!=null) {
            mHandler.removeMessages(0);
        }
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }
}
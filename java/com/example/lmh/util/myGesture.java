package com.example.lmh.util;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ToggleButton;

import static com.airbnb.lottie.L.TAG;

public class myGesture  extends GestureDetector.SimpleOnGestureListener {
    Context mContext;
    public float distance;
    public myGesture(Context context) {
        mContext = context;
    }
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2,float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {

        distance=(e1.getX()-e2.getX());
        if(distance>300.0)distance=300;
        if(distance<-300.0)distance=-300;

        Log.e(TAG, "distance: " + distance);
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    public static class TagView extends ToggleButton {

        private boolean mCheckEnable = true;

        public TagView(Context paramContext) {
            super(paramContext);
            init();
        }

        public TagView(Context paramContext, AttributeSet paramAttributeSet) {
            super(paramContext, paramAttributeSet);
            init();
        }

        public TagView(Context paramContext, AttributeSet paramAttributeSet,
                int paramInt) {
            super(paramContext, paramAttributeSet, 0);
            init();
        }

        private void init() {
            setTextOn(null);
            setTextOff(null);
            setText("");
            // setBackgroundResource(R.drawable.tag_bg);
        }

        //
        // public void setCheckEnable(boolean paramBoolean) {
        // this.mCheckEnable = paramBoolean;
        // if (!this.mCheckEnable) {
        // super.setChecked(false);
        // }
        // }
        //
        // public void setChecked(boolean paramBoolean) {
        // if (this.mCheckEnable) {
        // super.setChecked(paramBoolean);
        // }
        // }
        public void setCheckEnable(boolean paramBoolean) {
            super.setEnabled(paramBoolean);
        }
    }
}

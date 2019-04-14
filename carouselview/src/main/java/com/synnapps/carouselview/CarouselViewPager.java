package com.synnapps.carouselview;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;

import java.lang.reflect.Field;

/**
 * Created by Sayyam on 3/28/16.
 *
 * Edited by sqljim on 4/14/19
 */
public class CarouselViewPager extends ViewPager {

    private ImageClickListener imageClickListener;
    private float oldX = 0, newX = 0, oldY = 0, newY = 0, sens = 5;
    private boolean longClick;



    public void setImageClickListener(ImageClickListener imageClickListener) {
        this.imageClickListener = imageClickListener;
    }

    public CarouselViewPager(Context context) {
        super(context);
        postInitViewPager();
    }

    public CarouselViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        postInitViewPager();
    }

    private CarouselViewPagerScroller mScroller = null;

    /**
     * Override the Scroller instance with our own class so we can change the
     * duration
     */
    private void postInitViewPager() {
        try {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            Field interpolator = viewpager.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);

            mScroller = new CarouselViewPagerScroller(getContext(),
                    (Interpolator) interpolator.get(null));
            scroller.set(this, mScroller);
        } catch (Exception e) {
        }
    }

    /**
     * Set the factor by which the duration will change
     */
    public void setTransitionVelocity(int scrollFactor) {
        mScroller.setmScrollDuration(scrollFactor);
    }

    final Handler handler = new Handler();

    Runnable mlongClicked = new Runnable() {
        public void run() {
            longClick = true;
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldX = ev.getX();
                oldY = ev.getY();

                //This is where we start a timer, when the timer runs out the longClick boolean
                //is set to true, signifying a long press has occurred.
                longClick = false;
                handler.postDelayed(mlongClicked, ViewConfiguration.getLongPressTimeout());
                break;

            //The below detects whether a click/longClick has occurred
            //or whether a line has been drawn
            case MotionEvent.ACTION_UP:
                newX = ev.getX();
                newY = ev.getY();

                handler.removeCallbacks(mlongClicked);
                if (Math.abs(oldX - newX) < sens && !longClick) {
                    if(imageClickListener != null)
                        imageClickListener.onClick(getCurrentItem());
                    return true;
                }
                else if (Math.abs(oldX - newX) < sens && Math.abs(oldY - newY) < sens && longClick)
                {
                    if(imageClickListener != null)
                        imageClickListener.onLongClick(getCurrentItem());
                    return true;
                }
                oldX = 0;
                newX = 0;
                break;

        }

        return super.onTouchEvent(ev);
    }

}
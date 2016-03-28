package com.synnapps.carouselview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Sayyam on 11/25/15.
 */
public class CarouselView extends FrameLayout {

    private final int DEFAULT_SLIDE_INTERVAL = 3500;

    private int mPageCount;
    private int slideInterval = DEFAULT_SLIDE_INTERVAL;
    private int mIndicatorGravity;
    private int indicatorMarginVertical;
    private int indicatorMarginHorizontal;

    private ViewPager containerViewPager;
    private CirclePageIndicator mIndicator;
    private ViewListener mViewListener = null;
    private ImageListener mImageListener = null;

    private Timer swipeTimer;
    private SwipeTask swipeTask;

    private boolean autoPlay;
    private boolean disableAutoPlayOnUserInteraction;
    private int previousState;

    public CarouselView(Context context) {
        super(context);
    }

    public CarouselView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0, 0);
    }

    public CarouselView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CarouselView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (!isInEditMode()) {
            View view = LayoutInflater.from(context).inflate(R.layout.view_carousel, this, true);
            containerViewPager = (ViewPager) view.findViewById(R.id.containerViewPager);
            mIndicator = (CirclePageIndicator) view.findViewById(R.id.indicator);

            containerViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
              @Override
              public void onPageScrollStateChanged(int state) {
                // User initiated scroll
                if ((previousState == ViewPager.SCROLL_STATE_DRAGGING
                    && state == ViewPager.SCROLL_STATE_SETTLING) ||
                    (previousState == ViewPager.SCROLL_STATE_SETTLING
                        && state == ViewPager.SCROLL_STATE_IDLE)) {

                  if (!disableAutoPlayOnUserInteraction) {
                    playCarousel();
                  }
                }
                previousState = state;
              }
            });

            //Retrieve styles attributes
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CarouselView, defStyleAttr, 0);
            try {
                setIndicatorMarginVertical(a.getInt(R.styleable.CarouselView_indicatorMarginVertical, getResources().getDimensionPixelSize(R.dimen.default_indicator_margin_vertical)));
                setIndicatorMarginHorizontal(a.getInt(R.styleable.CarouselView_indicatorMarginHorizontal, getResources().getDimensionPixelSize(R.dimen.default_indicator_margin_horizontal)));
                setSlideInterval(a.getInt(R.styleable.CarouselView_slideInterval, DEFAULT_SLIDE_INTERVAL));
                setOrientation(a.getInt(R.styleable.CarouselView_indicatorOrientation, LinearLayout.HORIZONTAL));
                setIndicatorGravity(a.getInt(R.styleable.CarouselView_indicatorGravity, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL));
                setAutoPlay(a.getBoolean(R.styleable.CarouselView_autoPlay, true));
                setDisableAutoPlayOnUserInteraction(a.getBoolean(R.styleable.CarouselView_disableAutoPlayOnUserInteraction, false));

                int fillColor = a.getColor(R.styleable.CarouselView_fillColor, 0);
                if (fillColor != 0) {
                    setFillColor(fillColor);
                }
                int pageColor = a.getColor(R.styleable.CarouselView_pageColor, 0);
                if (pageColor != 0) {
                    setPageColor(pageColor);
                }
                float radius = a.getDimensionPixelSize(R.styleable.CarouselView_radius, 0);
                if (radius != 0) {
                    setRadius(radius);
                }
                setSnap(a.getBoolean(R.styleable.CarouselView_snap, getResources().getBoolean(R.bool.default_circle_indicator_snap)));
                int strokeColor = a.getColor(R.styleable.CarouselView_strokeColor, 0);
                if (strokeColor != 0) {
                    setStrokeColor(strokeColor);
                }
                float strokeWidth = a.getDimensionPixelSize(R.styleable.CarouselView_strokeWidth, 0);
                if (strokeWidth != 0) {
                    setStrokeWidth(strokeWidth);
                }
            } finally {
                a.recycle();
            }
        }
    }

    public int getSlideInterval() {
        return slideInterval;
    }

    /**
     * Set interval for one slide in milliseconds.
     *
     * @param slideInterval milliseconds
     */
    public void setSlideInterval(int slideInterval) {
        this.slideInterval = slideInterval;
    }

    /**
     * Set interval for one slide in milliseconds.
     *
     * @param slideInterval milliseconds
     */
    public void reSetSlideInterval(int slideInterval) {
        this.slideInterval = slideInterval;

        if (null != containerViewPager) {
            playCarousel();
        }
    }

    public boolean isAutoPlay() {
        return autoPlay;
    }

    private void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    public boolean isDisableAutoPlayOnUserInteraction() {
        return disableAutoPlayOnUserInteraction;
    }

    private void setDisableAutoPlayOnUserInteraction(boolean disableAutoPlayOnUserInteraction) {
        this.disableAutoPlayOnUserInteraction = disableAutoPlayOnUserInteraction;
    }

    private void setData() {
        CarouselPagerAdapter carouselPagerAdapter = new CarouselPagerAdapter(getContext());
        containerViewPager.setAdapter(carouselPagerAdapter);
        mIndicator.setViewPager(containerViewPager);
        containerViewPager.setOffscreenPageLimit(getPageCount());
        playCarousel();
    }

    private void stopScrollTimer() {
        if (null != swipeTimer) {
            swipeTimer.cancel();
        }

        if (null != swipeTask) {
            swipeTask.cancel();
        }
    }

    private void resetScrollTimer() {
        stopScrollTimer();

        swipeTask = new SwipeTask();
        swipeTimer = new Timer();
    }

    /**
     * Starts auto scrolling if
     */
    public void playCarousel() {
        resetScrollTimer();

        if (autoPlay && slideInterval > 0 &&
            containerViewPager.getAdapter() != null && containerViewPager.getAdapter().getCount() > 1) {

            swipeTimer.schedule(swipeTask, slideInterval, slideInterval);
        }
    }

    /**
     * Pause auto scrolling unless user interacts provided autoPlay is enabled.
     */
    public void pauseCarousel() {

        resetScrollTimer();
    }

    /**
     * Stops auto scrolling.
     */
    public void stopCarousel() {

        this.autoPlay = false;
    }

    private class CarouselPagerAdapter extends PagerAdapter {
        private Context mContext;

        public CarouselPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup viewGroup, int position) {
            Object objectToReturn;

            //Either let user set image to ImageView
            if (mImageListener != null) {

                ImageView imageView = new ImageView(mContext);
                imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));  //setting image position
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                objectToReturn = imageView;
                mImageListener.setImageForPosition(position, imageView);

                viewGroup.addView(imageView);

                //Or let user add his own ViewGroup
            } else if (mViewListener != null) {

                View view = mViewListener.setViewForPosition(position);

                if (null != view) {
                    objectToReturn = view;
                    viewGroup.addView(view);
                } else {
                    throw new RuntimeException("View can not be null for position " + position);
                }

            } else {
                throw new RuntimeException("View must set " + ImageListener.class.getSimpleName() + " or " + ViewListener.class.getSimpleName() + ".");
            }

            return objectToReturn;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return getPageCount();
        }
    }

    private class SwipeTask extends TimerTask {
        public void run() {
            containerViewPager.post(new Runnable() {
                public void run() {
                    containerViewPager.setCurrentItem((containerViewPager.getCurrentItem() + 1) % getPageCount(), true);
                }
            });
        }
    }

    public void setImageListener(ImageListener mImageListener) {
        this.mImageListener = mImageListener;
    }

    public void setViewListener(ViewListener mViewListener) {
        this.mViewListener = mViewListener;
    }

    public int getPageCount() {
        return mPageCount;
    }

    public void setPageCount(int mPageCount) {
        this.mPageCount = mPageCount;

        setData();
    }

    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        containerViewPager.addOnPageChangeListener(listener);
    }

    public void setCurrentItem(int item) {
        containerViewPager.setCurrentItem(item);
    }

    public int getIndicatorMarginVertical() {
        return indicatorMarginVertical;
    }

    public void setIndicatorMarginVertical(int _indicatorMarginVertical) {
        indicatorMarginVertical = _indicatorMarginVertical;
    }

    public int getIndicatorMarginHorizontal() {
        return indicatorMarginHorizontal;
    }

    public void setIndicatorMarginHorizontal(int _indicatorMarginHorizontal) {
        indicatorMarginHorizontal = _indicatorMarginHorizontal;
    }

    public int getIndicatorGravity() {
        return mIndicatorGravity;
    }

    public void setIndicatorGravity(int gravity) {
        mIndicatorGravity = gravity;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = mIndicatorGravity;
        params.setMargins(indicatorMarginHorizontal, indicatorMarginVertical, indicatorMarginHorizontal, indicatorMarginVertical);
        mIndicator.setLayoutParams(params);
    }

    public int getOrientation() {
        return mIndicator.getOrientation();
    }

    public int getFillColor() {
        return mIndicator.getFillColor();
    }

    public int getStrokeColor() {
        return mIndicator.getStrokeColor();
    }

    public void setSnap(boolean snap) {
        mIndicator.setSnap(snap);
    }

    public void setRadius(float radius) {
        mIndicator.setRadius(radius);
    }

    public float getStrokeWidth() {
        return mIndicator.getStrokeWidth();
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
    }

    public Drawable getIndicatorBackground() {
        return mIndicator.getBackground();
    }

    public void setFillColor(int fillColor) {
        mIndicator.setFillColor(fillColor);
    }

    public int getPageColor() {
        return mIndicator.getPageColor();
    }

    public void setOrientation(int orientation) {
        mIndicator.setOrientation(orientation);
    }

    public boolean isSnap() {
        return mIndicator.isSnap();
    }

    public void setStrokeColor(int strokeColor) {
        mIndicator.setStrokeColor(strokeColor);
    }

    public float getRadius() {
        return mIndicator.getRadius();
    }

    public void setPageColor(int pageColor) {
        mIndicator.setPageColor(pageColor);
    }

    public void setStrokeWidth(float strokeWidth) {
        mIndicator.setStrokeWidth(strokeWidth);
        int padding = (int) strokeWidth;
        mIndicator.setPadding(padding, padding, padding, padding);
    }
}
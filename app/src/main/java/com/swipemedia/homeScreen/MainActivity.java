package com.swipemedia.homeScreen;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.swipemedia.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private MediaPagerAdapter mAdapter;
    private int MaxCount = 10;
    private List<String> urilist = new ArrayList<>();
    private List<MainMediaView> mediaViewList = new ArrayList<>();
    private Context mContext;
    private int CurrentPage, SwipePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMainActivity();
        mContext = this;
    }

    void initMainActivity() {
        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        mAdapter = new MediaPagerAdapter();
        mViewPager.setAdapter(mAdapter);
    //    mViewPager.setPageTransformer(true, new DepthPageTransformer());
        urilist.add(0, "m1");
        urilist.add(1, "m2");
        urilist.add(2, "m3");
        urilist.add(3, "m1");
        urilist.add(4, "m2");
        urilist.add(5, "m3");
        urilist.add(6, "m1");
        urilist.add(7, "m2");
        urilist.add(8, "m3");
        urilist.add(9, "m2");
        ;
        for (int i = 0; i < MaxCount; i++) {
            String uri = urilist.get(i);
            String fileName = "android.resource://" + getPackageName() + "/raw/" + uri;
            mediaViewList.add(new MainMediaView(this, fileName));
        }
        mViewPager.setCurrentItem(MaxCount * 100);
        CurrentPage = MaxCount * 100;
        mediaViewList.get(0).setFirstStart();
        mViewPager.
                setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                    @Override
                    public void onPageSelected(int arg0) {
                        // arg0是当前选中的页面的Position
                        SwipePage = arg0;
                        Log.e("scroll", "page num=" + SwipePage);
                        if (CurrentPage == SwipePage) {
                            mediaViewList.get(CurrentPage % MaxCount).start();
                        } else {
                            mediaViewList.get(CurrentPage % MaxCount).pause();
                            mediaViewList.get(SwipePage % MaxCount).start();
                            CurrentPage = SwipePage;
                        }

                    }

                    @Override
                    public void onPageScrolled(int arg0, float arg1, int arg2) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int arg0) {
                        //arg0 ==1的时表示正在滑动，arg0==2的时表示滑动完毕了，arg0==0的时表示什么都没做。
                        if (arg0 == 0) {
                        } else if (arg0 == 1) {
                            mediaViewList.get(CurrentPage % MaxCount).pause();
                            Log.e("scrolled", "page num=" + CurrentPage);
                        } else if (arg0 == 2) {
                            if (CurrentPage == SwipePage) {
                                mediaViewList.get(CurrentPage % MaxCount).start();
                            }
                        }

                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < MaxCount; i++) {
            mediaViewList.get(i).release();
        }
    }

    class MediaPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            MainMediaView view = mediaViewList.get(position % MaxCount);
            view.initMedia();
            container.addView(view, 0);
            return view;

        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            MainMediaView view = mediaViewList.get(position % MaxCount);
            container.removeView(mediaViewList.get(position % MaxCount));
            view.release();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }


    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}

package com.github.teren4m.tab;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

public class TabLayout extends HorizontalScrollView {

    private Context context;
    private int tabWidth;
    private int indicatorColor;
    private int indicatorHeight;
    private int indicatorLineColor;

    private LinearLayout llTabs;
    private View indicator;

    public TabLayout(Context context) {
        super(context);
        init(context, null);
    }

    public TabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        this.context = context;
        if (attributeSet != null)
            initAttributes(attributeSet);
    }

    private void initAttributes(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AwesomeTabBar, 0, 0);
        try {
            int minimumTabWidth = context.getResources().getDimensionPixelSize(R.dimen.minimumTabWidth);
            tabWidth = typedArray.getDimensionPixelSize(R.styleable.AwesomeTabBar_atb_tabWidth, minimumTabWidth);
            int defaultIndicatorColor = ContextCompat.getColor(context, R.color.defaultIndicatorColor);
            indicatorColor = typedArray.getColor(R.styleable.AwesomeTabBar_atb_indicatorColor, defaultIndicatorColor);
            int minimumIndicatorHeight = context.getResources().getDimensionPixelSize(R.dimen.minimumIndicatorHeight);
            indicatorHeight = typedArray.getDimensionPixelSize(R.styleable.AwesomeTabBar_atb_indicatorHeight, minimumIndicatorHeight);
            indicatorLineColor = typedArray.getColor(R.styleable.AwesomeTabBar_atb_indicatorLineColor, 0);
        } finally {
            typedArray.recycle();
        }
    }

    public void setupWithViewPager(ViewPager viewPager) {
        if (viewPager == null) {
            throw new RuntimeException("Your ViewPager is null");
        } else if (viewPager.getAdapter() == null) {
            throw new RuntimeException("Your ViewPager has no adapter");
        } else if (!(viewPager.getAdapter() instanceof CustomViewFragmentPagerAdapter)) {
            throw new RuntimeException("Your ViewPager's adapter must extend SimpleTabBarAdapter");
        } else {
            setHorizontalScrollBarEnabled(false);
            addView(prepareContainerWithTabsAndIndicator(viewPager));
            scrollWithViewPager(viewPager);
        }
    }

    private LinearLayout prepareContainerWithTabsAndIndicator(ViewPager viewPager) {
        llTabs = new LinearLayout(context);
        LinearLayout.LayoutParams tabParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llTabs.setOrientation(LinearLayout.HORIZONTAL);
        llTabs.setLayoutParams(tabParams);
        addTabs(viewPager);

        LinearLayout parent = new LinearLayout(context);
        LinearLayout.LayoutParams parentParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        parent.setLayoutParams(parentParams);
        parent.setOrientation(LinearLayout.VERTICAL);

        parent.addView(llTabs);
        parent.addView(getFieldWithIndicator());

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                changeMaxTabWidth();
                fitTabs(tabWidth);
                invalidate();
            }
        });

        return parent;
    }

    private FrameLayout getFieldWithIndicator() {
        FrameLayout indicatorField = new FrameLayout(context);
        if (indicatorLineColor != 0)
            indicatorField.setBackgroundColor(indicatorLineColor);
        indicator = new View(context);
        LinearLayout.LayoutParams indicatorParams = new LinearLayout.LayoutParams(tabWidth, indicatorHeight);
        indicator.setBackgroundColor(indicatorColor);
        indicatorField.addView(indicator, indicatorParams);
        return indicatorField;
    }

    private void addTabs(final ViewPager viewPager) {
        for (int i = 0; i < viewPager.getAdapter().getCount(); i++) {
            addTab(viewPager, i);
        }
    }

    private void addTab(final ViewPager viewPager, final int position) {
        View view = newTab(viewPager, position);
        view.setMinimumWidth(tabWidth);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(position);
            }
        });
        llTabs.addView(view);
    }

    private View newTab(ViewPager viewPager, int position) {
        return getTabViewWithIcon(((CustomViewFragmentPagerAdapter) viewPager.getAdapter()), position);
    }

    private View getTabViewWithIcon(CustomViewFragmentPagerAdapter adapter, int position) {
        View view = adapter.getView(position);
        return view;
    }

    private void scrollWithViewPager(final ViewPager viewPager) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                indicator.animate()
                        .x((position + positionOffset) * tabWidth)
                        .setDuration(0)
                        .start();
                scrollTo((int) ((position + positionOffset - 1) * tabWidth), 0);
            }

            @Override
            public void onPageSelected(int position) {
                selectTab(viewPager);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void selectTab(ViewPager viewPager) {
        for (int i = 0; i < llTabs.getChildCount(); i++) {

        }
    }

    private void changeMaxTabWidth() {
        int screenWidth = getScreenWidth();
        int maxWidth = 0;
        for (int i = 0; i < llTabs.getChildCount(); i++) {
            View tab = llTabs.getChildAt(i);
            if (maxWidth < tab.getWidth())
                maxWidth = tab.getWidth();
        }
        if (llTabs.getChildCount() > 0) {
            View lastTab = llTabs.getChildAt(llTabs.getChildCount() - 1);
            if (lastTab.getRight() < screenWidth) {
                maxWidth = screenWidth / llTabs.getChildCount();
            }
        }
        tabWidth = maxWidth;
    }

    private void fitTabs(int maxWidth) {
        for (int i = 0; i < llTabs.getChildCount(); i++) {
            View tab = llTabs.getChildAt(i);
            tab.setMinimumWidth(maxWidth);
        }
        LayoutParams indicatorParams = new LayoutParams(maxWidth, indicatorHeight);
        indicator.setLayoutParams(indicatorParams);
        indicator.invalidate();
    }

    private int getScreenWidth() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }
}

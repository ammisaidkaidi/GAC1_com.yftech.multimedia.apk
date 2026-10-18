package android.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.icu.util.Calendar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.widget.PagerAdapter;

/* JADX INFO: loaded from: classes2.dex */
class DayPickerPagerAdapter extends PagerAdapter {
    private static final int MONTHS_IN_YEAR = 12;
    private ColorStateList mCalendarTextColor;
    private final int mCalendarViewId;
    private int mCount;
    private ColorStateList mDayHighlightColor;
    private int mDayOfWeekTextAppearance;
    private ColorStateList mDaySelectorColor;
    private int mDayTextAppearance;
    private int mFirstDayOfWeek;
    private final LayoutInflater mInflater;
    private final int mLayoutResId;
    private int mMonthTextAppearance;
    private OnDaySelectedListener mOnDaySelectedListener;
    private final Calendar mMinDate = Calendar.getInstance();
    private final Calendar mMaxDate = Calendar.getInstance();
    private final SparseArray<ViewHolder> mItems = new SparseArray<>();
    private Calendar mSelectedDay = null;
    private final SimpleMonthView.OnDayClickListener mOnDayClickListener = new SimpleMonthView.OnDayClickListener() { // from class: android.widget.DayPickerPagerAdapter.1
        @Override // android.widget.SimpleMonthView.OnDayClickListener
        public void onDayClick(SimpleMonthView simpleMonthView, Calendar calendar) {
            if (calendar != null) {
                DayPickerPagerAdapter.this.setSelectedDay(calendar);
                if (DayPickerPagerAdapter.this.mOnDaySelectedListener != null) {
                    DayPickerPagerAdapter.this.mOnDaySelectedListener.onDaySelected(DayPickerPagerAdapter.this, calendar);
                }
            }
        }
    };

    public interface OnDaySelectedListener {
        void onDaySelected(DayPickerPagerAdapter dayPickerPagerAdapter, Calendar calendar);
    }

    private static class ViewHolder {
        public final SimpleMonthView calendar;
        public final View container;
        public final int position;

        public ViewHolder(int i, View view, SimpleMonthView simpleMonthView) {
            this.position = i;
            this.container = view;
            this.calendar = simpleMonthView;
        }
    }

    public DayPickerPagerAdapter(Context context, int i, int i2) {
        this.mInflater = LayoutInflater.from(context);
        this.mLayoutResId = i;
        this.mCalendarViewId = i2;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(new int[]{16843820});
        this.mDayHighlightColor = typedArrayObtainStyledAttributes.getColorStateList(0);
        typedArrayObtainStyledAttributes.recycle();
    }

    private int getMonthForPosition(int i) {
        return (i + this.mMinDate.get(2)) % 12;
    }

    private int getPositionForDay(Calendar calendar) {
        if (calendar == null) {
            return -1;
        }
        return ((calendar.get(1) - this.mMinDate.get(1)) * 12) + (calendar.get(2) - this.mMinDate.get(2));
    }

    private int getYearForPosition(int i) {
        return ((i + this.mMinDate.get(2)) / 12) + this.mMinDate.get(1);
    }

    @Override // com.android.internal.widget.PagerAdapter
    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        viewGroup.removeView(((ViewHolder) obj).container);
        this.mItems.remove(i);
    }

    public boolean getBoundsForDate(Calendar calendar, Rect rect) {
        ViewHolder viewHolder = this.mItems.get(getPositionForDay(calendar), null);
        if (viewHolder == null) {
            return false;
        }
        return viewHolder.calendar.getBoundsForDay(calendar.get(5), rect);
    }

    @Override // com.android.internal.widget.PagerAdapter
    public int getCount() {
        return this.mCount;
    }

    int getDayOfWeekTextAppearance() {
        return this.mDayOfWeekTextAppearance;
    }

    int getDayTextAppearance() {
        return this.mDayTextAppearance;
    }

    public int getFirstDayOfWeek() {
        return this.mFirstDayOfWeek;
    }

    @Override // com.android.internal.widget.PagerAdapter
    public int getItemPosition(Object obj) {
        return ((ViewHolder) obj).position;
    }

    @Override // com.android.internal.widget.PagerAdapter
    public CharSequence getPageTitle(int i) {
        SimpleMonthView simpleMonthView = this.mItems.get(i).calendar;
        if (simpleMonthView != null) {
            return simpleMonthView.getMonthYearLabel();
        }
        return null;
    }

    SimpleMonthView getView(Object obj) {
        if (obj == null) {
            return null;
        }
        return ((ViewHolder) obj).calendar;
    }

    @Override // com.android.internal.widget.PagerAdapter
    public Object instantiateItem(ViewGroup viewGroup, int i) {
        View viewInflate = this.mInflater.inflate(this.mLayoutResId, viewGroup, false);
        SimpleMonthView simpleMonthView = (SimpleMonthView) viewInflate.findViewById(this.mCalendarViewId);
        simpleMonthView.setOnDayClickListener(this.mOnDayClickListener);
        simpleMonthView.setMonthTextAppearance(this.mMonthTextAppearance);
        simpleMonthView.setDayOfWeekTextAppearance(this.mDayOfWeekTextAppearance);
        simpleMonthView.setDayTextAppearance(this.mDayTextAppearance);
        if (this.mDaySelectorColor != null) {
            simpleMonthView.setDaySelectorColor(this.mDaySelectorColor);
        }
        if (this.mDayHighlightColor != null) {
            simpleMonthView.setDayHighlightColor(this.mDayHighlightColor);
        }
        if (this.mCalendarTextColor != null) {
            simpleMonthView.setMonthTextColor(this.mCalendarTextColor);
            simpleMonthView.setDayOfWeekTextColor(this.mCalendarTextColor);
            simpleMonthView.setDayTextColor(this.mCalendarTextColor);
        }
        int monthForPosition = getMonthForPosition(i);
        int yearForPosition = getYearForPosition(i);
        simpleMonthView.setMonthParams((this.mSelectedDay == null || this.mSelectedDay.get(2) != monthForPosition) ? -1 : this.mSelectedDay.get(5), monthForPosition, yearForPosition, this.mFirstDayOfWeek, (this.mMinDate.get(2) == monthForPosition && this.mMinDate.get(1) == yearForPosition) ? this.mMinDate.get(5) : 1, (this.mMaxDate.get(2) == monthForPosition && this.mMaxDate.get(1) == yearForPosition) ? this.mMaxDate.get(5) : 31);
        ViewHolder viewHolder = new ViewHolder(i, viewInflate, simpleMonthView);
        this.mItems.put(i, viewHolder);
        viewGroup.addView(viewInflate);
        return viewHolder;
    }

    @Override // com.android.internal.widget.PagerAdapter
    public boolean isViewFromObject(View view, Object obj) {
        return view == ((ViewHolder) obj).container;
    }

    void setCalendarTextColor(ColorStateList colorStateList) {
        this.mCalendarTextColor = colorStateList;
        notifyDataSetChanged();
    }

    void setDayOfWeekTextAppearance(int i) {
        this.mDayOfWeekTextAppearance = i;
        notifyDataSetChanged();
    }

    void setDaySelectorColor(ColorStateList colorStateList) {
        this.mDaySelectorColor = colorStateList;
        notifyDataSetChanged();
    }

    void setDayTextAppearance(int i) {
        this.mDayTextAppearance = i;
        notifyDataSetChanged();
    }

    public void setFirstDayOfWeek(int i) {
        this.mFirstDayOfWeek = i;
        int size = this.mItems.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.mItems.valueAt(i2).calendar.setFirstDayOfWeek(i);
        }
    }

    void setMonthTextAppearance(int i) {
        this.mMonthTextAppearance = i;
        notifyDataSetChanged();
    }

    public void setOnDaySelectedListener(OnDaySelectedListener onDaySelectedListener) {
        this.mOnDaySelectedListener = onDaySelectedListener;
    }

    public void setRange(Calendar calendar, Calendar calendar2) {
        this.mMinDate.setTimeInMillis(calendar.getTimeInMillis());
        this.mMaxDate.setTimeInMillis(calendar2.getTimeInMillis());
        this.mCount = (this.mMaxDate.get(2) - this.mMinDate.get(2)) + ((this.mMaxDate.get(1) - this.mMinDate.get(1)) * 12) + 1;
        notifyDataSetChanged();
    }

    public void setSelectedDay(Calendar calendar) {
        ViewHolder viewHolder;
        ViewHolder viewHolder2;
        int positionForDay = getPositionForDay(this.mSelectedDay);
        int positionForDay2 = getPositionForDay(calendar);
        if (positionForDay != positionForDay2 && positionForDay >= 0 && (viewHolder2 = this.mItems.get(positionForDay, null)) != null) {
            viewHolder2.calendar.setSelectedDay(-1);
        }
        if (positionForDay2 >= 0 && (viewHolder = this.mItems.get(positionForDay2, null)) != null) {
            viewHolder.calendar.setSelectedDay(calendar.get(5));
        }
        this.mSelectedDay = calendar;
    }
}

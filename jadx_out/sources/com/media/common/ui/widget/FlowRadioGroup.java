package com.media.common.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes3.dex */
public class FlowRadioGroup extends LinearLayout {
    private static final String TAG = "FlowRadioGroup";
    private int mCheckedId;
    private CompoundButton.OnCheckedChangeListener mChildOnCheckedChangeListener;
    private View.OnClickListener mChildOnClickListener;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private PassThroughHierarchyChangeListener mPassThroughListener;
    private boolean mProtectFromCheckedChange;
    private boolean onlyForChange;
    private ArrayList<RadioButton> radioButtons;

    private class CheckedStateTracker implements CompoundButton.OnCheckedChangeListener {
        private CheckedStateTracker() {
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            if (FlowRadioGroup.this.mProtectFromCheckedChange) {
                return;
            }
            FlowRadioGroup.this.mProtectFromCheckedChange = true;
            if (FlowRadioGroup.this.mCheckedId != -1) {
                FlowRadioGroup.this.setCheckedStateForView(FlowRadioGroup.this.mCheckedId, false);
            }
            FlowRadioGroup.this.mProtectFromCheckedChange = false;
            FlowRadioGroup.this.setCheckedId(compoundButton.getId());
        }
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {
        public LayoutParams(int i, int i2) {
            super(i, i2);
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
        }

        @Override // android.view.ViewGroup.LayoutParams
        protected void setBaseAttributes(TypedArray typedArray, int i, int i2) {
            if (typedArray.hasValue(i)) {
                this.width = typedArray.getLayoutDimension(i, "layout_width");
            } else {
                this.width = -2;
            }
            if (typedArray.hasValue(i2)) {
                this.height = typedArray.getLayoutDimension(i2, "layout_height");
            } else {
                this.height = -2;
            }
        }
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(FlowRadioGroup flowRadioGroup, int i);
    }

    private class PassThroughHierarchyChangeListener implements ViewGroup.OnHierarchyChangeListener {
        private ViewGroup.OnHierarchyChangeListener mOnHierarchyChangeListener;

        private PassThroughHierarchyChangeListener() {
        }

        @Override // android.view.ViewGroup.OnHierarchyChangeListener
        public void onChildViewAdded(View view, View view2) {
            if (view == FlowRadioGroup.this && (view2 instanceof RadioButton)) {
                if (view2.getId() == -1) {
                    view2.setId(view2.hashCode());
                }
                if (FlowRadioGroup.this.onlyForChange) {
                    ((RadioButton) view2).setOnCheckedChangeListener(FlowRadioGroup.this.mChildOnCheckedChangeListener);
                } else {
                    view2.setOnClickListener(FlowRadioGroup.this.mChildOnClickListener);
                }
            } else if (view == FlowRadioGroup.this && (view2 instanceof ViewGroup)) {
                FlowRadioGroup.this.setCheckedId((ViewGroup) view2);
            }
            if (this.mOnHierarchyChangeListener != null) {
                this.mOnHierarchyChangeListener.onChildViewAdded(view, view2);
            }
        }

        @Override // android.view.ViewGroup.OnHierarchyChangeListener
        public void onChildViewRemoved(View view, View view2) {
            if (view == FlowRadioGroup.this && (view2 instanceof RadioButton)) {
                ((RadioButton) view2).setOnCheckedChangeListener(null);
            } else if (view == FlowRadioGroup.this && (view2 instanceof ViewGroup)) {
                FlowRadioGroup.this.findRadioButton((ViewGroup) view2).setOnCheckedChangeListener(null);
            }
            if (this.mOnHierarchyChangeListener != null) {
                this.mOnHierarchyChangeListener.onChildViewRemoved(view, view2);
            }
        }
    }

    public FlowRadioGroup(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCheckedId = -1;
        this.mProtectFromCheckedChange = false;
        this.onlyForChange = true;
        init();
    }

    private void init() {
        this.mChildOnCheckedChangeListener = new CheckedStateTracker();
        this.mPassThroughListener = new PassThroughHierarchyChangeListener();
        super.setOnHierarchyChangeListener(this.mPassThroughListener);
        this.radioButtons = new ArrayList<>();
        this.mChildOnClickListener = new View.OnClickListener() { // from class: com.media.common.ui.widget.FlowRadioGroup.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (FlowRadioGroup.this.mProtectFromCheckedChange) {
                    return;
                }
                FlowRadioGroup.this.mProtectFromCheckedChange = true;
                if (FlowRadioGroup.this.mCheckedId != -1 && FlowRadioGroup.this.mCheckedId != view.getId()) {
                    FlowRadioGroup.this.setCheckedStateForView(FlowRadioGroup.this.mCheckedId, false);
                }
                FlowRadioGroup.this.mProtectFromCheckedChange = false;
                FlowRadioGroup.this.setCheckedId(view.getId());
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCheckedId(int i) {
        this.mCheckedId = i;
        if (this.mOnCheckedChangeListener != null) {
            this.mOnCheckedChangeListener.onCheckedChanged(this, this.mCheckedId);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCheckedId(ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (viewGroup.getChildAt(i) instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) viewGroup.getChildAt(i);
                if (radioButton.getId() == -1) {
                    radioButton.setId(radioButton.hashCode());
                }
                if (this.onlyForChange) {
                    radioButton.setOnCheckedChangeListener(this.mChildOnCheckedChangeListener);
                } else {
                    radioButton.setOnClickListener(this.mChildOnClickListener);
                }
            } else if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                setCheckedId((ViewGroup) viewGroup.getChildAt(i));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCheckedStateForView(int i, boolean z) {
        View viewFindViewById = findViewById(i);
        if (viewFindViewById == null || !(viewFindViewById instanceof RadioButton)) {
            return;
        }
        ((RadioButton) viewFindViewById).setChecked(z);
    }

    private void setCheckedView(ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (viewGroup.getChildAt(i) instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) viewGroup.getChildAt(i);
                this.radioButtons.add(radioButton);
                if (radioButton.isChecked()) {
                    this.mProtectFromCheckedChange = true;
                    if (this.mCheckedId != -1) {
                        setCheckedStateForView(this.mCheckedId, false);
                    }
                    this.mProtectFromCheckedChange = false;
                    setCheckedId(radioButton.getId());
                }
            } else if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                setCheckedView((ViewGroup) viewGroup.getChildAt(i));
            }
        }
    }

    @Override // android.view.ViewGroup
    public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
        if (view instanceof RadioButton) {
            RadioButton radioButton = (RadioButton) view;
            this.radioButtons.add(radioButton);
            if (radioButton.isChecked()) {
                this.mProtectFromCheckedChange = true;
                if (this.mCheckedId != -1) {
                    setCheckedStateForView(this.mCheckedId, false);
                }
                this.mProtectFromCheckedChange = false;
                setCheckedId(radioButton.getId());
            }
        } else if (view instanceof ViewGroup) {
            setCheckedView((ViewGroup) view);
        }
        super.addView(view, i, layoutParams);
    }

    public void check(int i) {
        if (!this.onlyForChange) {
            if (this.mCheckedId != -1) {
                setCheckedStateForView(this.mCheckedId, false);
            }
            if (i != -1) {
                setCheckedStateForView(i, true);
            }
            setCheckedId(i);
            return;
        }
        if (i == -1 || i != this.mCheckedId) {
            if (this.mCheckedId != -1) {
                setCheckedStateForView(this.mCheckedId, false);
            }
            if (i != -1) {
                setCheckedStateForView(i, true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.ViewGroup
    public boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    public void clearCheck() {
        check(-1);
    }

    public RadioButton findRadioButton(ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        RadioButton radioButton = null;
        for (int i = 0; i < childCount; i++) {
            if (viewGroup.getChildAt(i) instanceof RadioButton) {
                radioButton = (RadioButton) viewGroup.getChildAt(i);
            } else if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                RadioButton radioButtonFindRadioButton = findRadioButton((ViewGroup) viewGroup.getChildAt(i));
                findRadioButton((ViewGroup) viewGroup.getChildAt(i));
                return radioButtonFindRadioButton;
            }
        }
        return radioButton;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.ViewGroup
    public LinearLayout.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    public int getCheckedRadioButtonId() {
        return this.mCheckedId;
    }

    public RadioButton getRadioButton(int i) {
        return this.radioButtons.get(i);
    }

    public int getRadioButtonCount() {
        return this.radioButtons.size();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        if (this.mCheckedId != -1) {
            this.mProtectFromCheckedChange = true;
            setCheckedStateForView(this.mCheckedId, true);
            this.mProtectFromCheckedChange = false;
            setCheckedId(this.mCheckedId);
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.mOnCheckedChangeListener = onCheckedChangeListener;
    }

    @Override // android.view.ViewGroup
    public void setOnHierarchyChangeListener(ViewGroup.OnHierarchyChangeListener onHierarchyChangeListener) {
        this.mPassThroughListener.mOnHierarchyChangeListener = onHierarchyChangeListener;
    }

    public void setOnlyForChange(boolean z) {
        this.onlyForChange = z;
        for (RadioButton radioButton : this.radioButtons) {
            if (z) {
                radioButton.setOnClickListener(null);
                radioButton.setOnCheckedChangeListener(this.mChildOnCheckedChangeListener);
            } else {
                radioButton.setOnClickListener(this.mChildOnClickListener);
                radioButton.setOnCheckedChangeListener(null);
            }
        }
    }
}

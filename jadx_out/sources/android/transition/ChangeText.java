package android.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class ChangeText extends Transition {
    public static final int CHANGE_BEHAVIOR_IN = 2;
    public static final int CHANGE_BEHAVIOR_KEEP = 0;
    public static final int CHANGE_BEHAVIOR_OUT = 1;
    public static final int CHANGE_BEHAVIOR_OUT_IN = 3;
    private static final String LOG_TAG = "TextChange";
    private static final String PROPNAME_TEXT_COLOR = "android:textchange:textColor";
    private int mChangeBehavior = 0;
    private static final String PROPNAME_TEXT = "android:textchange:text";
    private static final String PROPNAME_TEXT_SELECTION_START = "android:textchange:textSelectionStart";
    private static final String PROPNAME_TEXT_SELECTION_END = "android:textchange:textSelectionEnd";
    private static final String[] sTransitionProperties = {PROPNAME_TEXT, PROPNAME_TEXT_SELECTION_START, PROPNAME_TEXT_SELECTION_END};

    private void captureValues(TransitionValues transitionValues) {
        if (transitionValues.view instanceof TextView) {
            TextView textView = (TextView) transitionValues.view;
            transitionValues.values.put(PROPNAME_TEXT, textView.getText());
            if (textView instanceof EditText) {
                transitionValues.values.put(PROPNAME_TEXT_SELECTION_START, Integer.valueOf(textView.getSelectionStart()));
                transitionValues.values.put(PROPNAME_TEXT_SELECTION_END, Integer.valueOf(textView.getSelectionEnd()));
            }
            if (this.mChangeBehavior > 0) {
                transitionValues.values.put(PROPNAME_TEXT_COLOR, Integer.valueOf(textView.getCurrentTextColor()));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSelection(EditText editText, int i, int i2) {
        if (i < 0 || i2 < 0) {
            return;
        }
        editText.setSelection(i, i2);
    }

    @Override // android.transition.Transition
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override // android.transition.Transition
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    /* JADX WARN: Code duplicated, block: B:67:0x019d A[PHI: r18
      0x019d: PHI (r18v3 android.animation.Animator) = (r18v1 android.animation.Animator), (r18v4 android.animation.Animator) binds: [B:68:0x019f, B:66:0x018a] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:68:0x019f A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:69:0x01a1  */
    @Override // android.transition.Transition
    public Animator createAnimator(ViewGroup viewGroup, TransitionValues transitionValues, TransitionValues transitionValues2) {
        int i;
        int i2;
        int i3;
        int iIntValue;
        final int i4;
        ValueAnimator valueAnimator;
        int i5;
        final int i6;
        Animator animator;
        final int i7;
        if (transitionValues == null || transitionValues2 == null || !(transitionValues.view instanceof TextView) || !(transitionValues2.view instanceof TextView)) {
            return null;
        }
        final TextView textView = (TextView) transitionValues2.view;
        Map<String, Object> map = transitionValues.values;
        Map<String, Object> map2 = transitionValues2.values;
        final CharSequence charSequence = map.get(PROPNAME_TEXT) != null ? (CharSequence) map.get(PROPNAME_TEXT) : "";
        final CharSequence charSequence2 = map2.get(PROPNAME_TEXT) != null ? (CharSequence) map2.get(PROPNAME_TEXT) : "";
        boolean z = textView instanceof EditText;
        if (z) {
            int iIntValue2 = map.get(PROPNAME_TEXT_SELECTION_START) != null ? ((Integer) map.get(PROPNAME_TEXT_SELECTION_START)).intValue() : -1;
            int iIntValue3 = map.get(PROPNAME_TEXT_SELECTION_END) != null ? ((Integer) map.get(PROPNAME_TEXT_SELECTION_END)).intValue() : iIntValue2;
            int iIntValue4 = map2.get(PROPNAME_TEXT_SELECTION_START) != null ? ((Integer) map2.get(PROPNAME_TEXT_SELECTION_START)).intValue() : -1;
            i3 = iIntValue4;
            i = iIntValue2;
            i2 = iIntValue3;
            iIntValue = map2.get(PROPNAME_TEXT_SELECTION_END) != null ? ((Integer) map2.get(PROPNAME_TEXT_SELECTION_END)).intValue() : iIntValue4;
        } else {
            i = -1;
            i2 = -1;
            i3 = -1;
            iIntValue = -1;
        }
        if (charSequence.equals(charSequence2)) {
            return null;
        }
        if (this.mChangeBehavior != 2) {
            textView.setText(charSequence);
            if (z) {
                setSelection((EditText) textView, i, i2);
            }
        }
        if (this.mChangeBehavior == 0) {
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            final int i8 = i3;
            final int i9 = iIntValue;
            valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: android.transition.ChangeText.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator2) {
                    if (charSequence.equals(textView.getText())) {
                        textView.setText(charSequence2);
                        if (textView instanceof EditText) {
                            ChangeText.this.setSelection((EditText) textView, i8, i9);
                        }
                    }
                }
            });
            animator = valueAnimatorOfFloat;
            i = i;
            i2 = i2;
            i7 = 0;
        } else {
            final int iIntValue5 = ((Integer) map.get(PROPNAME_TEXT_COLOR)).intValue();
            int iIntValue6 = ((Integer) map2.get(PROPNAME_TEXT_COLOR)).intValue();
            if (this.mChangeBehavior == 3 || this.mChangeBehavior == 1) {
                ValueAnimator valueAnimatorOfInt = ValueAnimator.ofInt(Color.alpha(iIntValue5), 0);
                valueAnimatorOfInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: android.transition.ChangeText.2
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        int iIntValue7 = ((Integer) valueAnimator2.getAnimatedValue()).intValue();
                        textView.setTextColor((iIntValue5 & 16777215) | (iIntValue7 << 24));
                    }
                });
                final int i10 = i3;
                i4 = iIntValue6;
                final int i11 = iIntValue;
                valueAnimatorOfInt.addListener(new AnimatorListenerAdapter() { // from class: android.transition.ChangeText.3
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator2) {
                        if (charSequence.equals(textView.getText())) {
                            textView.setText(charSequence2);
                            if (textView instanceof EditText) {
                                ChangeText.this.setSelection((EditText) textView, i10, i11);
                            }
                        }
                        textView.setTextColor(i4);
                    }
                });
                valueAnimator = valueAnimatorOfInt;
            } else {
                i4 = iIntValue6;
                valueAnimator = null;
            }
            if (this.mChangeBehavior != 3) {
                i5 = 2;
                if (this.mChangeBehavior != 2) {
                    i6 = i4;
                    animator = null;
                }
                if (valueAnimator == null && animator != null) {
                    AnimatorSet animatorSet = new AnimatorSet();
                    Animator[] animatorArr = new Animator[i5];
                    animatorArr[0] = valueAnimator;
                    animatorArr[1] = animator;
                    animatorSet.playSequentially(animatorArr);
                    animator = animatorSet;
                } else if (valueAnimator != null) {
                    i7 = i6;
                    animator = valueAnimator;
                }
                i7 = i6;
            } else {
                i5 = 2;
            }
            int[] iArr = new int[i5];
            iArr[0] = 0;
            iArr[r4] = Color.alpha(i4);
            ValueAnimator valueAnimatorOfInt2 = ValueAnimator.ofInt(iArr);
            i6 = i4;
            valueAnimatorOfInt2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: android.transition.ChangeText.4
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    int iIntValue7 = ((Integer) valueAnimator2.getAnimatedValue()).intValue();
                    textView.setTextColor((i6 & 16777215) | (iIntValue7 << 24));
                }
            });
            valueAnimatorOfInt2.addListener(new AnimatorListenerAdapter() { // from class: android.transition.ChangeText.5
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator2) {
                    textView.setTextColor(i6);
                }
            });
            animator = valueAnimatorOfInt2;
            if (valueAnimator == null) {
                if (valueAnimator != null) {
                    i7 = i6;
                    animator = valueAnimator;
                } else {
                    i7 = i6;
                }
            } else if (valueAnimator != null) {
                i7 = i6;
                animator = valueAnimator;
            } else {
                i7 = i6;
            }
        }
        final int i12 = i3;
        final int i13 = iIntValue;
        final int i14 = i;
        final int i15 = i2;
        addListener(new TransitionListenerAdapter() { // from class: android.transition.ChangeText.6
            int mPausedColor = 0;

            @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
            }

            @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
            public void onTransitionPause(Transition transition) {
                if (ChangeText.this.mChangeBehavior != 2) {
                    textView.setText(charSequence2);
                    if (textView instanceof EditText) {
                        ChangeText.this.setSelection((EditText) textView, i12, i13);
                    }
                }
                if (ChangeText.this.mChangeBehavior > 0) {
                    this.mPausedColor = textView.getCurrentTextColor();
                    textView.setTextColor(i7);
                }
            }

            @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
            public void onTransitionResume(Transition transition) {
                if (ChangeText.this.mChangeBehavior != 2) {
                    textView.setText(charSequence);
                    if (textView instanceof EditText) {
                        ChangeText.this.setSelection((EditText) textView, i14, i15);
                    }
                }
                if (ChangeText.this.mChangeBehavior > 0) {
                    textView.setTextColor(this.mPausedColor);
                }
            }
        });
        return animator;
    }

    public int getChangeBehavior() {
        return this.mChangeBehavior;
    }

    @Override // android.transition.Transition
    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    public ChangeText setChangeBehavior(int i) {
        if (i >= 0 && i <= 3) {
            this.mChangeBehavior = i;
        }
        return this;
    }
}

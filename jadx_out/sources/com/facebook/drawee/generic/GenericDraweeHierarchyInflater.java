package com.facebook.drawee.generic;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import com.facebook.drawee.R;
import com.facebook.drawee.drawable.AutoRotateDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import com.facebook.infer.annotation.ReturnsOwnership;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
public class GenericDraweeHierarchyInflater {
    @Nullable
    private static Drawable getDrawable(Context context, TypedArray typedArray, int i) {
        int resourceId = typedArray.getResourceId(i, 0);
        if (resourceId == 0) {
            return null;
        }
        return context.getResources().getDrawable(resourceId);
    }

    @ReturnsOwnership
    private static RoundingParams getRoundingParams(GenericDraweeHierarchyBuilder genericDraweeHierarchyBuilder) {
        if (genericDraweeHierarchyBuilder.getRoundingParams() == null) {
            genericDraweeHierarchyBuilder.setRoundingParams(new RoundingParams());
        }
        return genericDraweeHierarchyBuilder.getRoundingParams();
    }

    @Nullable
    private static ScalingUtils.ScaleType getScaleTypeFromXml(TypedArray typedArray, int i) {
        switch (typedArray.getInt(i, -2)) {
            case -1:
                return null;
            case 0:
                return ScalingUtils.ScaleType.FIT_XY;
            case 1:
                return ScalingUtils.ScaleType.FIT_START;
            case 2:
                return ScalingUtils.ScaleType.FIT_CENTER;
            case 3:
                return ScalingUtils.ScaleType.FIT_END;
            case 4:
                return ScalingUtils.ScaleType.CENTER;
            case 5:
                return ScalingUtils.ScaleType.CENTER_INSIDE;
            case 6:
                return ScalingUtils.ScaleType.CENTER_CROP;
            case 7:
                return ScalingUtils.ScaleType.FOCUS_CROP;
            case 8:
                return ScalingUtils.ScaleType.FIT_BOTTOM_START;
            default:
                throw new RuntimeException("XML attribute not specified!");
        }
    }

    public static GenericDraweeHierarchyBuilder inflateBuilder(Context context, @Nullable AttributeSet attributeSet) throws Throwable {
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.beginSection("GenericDraweeHierarchyBuilder#inflateBuilder");
        }
        GenericDraweeHierarchyBuilder genericDraweeHierarchyBuilderUpdateBuilder = updateBuilder(new GenericDraweeHierarchyBuilder(context.getResources()), context, attributeSet);
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
        }
        return genericDraweeHierarchyBuilderUpdateBuilder;
    }

    public static GenericDraweeHierarchy inflateHierarchy(Context context, @Nullable AttributeSet attributeSet) {
        return inflateBuilder(context, attributeSet).build();
    }

    /* JADX WARN: Code duplicated, block: B:136:0x01f6 A[PHI: r1 r2 r3
      0x01f6: PHI (r1v17 boolean) = (r1v13 boolean), (r1v22 boolean) binds: [B:135:0x01f4, B:120:0x01da] A[DONT_GENERATE, DONT_INLINE]
      0x01f6: PHI (r2v15 boolean) = (r2v11 boolean), (r2v18 boolean) binds: [B:135:0x01f4, B:120:0x01da] A[DONT_GENERATE, DONT_INLINE]
      0x01f6: PHI (r3v10 boolean) = (r3v6 boolean), (r3v12 boolean) binds: [B:135:0x01f4, B:120:0x01da] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:137:0x01f8 A[PHI: r1 r2 r3
      0x01f8: PHI (r1v15 boolean) = (r1v13 boolean), (r1v13 boolean), (r1v22 boolean), (r1v22 boolean) binds: [B:134:0x01f2, B:135:0x01f4, B:119:0x01d8, B:120:0x01da] A[DONT_GENERATE, DONT_INLINE]
      0x01f8: PHI (r2v13 boolean) = (r2v11 boolean), (r2v11 boolean), (r2v18 boolean), (r2v18 boolean) binds: [B:134:0x01f2, B:135:0x01f4, B:119:0x01d8, B:120:0x01da] A[DONT_GENERATE, DONT_INLINE]
      0x01f8: PHI (r3v8 boolean) = (r3v6 boolean), (r3v6 boolean), (r3v12 boolean), (r3v12 boolean) binds: [B:134:0x01f2, B:135:0x01f4, B:119:0x01d8, B:120:0x01da] A[DONT_GENERATE, DONT_INLINE]] */
    public static GenericDraweeHierarchyBuilder updateBuilder(GenericDraweeHierarchyBuilder genericDraweeHierarchyBuilder, Context context, @Nullable AttributeSet attributeSet) throws Throwable {
        boolean z;
        int i;
        boolean z2;
        boolean z3;
        boolean z4;
        int integer;
        int i2;
        Context context2 = context;
        if (attributeSet != null) {
            TypedArray typedArrayObtainStyledAttributes = context2.obtainStyledAttributes(attributeSet, R.styleable.GenericDraweeHierarchy);
            try {
                int indexCount = typedArrayObtainStyledAttributes.getIndexCount();
                boolean z5 = true;
                integer = 0;
                int i3 = 0;
                boolean z6 = true;
                boolean z7 = true;
                boolean z8 = true;
                boolean z9 = true;
                boolean z10 = true;
                boolean z11 = true;
                boolean z12 = true;
                int dimensionPixelSize = 0;
                while (i3 < indexCount) {
                    try {
                        int index = typedArrayObtainStyledAttributes.getIndex(i3);
                        if (index == R.styleable.GenericDraweeHierarchy_actualImageScaleType) {
                            genericDraweeHierarchyBuilder.setActualImageScaleType(getScaleTypeFromXml(typedArrayObtainStyledAttributes, index));
                        } else if (index == R.styleable.GenericDraweeHierarchy_placeholderImage) {
                            genericDraweeHierarchyBuilder.setPlaceholderImage(getDrawable(context2, typedArrayObtainStyledAttributes, index));
                        } else if (index == R.styleable.GenericDraweeHierarchy_pressedStateOverlayImage) {
                            genericDraweeHierarchyBuilder.setPressedStateOverlay(getDrawable(context2, typedArrayObtainStyledAttributes, index));
                        } else if (index == R.styleable.GenericDraweeHierarchy_progressBarImage) {
                            genericDraweeHierarchyBuilder.setProgressBarImage(getDrawable(context2, typedArrayObtainStyledAttributes, index));
                        } else if (index == R.styleable.GenericDraweeHierarchy_fadeDuration) {
                            genericDraweeHierarchyBuilder.setFadeDuration(typedArrayObtainStyledAttributes.getInt(index, 0));
                        } else if (index == R.styleable.GenericDraweeHierarchy_viewAspectRatio) {
                            genericDraweeHierarchyBuilder.setDesiredAspectRatio(typedArrayObtainStyledAttributes.getFloat(index, 0.0f));
                        } else if (index == R.styleable.GenericDraweeHierarchy_placeholderImageScaleType) {
                            genericDraweeHierarchyBuilder.setPlaceholderImageScaleType(getScaleTypeFromXml(typedArrayObtainStyledAttributes, index));
                        } else if (index == R.styleable.GenericDraweeHierarchy_retryImage) {
                            genericDraweeHierarchyBuilder.setRetryImage(getDrawable(context2, typedArrayObtainStyledAttributes, index));
                        } else if (index == R.styleable.GenericDraweeHierarchy_retryImageScaleType) {
                            genericDraweeHierarchyBuilder.setRetryImageScaleType(getScaleTypeFromXml(typedArrayObtainStyledAttributes, index));
                        } else if (index == R.styleable.GenericDraweeHierarchy_failureImage) {
                            genericDraweeHierarchyBuilder.setFailureImage(getDrawable(context2, typedArrayObtainStyledAttributes, index));
                        } else if (index == R.styleable.GenericDraweeHierarchy_failureImageScaleType) {
                            genericDraweeHierarchyBuilder.setFailureImageScaleType(getScaleTypeFromXml(typedArrayObtainStyledAttributes, index));
                        } else if (index == R.styleable.GenericDraweeHierarchy_progressBarImageScaleType) {
                            genericDraweeHierarchyBuilder.setProgressBarImageScaleType(getScaleTypeFromXml(typedArrayObtainStyledAttributes, index));
                        } else {
                            if (index == R.styleable.GenericDraweeHierarchy_progressBarAutoRotateInterval) {
                                integer = typedArrayObtainStyledAttributes.getInteger(index, integer);
                            } else if (index == R.styleable.GenericDraweeHierarchy_backgroundImage) {
                                genericDraweeHierarchyBuilder.setBackground(getDrawable(context2, typedArrayObtainStyledAttributes, index));
                            } else if (index == R.styleable.GenericDraweeHierarchy_overlayImage) {
                                genericDraweeHierarchyBuilder.setOverlay(getDrawable(context2, typedArrayObtainStyledAttributes, index));
                            } else {
                                if (index == R.styleable.GenericDraweeHierarchy_roundAsCircle) {
                                    getRoundingParams(genericDraweeHierarchyBuilder).setRoundAsCircle(typedArrayObtainStyledAttributes.getBoolean(index, false));
                                } else if (index == R.styleable.GenericDraweeHierarchy_roundedCornerRadius) {
                                    dimensionPixelSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(index, dimensionPixelSize);
                                } else {
                                    int i4 = dimensionPixelSize;
                                    if (index == R.styleable.GenericDraweeHierarchy_roundTopLeft) {
                                        z6 = typedArrayObtainStyledAttributes.getBoolean(index, z6);
                                    } else if (index == R.styleable.GenericDraweeHierarchy_roundTopRight) {
                                        z9 = typedArrayObtainStyledAttributes.getBoolean(index, z9);
                                    } else if (index == R.styleable.GenericDraweeHierarchy_roundBottomLeft) {
                                        z5 = typedArrayObtainStyledAttributes.getBoolean(index, z5);
                                    } else if (index == R.styleable.GenericDraweeHierarchy_roundBottomRight) {
                                        z10 = typedArrayObtainStyledAttributes.getBoolean(index, z10);
                                    } else if (index == R.styleable.GenericDraweeHierarchy_roundTopStart) {
                                        z7 = typedArrayObtainStyledAttributes.getBoolean(index, z7);
                                    } else if (index == R.styleable.GenericDraweeHierarchy_roundTopEnd) {
                                        z8 = typedArrayObtainStyledAttributes.getBoolean(index, z8);
                                    } else if (index == R.styleable.GenericDraweeHierarchy_roundBottomStart) {
                                        z12 = typedArrayObtainStyledAttributes.getBoolean(index, z12);
                                    } else if (index == R.styleable.GenericDraweeHierarchy_roundBottomEnd) {
                                        z11 = typedArrayObtainStyledAttributes.getBoolean(index, z11);
                                    } else if (index == R.styleable.GenericDraweeHierarchy_roundWithOverlayColor) {
                                        i2 = i4;
                                        getRoundingParams(genericDraweeHierarchyBuilder).setOverlayColor(typedArrayObtainStyledAttributes.getColor(index, 0));
                                    } else {
                                        i2 = i4;
                                        if (index == R.styleable.GenericDraweeHierarchy_roundingBorderWidth) {
                                            getRoundingParams(genericDraweeHierarchyBuilder).setBorderWidth(typedArrayObtainStyledAttributes.getDimensionPixelSize(index, 0));
                                        } else if (index == R.styleable.GenericDraweeHierarchy_roundingBorderColor) {
                                            getRoundingParams(genericDraweeHierarchyBuilder).setBorderColor(typedArrayObtainStyledAttributes.getColor(index, 0));
                                        } else if (index == R.styleable.GenericDraweeHierarchy_roundingBorderPadding) {
                                            getRoundingParams(genericDraweeHierarchyBuilder).setPadding(typedArrayObtainStyledAttributes.getDimensionPixelSize(index, 0));
                                        }
                                    }
                                    dimensionPixelSize = i4;
                                }
                                dimensionPixelSize = i2;
                                i3++;
                                context2 = context;
                            }
                            i3++;
                            context2 = context;
                        }
                        i2 = dimensionPixelSize;
                        dimensionPixelSize = i2;
                        i3++;
                        context2 = context;
                    } catch (Throwable th) {
                        th = th;
                        typedArrayObtainStyledAttributes.recycle();
                        if (Build.VERSION.SDK_INT >= 17) {
                            context.getResources().getConfiguration().getLayoutDirection();
                        }
                        throw th;
                    }
                }
                int i5 = dimensionPixelSize;
                typedArrayObtainStyledAttributes.recycle();
                if (Build.VERSION.SDK_INT < 17 || context.getResources().getConfiguration().getLayoutDirection() != 1) {
                    z2 = z6 && z7;
                    z = z9 && z8;
                    z3 = z10 && z11;
                    if (z5 && z12) {
                        z4 = true;
                    } else {
                        z4 = false;
                    }
                } else {
                    z2 = z6 && z8;
                    z = z9 && z7;
                    z3 = z10 && z12;
                    if (z5 && z11) {
                        z4 = true;
                    } else {
                        z4 = false;
                    }
                }
                i = i5;
            } catch (Throwable th2) {
                th = th2;
            }
        } else {
            z = true;
            i = 0;
            z2 = true;
            z3 = true;
            z4 = true;
            integer = 0;
        }
        if (genericDraweeHierarchyBuilder.getProgressBarImage() != null && integer > 0) {
            genericDraweeHierarchyBuilder.setProgressBarImage(new AutoRotateDrawable(genericDraweeHierarchyBuilder.getProgressBarImage(), integer));
        }
        if (i > 0) {
            getRoundingParams(genericDraweeHierarchyBuilder).setCornersRadii(z2 ? i : 0.0f, z ? i : 0.0f, z3 ? i : 0.0f, z4 ? i : 0.0f);
        }
        return genericDraweeHierarchyBuilder;
    }
}

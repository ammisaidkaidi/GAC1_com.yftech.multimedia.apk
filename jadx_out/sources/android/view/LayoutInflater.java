package android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.os.Trace;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.util.Xml;
import android.widget.FrameLayout;
import com.android.internal.R;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes2.dex */
public abstract class LayoutInflater {
    private static final String ATTR_LAYOUT = "layout";
    private static final boolean DEBUG = false;
    private static final String TAG = "LayoutInflater";
    private static final String TAG_1995 = "blink";
    private static final String TAG_INCLUDE = "include";
    private static final String TAG_MERGE = "merge";
    private static final String TAG_REQUEST_FOCUS = "requestFocus";
    private static final String TAG_TAG = "tag";
    final Object[] mConstructorArgs;
    protected final Context mContext;
    private Factory mFactory;
    private Factory2 mFactory2;
    private boolean mFactorySet;
    private Filter mFilter;
    private HashMap<String, Boolean> mFilterMap;
    private Factory2 mPrivateFactory;
    private TypedValue mTempValue;
    private static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];
    static final Class<?>[] mConstructorSignature = {Context.class, AttributeSet.class};
    private static final HashMap<String, Constructor<? extends View>> sConstructorMap = new HashMap<>();
    private static final int[] ATTRS_THEME = {16842752};
    private static final ClassLoader BOOT_CLASS_LOADER = LayoutInflater.class.getClassLoader();

    private static class BlinkLayout extends FrameLayout {
        private static final int BLINK_DELAY = 500;
        private static final int MESSAGE_BLINK = 66;
        private boolean mBlink;
        private boolean mBlinkState;
        private final Handler mHandler;

        public BlinkLayout(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.mHandler = new Handler(new Handler.Callback() { // from class: android.view.LayoutInflater.BlinkLayout.1
                @Override // android.os.Handler.Callback
                public boolean handleMessage(Message message) {
                    if (message.what != 66) {
                        return false;
                    }
                    if (BlinkLayout.this.mBlink) {
                        BlinkLayout.this.mBlinkState = !BlinkLayout.this.mBlinkState;
                        BlinkLayout.this.makeBlink();
                    }
                    BlinkLayout.this.invalidate();
                    return true;
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void makeBlink() {
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(66), 500L);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            if (this.mBlinkState) {
                super.dispatchDraw(canvas);
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.mBlink = true;
            this.mBlinkState = true;
            makeBlink();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.mBlink = false;
            this.mBlinkState = true;
            this.mHandler.removeMessages(66);
        }
    }

    public interface Factory {
        View onCreateView(String str, Context context, AttributeSet attributeSet);
    }

    public interface Factory2 extends Factory {
        View onCreateView(View view, String str, Context context, AttributeSet attributeSet);
    }

    private static class FactoryMerger implements Factory2 {
        private final Factory mF1;
        private final Factory2 mF12;
        private final Factory mF2;
        private final Factory2 mF22;

        FactoryMerger(Factory factory, Factory2 factory2, Factory factory3, Factory2 factory4) {
            this.mF1 = factory;
            this.mF2 = factory3;
            this.mF12 = factory2;
            this.mF22 = factory4;
        }

        @Override // android.view.LayoutInflater.Factory2
        public View onCreateView(View view, String str, Context context, AttributeSet attributeSet) {
            View viewOnCreateView = this.mF12 != null ? this.mF12.onCreateView(view, str, context, attributeSet) : this.mF1.onCreateView(str, context, attributeSet);
            if (viewOnCreateView != null) {
                return viewOnCreateView;
            }
            return this.mF22 != null ? this.mF22.onCreateView(view, str, context, attributeSet) : this.mF2.onCreateView(str, context, attributeSet);
        }

        @Override // android.view.LayoutInflater.Factory
        public View onCreateView(String str, Context context, AttributeSet attributeSet) {
            View viewOnCreateView = this.mF1.onCreateView(str, context, attributeSet);
            return viewOnCreateView != null ? viewOnCreateView : this.mF2.onCreateView(str, context, attributeSet);
        }
    }

    public interface Filter {
        boolean onLoadClass(Class cls);
    }

    protected LayoutInflater(Context context) {
        this.mConstructorArgs = new Object[2];
        this.mContext = context;
    }

    protected LayoutInflater(LayoutInflater layoutInflater, Context context) {
        this.mConstructorArgs = new Object[2];
        this.mContext = context;
        this.mFactory = layoutInflater.mFactory;
        this.mFactory2 = layoutInflater.mFactory2;
        this.mPrivateFactory = layoutInflater.mPrivateFactory;
        setFilter(layoutInflater.mFilter);
    }

    static final void consumeChildElements(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        int next;
        int depth = xmlPullParser.getDepth();
        do {
            next = xmlPullParser.next();
            if (next == 3 && xmlPullParser.getDepth() <= depth) {
                return;
            }
        } while (next != 1);
    }

    private View createViewFromTag(View view, String str, Context context, AttributeSet attributeSet) {
        return createViewFromTag(view, str, context, attributeSet, false);
    }

    private void failNotAllowed(String str, String str2, AttributeSet attributeSet) {
        StringBuilder sb = new StringBuilder();
        sb.append(attributeSet.getPositionDescription());
        sb.append(": Class not allowed to be inflated ");
        if (str2 != null) {
            str = str2 + str;
        }
        sb.append(str);
        throw new InflateException(sb.toString());
    }

    public static LayoutInflater from(Context context) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (layoutInflater != null) {
            return layoutInflater;
        }
        throw new AssertionError("LayoutInflater not found.");
    }

    private void parseInclude(XmlPullParser xmlPullParser, Context context, View view, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        int next;
        if (!(view instanceof ViewGroup)) {
            throw new InflateException("<include /> can only be used inside of a ViewGroup");
        }
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, ATTRS_THEME);
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(0, 0);
        boolean z = resourceId != 0;
        Context contextThemeWrapper = z ? new ContextThemeWrapper(context, resourceId) : context;
        typedArrayObtainStyledAttributes.recycle();
        ViewGroup.LayoutParams layoutParamsGenerateLayoutParams = null;
        int attributeResourceValue = attributeSet.getAttributeResourceValue(null, "layout", 0);
        if (attributeResourceValue == 0) {
            String attributeValue = attributeSet.getAttributeValue(null, "layout");
            if (attributeValue == null || attributeValue.length() <= 0) {
                throw new InflateException("You must specify a layout in the include tag: <include layout=\"@layout/layoutID\" />");
            }
            attributeResourceValue = contextThemeWrapper.getResources().getIdentifier(attributeValue.substring(1), "attr", contextThemeWrapper.getPackageName());
        }
        if (this.mTempValue == null) {
            this.mTempValue = new TypedValue();
        }
        if (attributeResourceValue != 0 && contextThemeWrapper.getTheme().resolveAttribute(attributeResourceValue, this.mTempValue, true)) {
            attributeResourceValue = this.mTempValue.resourceId;
        }
        if (attributeResourceValue == 0) {
            throw new InflateException("You must specify a valid layout reference. The layout ID " + attributeSet.getAttributeValue(null, "layout") + " is not valid.");
        }
        XmlResourceParser layout = contextThemeWrapper.getResources().getLayout(attributeResourceValue);
        try {
            AttributeSet attributeSetAsAttributeSet = Xml.asAttributeSet(layout);
            do {
                next = layout.next();
                if (next == 2) {
                    break;
                }
            } while (next != 1);
            if (next != 2) {
                throw new InflateException(layout.getPositionDescription() + ": No start tag found!");
            }
            String name = layout.getName();
            if (TAG_MERGE.equals(name)) {
                rInflate(layout, view, contextThemeWrapper, attributeSetAsAttributeSet, false);
            } else {
                View viewCreateViewFromTag = createViewFromTag(view, name, contextThemeWrapper, attributeSetAsAttributeSet, z);
                ViewGroup viewGroup = (ViewGroup) view;
                TypedArray typedArrayObtainStyledAttributes2 = contextThemeWrapper.obtainStyledAttributes(attributeSet, R.styleable.Include);
                int resourceId2 = typedArrayObtainStyledAttributes2.getResourceId(0, -1);
                int i = typedArrayObtainStyledAttributes2.getInt(1, -1);
                typedArrayObtainStyledAttributes2.recycle();
                try {
                    layoutParamsGenerateLayoutParams = viewGroup.generateLayoutParams(attributeSet);
                } catch (RuntimeException unused) {
                }
                if (layoutParamsGenerateLayoutParams == null) {
                    layoutParamsGenerateLayoutParams = viewGroup.generateLayoutParams(attributeSetAsAttributeSet);
                }
                viewCreateViewFromTag.setLayoutParams(layoutParamsGenerateLayoutParams);
                rInflateChildren(layout, viewCreateViewFromTag, attributeSetAsAttributeSet, true);
                if (resourceId2 != -1) {
                    viewCreateViewFromTag.setId(resourceId2);
                }
                switch (i) {
                    case 0:
                        viewCreateViewFromTag.setVisibility(0);
                        break;
                    case 1:
                        viewCreateViewFromTag.setVisibility(4);
                        break;
                    case 2:
                        viewCreateViewFromTag.setVisibility(8);
                        break;
                }
                viewGroup.addView(viewCreateViewFromTag);
            }
            layout.close();
            consumeChildElements(xmlPullParser);
        } catch (Throwable th) {
            layout.close();
            throw th;
        }
    }

    private void parseViewTag(XmlPullParser xmlPullParser, View view, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        TypedArray typedArrayObtainStyledAttributes = view.getContext().obtainStyledAttributes(attributeSet, R.styleable.ViewTag);
        view.setTag(typedArrayObtainStyledAttributes.getResourceId(1, 0), typedArrayObtainStyledAttributes.getText(0));
        typedArrayObtainStyledAttributes.recycle();
        consumeChildElements(xmlPullParser);
    }

    private final boolean verifyClassLoader(Constructor<? extends View> constructor) {
        ClassLoader classLoader = constructor.getDeclaringClass().getClassLoader();
        if (classLoader == BOOT_CLASS_LOADER) {
            return true;
        }
        ClassLoader classLoader2 = this.mContext.getClassLoader();
        while (classLoader != classLoader2) {
            classLoader2 = classLoader2.getParent();
            if (classLoader2 == null) {
                return false;
            }
        }
        return true;
    }

    public abstract LayoutInflater cloneInContext(Context context);

    /* JADX WARN: Code duplicated, block: B:40:0x00aa  */
    /* JADX WARN: Code duplicated, block: B:43:0x00b6 A[Catch: Exception -> 0x00a7, all -> 0x00fa, ClassNotFoundException -> 0x012a, ClassCastException -> 0x012c, NoSuchMethodException -> 0x0161, TRY_LEAVE, TryCatch #2 {Exception -> 0x00a7, blocks: (B:35:0x009d, B:41:0x00ab, B:43:0x00b6), top: B:84:0x009d }] */
    /* JADX WARN: Code duplicated, block: B:50:0x00d0 A[Catch: all -> 0x00fa, Exception -> 0x00fd, ClassNotFoundException -> 0x012a, ClassCastException -> 0x012c, NoSuchMethodException -> 0x0161, TryCatch #0 {Exception -> 0x00fd, blocks: (B:8:0x0019, B:10:0x0020, B:12:0x0028, B:14:0x0039, B:48:0x00c6, B:50:0x00d0, B:51:0x00d6, B:53:0x00e4, B:54:0x00f2, B:25:0x006a, B:27:0x006e, B:29:0x0078, B:31:0x0080, B:33:0x0091, B:45:0x00bb, B:47:0x00c3), top: B:82:0x0019 }] */
    /* JADX WARN: Code duplicated, block: B:53:0x00e4 A[Catch: all -> 0x00fa, Exception -> 0x00fd, ClassNotFoundException -> 0x012a, ClassCastException -> 0x012c, NoSuchMethodException -> 0x0161, TryCatch #0 {Exception -> 0x00fd, blocks: (B:8:0x0019, B:10:0x0020, B:12:0x0028, B:14:0x0039, B:48:0x00c6, B:50:0x00d0, B:51:0x00d6, B:53:0x00e4, B:54:0x00f2, B:25:0x006a, B:27:0x006e, B:29:0x0078, B:31:0x0080, B:33:0x0091, B:45:0x00bb, B:47:0x00c3), top: B:82:0x0019 }] */
    /* JADX WARN: Code duplicated, block: B:62:0x0113  */
    /* JADX WARN: Code duplicated, block: B:63:0x0116 A[Catch: all -> 0x00fa, TryCatch #5 {all -> 0x00fa, blocks: (B:8:0x0019, B:10:0x0020, B:12:0x0028, B:14:0x0039, B:15:0x0043, B:18:0x0049, B:20:0x0051, B:21:0x0054, B:48:0x00c6, B:50:0x00d0, B:51:0x00d6, B:53:0x00e4, B:54:0x00f2, B:60:0x00fe, B:64:0x011a, B:65:0x0129, B:63:0x0116, B:25:0x006a, B:27:0x006e, B:29:0x0078, B:31:0x0080, B:33:0x0091, B:35:0x009d, B:41:0x00ab, B:43:0x00b6, B:45:0x00bb, B:47:0x00c3, B:67:0x012b, B:69:0x012d, B:71:0x0142, B:72:0x0151, B:73:0x0160, B:75:0x0162, B:77:0x0177, B:78:0x0186, B:79:0x0195), top: B:88:0x0019, inners: #6, #7 }] */
    public final View createView(String str, String str2, AttributeSet attributeSet) throws InflateException, ClassNotFoundException {
        String name;
        String str3;
        boolean z;
        Object[] objArr;
        View viewNewInstance;
        String str4;
        Constructor<? extends View> constructor = sConstructorMap.get(str);
        Class cls = null;
        if (constructor != null && !verifyClassLoader(constructor)) {
            sConstructorMap.remove(str);
            constructor = null;
        }
        try {
            try {
                try {
                    try {
                        Trace.traceBegin(8L, str);
                        if (constructor == null) {
                            ClassLoader classLoader = this.mContext.getClassLoader();
                            if (str2 != null) {
                                str4 = str2 + str;
                            } else {
                                str4 = str;
                            }
                            Class clsAsSubclass = classLoader.loadClass(str4).asSubclass(View.class);
                            try {
                                if (this.mFilter != null && clsAsSubclass != null && !this.mFilter.onLoadClass(clsAsSubclass)) {
                                    failNotAllowed(str, str2, attributeSet);
                                }
                                Constructor<? extends View> constructor2 = clsAsSubclass.getConstructor(mConstructorSignature);
                                constructor2.setAccessible(true);
                                sConstructorMap.put(str, constructor2);
                                cls = clsAsSubclass;
                                constructor = constructor2;
                                Object obj = this.mConstructorArgs[0];
                                if (this.mConstructorArgs[0] == null) {
                                    this.mConstructorArgs[0] = this.mContext;
                                }
                                objArr = this.mConstructorArgs;
                                objArr[1] = attributeSet;
                                viewNewInstance = constructor.newInstance(objArr);
                                if (viewNewInstance instanceof ViewStub) {
                                    ((ViewStub) viewNewInstance).setLayoutInflater(cloneInContext((Context) objArr[0]));
                                }
                                this.mConstructorArgs[0] = obj;
                                Trace.traceEnd(8L);
                                return viewNewInstance;
                            } catch (Exception e) {
                                e = e;
                                cls = clsAsSubclass;
                                StringBuilder sb = new StringBuilder();
                                sb.append(attributeSet.getPositionDescription());
                                sb.append(": Error inflating class ");
                                if (cls == null) {
                                    name = MediaStore.UNKNOWN_STRING;
                                } else {
                                    name = cls.getName();
                                }
                                sb.append(name);
                                InflateException inflateException = new InflateException(sb.toString(), e);
                                inflateException.setStackTrace(EMPTY_STACK_TRACE);
                                throw inflateException;
                            }
                        }
                        if (this.mFilter != null) {
                            Boolean bool = this.mFilterMap.get(str);
                            if (bool == null) {
                                ClassLoader classLoader2 = this.mContext.getClassLoader();
                                if (str2 != null) {
                                    str3 = str2 + str;
                                } else {
                                    str3 = str;
                                }
                                Class clsAsSubclass2 = classLoader2.loadClass(str3).asSubclass(View.class);
                                if (clsAsSubclass2 != null) {
                                    try {
                                        if (this.mFilter.onLoadClass(clsAsSubclass2)) {
                                            z = true;
                                        } else {
                                            z = false;
                                        }
                                        this.mFilterMap.put(str, Boolean.valueOf(z));
                                        if (!z) {
                                            failNotAllowed(str, str2, attributeSet);
                                        }
                                        cls = clsAsSubclass2;
                                    } catch (Exception e2) {
                                        e = e2;
                                        cls = clsAsSubclass2;
                                        StringBuilder sb2 = new StringBuilder();
                                        sb2.append(attributeSet.getPositionDescription());
                                        sb2.append(": Error inflating class ");
                                        if (cls == null) {
                                            name = MediaStore.UNKNOWN_STRING;
                                        } else {
                                            name = cls.getName();
                                        }
                                        sb2.append(name);
                                        InflateException inflateException2 = new InflateException(sb2.toString(), e);
                                        inflateException2.setStackTrace(EMPTY_STACK_TRACE);
                                        throw inflateException2;
                                    }
                                } else {
                                    z = false;
                                    this.mFilterMap.put(str, Boolean.valueOf(z));
                                    if (!z) {
                                        failNotAllowed(str, str2, attributeSet);
                                    }
                                    cls = clsAsSubclass2;
                                }
                            } else if (bool.equals(Boolean.FALSE)) {
                                failNotAllowed(str, str2, attributeSet);
                            }
                        }
                        Object obj2 = this.mConstructorArgs[0];
                        if (this.mConstructorArgs[0] == null) {
                            this.mConstructorArgs[0] = this.mContext;
                        }
                        objArr = this.mConstructorArgs;
                        objArr[1] = attributeSet;
                        viewNewInstance = constructor.newInstance(objArr);
                        if (viewNewInstance instanceof ViewStub) {
                            ((ViewStub) viewNewInstance).setLayoutInflater(cloneInContext((Context) objArr[0]));
                        }
                        this.mConstructorArgs[0] = obj2;
                        Trace.traceEnd(8L);
                        return viewNewInstance;
                    } catch (Exception e3) {
                        e = e3;
                    }
                } catch (ClassNotFoundException e4) {
                    throw e4;
                }
            } catch (ClassCastException e5) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append(attributeSet.getPositionDescription());
                sb3.append(": Class is not a View ");
                if (str2 != null) {
                    str = str2 + str;
                }
                sb3.append(str);
                InflateException inflateException3 = new InflateException(sb3.toString(), e5);
                inflateException3.setStackTrace(EMPTY_STACK_TRACE);
                throw inflateException3;
            } catch (NoSuchMethodException e6) {
                StringBuilder sb4 = new StringBuilder();
                sb4.append(attributeSet.getPositionDescription());
                sb4.append(": Error inflating class ");
                if (str2 != null) {
                    str = str2 + str;
                }
                sb4.append(str);
                InflateException inflateException4 = new InflateException(sb4.toString(), e6);
                inflateException4.setStackTrace(EMPTY_STACK_TRACE);
                throw inflateException4;
            }
        } catch (Throwable th) {
            Trace.traceEnd(8L);
            throw th;
        }
    }

    View createViewFromTag(View view, String str, Context context, AttributeSet attributeSet, boolean z) {
        View viewOnCreateView;
        if (str.equals("view")) {
            str = attributeSet.getAttributeValue(null, "class");
        }
        if (!z) {
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, ATTRS_THEME);
            int resourceId = typedArrayObtainStyledAttributes.getResourceId(0, 0);
            if (resourceId != 0) {
                context = new ContextThemeWrapper(context, resourceId);
            }
            typedArrayObtainStyledAttributes.recycle();
        }
        if (str.equals(TAG_1995)) {
            return new BlinkLayout(context, attributeSet);
        }
        try {
            if (this.mFactory2 != null) {
                viewOnCreateView = this.mFactory2.onCreateView(view, str, context, attributeSet);
            } else {
                viewOnCreateView = this.mFactory != null ? this.mFactory.onCreateView(str, context, attributeSet) : null;
            }
            if (viewOnCreateView == null && this.mPrivateFactory != null) {
                viewOnCreateView = this.mPrivateFactory.onCreateView(view, str, context, attributeSet);
            }
            if (viewOnCreateView != null) {
                return viewOnCreateView;
            }
            Object obj = this.mConstructorArgs[0];
            this.mConstructorArgs[0] = context;
            try {
                return -1 == str.indexOf(46) ? onCreateView(view, str, attributeSet) : createView(str, null, attributeSet);
            } finally {
                this.mConstructorArgs[0] = obj;
            }
        } catch (InflateException e) {
            throw e;
        } catch (ClassNotFoundException e2) {
            InflateException inflateException = new InflateException(attributeSet.getPositionDescription() + ": Error inflating class " + str, e2);
            inflateException.setStackTrace(EMPTY_STACK_TRACE);
            throw inflateException;
        } catch (Exception e3) {
            InflateException inflateException2 = new InflateException(attributeSet.getPositionDescription() + ": Error inflating class " + str, e3);
            inflateException2.setStackTrace(EMPTY_STACK_TRACE);
            throw inflateException2;
        }
    }

    public Context getContext() {
        return this.mContext;
    }

    public final Factory getFactory() {
        return this.mFactory;
    }

    public final Factory2 getFactory2() {
        return this.mFactory2;
    }

    public Filter getFilter() {
        return this.mFilter;
    }

    public View inflate(int i, ViewGroup viewGroup) {
        return inflate(i, viewGroup, viewGroup != null);
    }

    public View inflate(int i, ViewGroup viewGroup, boolean z) {
        XmlResourceParser layout = getContext().getResources().getLayout(i);
        try {
            return inflate(layout, viewGroup, z);
        } finally {
            layout.close();
        }
    }

    public View inflate(XmlPullParser xmlPullParser, ViewGroup viewGroup) {
        return inflate(xmlPullParser, viewGroup, viewGroup != null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0, types: [android.view.View, android.view.ViewGroup] */
    /* JADX WARN: Type inference failed for: r0v10 */
    /* JADX WARN: Type inference failed for: r0v11, types: [android.view.View] */
    /* JADX WARN: Type inference failed for: r15v0, types: [android.view.LayoutInflater] */
    public View inflate(XmlPullParser xmlPullParser, ViewGroup viewGroup, boolean z) {
        int next;
        ViewGroup.LayoutParams layoutParams;
        ?? r0 = viewGroup;
        synchronized (this.mConstructorArgs) {
            Trace.traceBegin(8L, "inflate");
            Context context = this.mContext;
            AttributeSet attributeSetAsAttributeSet = Xml.asAttributeSet(xmlPullParser);
            Context context2 = (Context) this.mConstructorArgs[0];
            this.mConstructorArgs[0] = context;
            do {
                try {
                    try {
                        try {
                            next = xmlPullParser.next();
                            if (next == 2) {
                                break;
                            }
                        } catch (Throwable th) {
                            this.mConstructorArgs[0] = context2;
                            this.mConstructorArgs[1] = null;
                            Trace.traceEnd(8L);
                            throw th;
                        }
                    } catch (XmlPullParserException e) {
                        InflateException inflateException = new InflateException(e.getMessage(), e);
                        inflateException.setStackTrace(EMPTY_STACK_TRACE);
                        throw inflateException;
                    }
                } catch (Exception e2) {
                    e = e2;
                }
            } while (next != 1);
            try {
                if (next != 2) {
                    throw new InflateException(xmlPullParser.getPositionDescription() + ": No start tag found!");
                }
                String name = xmlPullParser.getName();
                if (!TAG_MERGE.equals(name)) {
                    View viewCreateViewFromTag = createViewFromTag(r0, name, context, attributeSetAsAttributeSet);
                    if (r0 != 0) {
                        ViewGroup.LayoutParams layoutParamsGenerateLayoutParams = r0.generateLayoutParams(attributeSetAsAttributeSet);
                        if (!z) {
                            viewCreateViewFromTag.setLayoutParams(layoutParamsGenerateLayoutParams);
                        }
                        layoutParams = layoutParamsGenerateLayoutParams;
                    } else {
                        layoutParams = null;
                    }
                    rInflateChildren(xmlPullParser, viewCreateViewFromTag, attributeSetAsAttributeSet, true);
                    if (r0 != 0 && z) {
                        r0.addView(viewCreateViewFromTag, layoutParams);
                    }
                    if (r0 == 0 || !z) {
                        r0 = viewCreateViewFromTag;
                    }
                } else {
                    if (r0 == 0 || !z) {
                        throw new InflateException("<merge /> can be used only with a valid ViewGroup root and attachToRoot=true");
                    }
                    rInflate(xmlPullParser, viewGroup, context, attributeSetAsAttributeSet, false);
                }
                this.mConstructorArgs[0] = context2;
                this.mConstructorArgs[1] = null;
                Trace.traceEnd(8L);
            } catch (Exception e3) {
                e = e3;
                InflateException inflateException2 = new InflateException(xmlPullParser.getPositionDescription() + ": " + e.getMessage(), e);
                inflateException2.setStackTrace(EMPTY_STACK_TRACE);
                throw inflateException2;
            }
        }
        return r0;
    }

    protected View onCreateView(View view, String str, AttributeSet attributeSet) throws ClassNotFoundException {
        return onCreateView(str, attributeSet);
    }

    protected View onCreateView(String str, AttributeSet attributeSet) throws ClassNotFoundException {
        return createView(str, "android.view.", attributeSet);
    }

    void rInflate(XmlPullParser xmlPullParser, View view, Context context, AttributeSet attributeSet, boolean z) throws XmlPullParserException, IOException {
        int depth = xmlPullParser.getDepth();
        boolean z2 = false;
        while (true) {
            int next = xmlPullParser.next();
            if ((next == 3 && xmlPullParser.getDepth() <= depth) || next == 1) {
                break;
            }
            if (next == 2) {
                String name = xmlPullParser.getName();
                if (TAG_REQUEST_FOCUS.equals(name)) {
                    consumeChildElements(xmlPullParser);
                    z2 = true;
                } else if ("tag".equals(name)) {
                    parseViewTag(xmlPullParser, view, attributeSet);
                } else if (TAG_INCLUDE.equals(name)) {
                    if (xmlPullParser.getDepth() == 0) {
                        throw new InflateException("<include /> cannot be the root element");
                    }
                    parseInclude(xmlPullParser, context, view, attributeSet);
                } else {
                    if (TAG_MERGE.equals(name)) {
                        throw new InflateException("<merge /> must be the root element");
                    }
                    View viewCreateViewFromTag = createViewFromTag(view, name, context, attributeSet);
                    ViewGroup viewGroup = (ViewGroup) view;
                    ViewGroup.LayoutParams layoutParamsGenerateLayoutParams = viewGroup.generateLayoutParams(attributeSet);
                    rInflateChildren(xmlPullParser, viewCreateViewFromTag, attributeSet, true);
                    viewGroup.addView(viewCreateViewFromTag, layoutParamsGenerateLayoutParams);
                }
            }
        }
        if (z2) {
            view.restoreDefaultFocus();
        }
        if (z) {
            view.onFinishInflate();
        }
    }

    final void rInflateChildren(XmlPullParser xmlPullParser, View view, AttributeSet attributeSet, boolean z) throws XmlPullParserException, IOException {
        rInflate(xmlPullParser, view, view.getContext(), attributeSet, z);
    }

    public void setFactory(Factory factory) {
        if (this.mFactorySet) {
            throw new IllegalStateException("A factory has already been set on this LayoutInflater");
        }
        if (factory == null) {
            throw new NullPointerException("Given factory can not be null");
        }
        this.mFactorySet = true;
        if (this.mFactory == null) {
            this.mFactory = factory;
        } else {
            this.mFactory = new FactoryMerger(factory, null, this.mFactory, this.mFactory2);
        }
    }

    public void setFactory2(Factory2 factory2) {
        if (this.mFactorySet) {
            throw new IllegalStateException("A factory has already been set on this LayoutInflater");
        }
        if (factory2 == null) {
            throw new NullPointerException("Given factory can not be null");
        }
        this.mFactorySet = true;
        if (this.mFactory == null) {
            this.mFactory2 = factory2;
            this.mFactory = factory2;
        } else {
            FactoryMerger factoryMerger = new FactoryMerger(factory2, factory2, this.mFactory, this.mFactory2);
            this.mFactory2 = factoryMerger;
            this.mFactory = factoryMerger;
        }
    }

    public void setFilter(Filter filter) {
        this.mFilter = filter;
        if (filter != null) {
            this.mFilterMap = new HashMap<>();
        }
    }

    public void setPrivateFactory(Factory2 factory2) {
        if (this.mPrivateFactory == null) {
            this.mPrivateFactory = factory2;
        } else {
            this.mPrivateFactory = new FactoryMerger(factory2, factory2, this.mPrivateFactory, this.mPrivateFactory);
        }
    }
}

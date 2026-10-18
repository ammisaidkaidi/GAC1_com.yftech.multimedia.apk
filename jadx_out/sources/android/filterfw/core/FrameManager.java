package android.filterfw.core;

/* JADX INFO: loaded from: classes.dex */
public abstract class FrameManager {
    private FilterContext mContext;

    public Frame duplicateFrame(Frame frame) {
        Frame frameNewFrame = newFrame(frame.getFormat());
        frameNewFrame.setDataFromFrame(frame);
        return frameNewFrame;
    }

    public Frame duplicateFrameToTarget(Frame frame, int i) {
        MutableFrameFormat mutableFrameFormatMutableCopy = frame.getFormat().mutableCopy();
        mutableFrameFormatMutableCopy.setTarget(i);
        Frame frameNewFrame = newFrame(mutableFrameFormatMutableCopy);
        frameNewFrame.setDataFromFrame(frame);
        return frameNewFrame;
    }

    public FilterContext getContext() {
        return this.mContext;
    }

    public GLEnvironment getGLEnvironment() {
        if (this.mContext != null) {
            return this.mContext.getGLEnvironment();
        }
        return null;
    }

    public abstract Frame newBoundFrame(FrameFormat frameFormat, int i, long j);

    public abstract Frame newFrame(FrameFormat frameFormat);

    public abstract Frame releaseFrame(Frame frame);

    public abstract Frame retainFrame(Frame frame);

    void setContext(FilterContext filterContext) {
        this.mContext = filterContext;
    }

    public void tearDown() {
    }
}

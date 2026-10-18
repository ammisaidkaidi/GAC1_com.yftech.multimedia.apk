package android.filterfw.core;

/* JADX INFO: loaded from: classes.dex */
public abstract class Scheduler {
    private FilterGraph mGraph;

    Scheduler(FilterGraph filterGraph) {
        this.mGraph = filterGraph;
    }

    boolean finished() {
        return true;
    }

    FilterGraph getGraph() {
        return this.mGraph;
    }

    abstract void reset();

    abstract Filter scheduleNextNode();
}

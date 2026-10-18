package android.filterfw.core;

/* JADX INFO: loaded from: classes.dex */
public class SimpleScheduler extends Scheduler {
    public SimpleScheduler(FilterGraph filterGraph) {
        super(filterGraph);
    }

    @Override // android.filterfw.core.Scheduler
    public void reset() {
    }

    @Override // android.filterfw.core.Scheduler
    public Filter scheduleNextNode() {
        for (Filter filter : getGraph().getFilters()) {
            if (filter.canProcess()) {
                return filter;
            }
        }
        return null;
    }
}

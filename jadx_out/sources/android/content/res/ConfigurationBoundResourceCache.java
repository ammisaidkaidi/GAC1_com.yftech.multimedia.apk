package android.content.res;

/* JADX INFO: loaded from: classes.dex */
public class ConfigurationBoundResourceCache<T> extends ThemedResourceCache<ConstantState<T>> {
    public T getInstance(long j, Resources resources, Resources.Theme theme) {
        ConstantState constantState = (ConstantState) get(j, theme);
        if (constantState != null) {
            return (T) constantState.newInstance2(resources, theme);
        }
        return null;
    }

    @Override // android.content.res.ThemedResourceCache
    public /* bridge */ /* synthetic */ void onConfigurationChange(int i) {
        super.onConfigurationChange(i);
    }

    @Override // android.content.res.ThemedResourceCache
    public boolean shouldInvalidateEntry(ConstantState<T> constantState, int i) {
        return Configuration.needNewResources(i, constantState.getChangingConfigurations());
    }
}

package android.app;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.android.internal.R;

/* JADX INFO: loaded from: classes.dex */
public class ListActivity extends Activity {
    protected ListAdapter mAdapter;
    protected ListView mList;
    private Handler mHandler = new Handler();
    private boolean mFinishedStart = false;
    private Runnable mRequestFocus = new Runnable() { // from class: android.app.ListActivity.1
        @Override // java.lang.Runnable
        public void run() {
            ListActivity.this.mList.focusableViewAvailable(ListActivity.this.mList);
        }
    };
    private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() { // from class: android.app.ListActivity.2
        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            ListActivity.this.onListItemClick((ListView) adapterView, view, i, j);
        }
    };

    private void ensureList() {
        if (this.mList != null) {
            return;
        }
        setContentView(R.layout.list_content_simple);
    }

    public ListAdapter getListAdapter() {
        return this.mAdapter;
    }

    public ListView getListView() {
        ensureList();
        return this.mList;
    }

    public long getSelectedItemId() {
        return this.mList.getSelectedItemId();
    }

    public int getSelectedItemPosition() {
        return this.mList.getSelectedItemPosition();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onContentChanged() {
        super.onContentChanged();
        View viewFindViewById = findViewById(16908292);
        this.mList = (ListView) findViewById(16908298);
        if (this.mList == null) {
            throw new RuntimeException("Your content must have a ListView whose id attribute is 'android.R.id.list'");
        }
        if (viewFindViewById != null) {
            this.mList.setEmptyView(viewFindViewById);
        }
        this.mList.setOnItemClickListener(this.mOnClickListener);
        if (this.mFinishedStart) {
            setListAdapter(this.mAdapter);
        }
        this.mHandler.post(this.mRequestFocus);
        this.mFinishedStart = true;
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        this.mHandler.removeCallbacks(this.mRequestFocus);
        super.onDestroy();
    }

    protected void onListItemClick(ListView listView, View view, int i, long j) {
    }

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle bundle) {
        ensureList();
        super.onRestoreInstanceState(bundle);
    }

    public void setListAdapter(ListAdapter listAdapter) {
        synchronized (this) {
            ensureList();
            this.mAdapter = listAdapter;
            this.mList.setAdapter(listAdapter);
        }
    }

    public void setSelection(int i) {
        this.mList.setSelection(i);
    }
}

package com.media.common.util;

import android.util.SparseArray;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import java.util.Stack;

/* JADX INFO: loaded from: classes3.dex */
public class MyFragmentManger {
    private static volatile MyFragmentManger instance;
    private int photoLayoutId;
    private int videoPlayerLayoutId;
    private FragmentManager fragmentManager = null;
    private Fragment currentFragment = null;
    private SparseArray<Stack<Fragment>> fragments = new SparseArray<>();

    private MyFragmentManger() {
    }

    public static MyFragmentManger getInstance() {
        if (instance == null) {
            synchronized (MyFragmentManger.class) {
                if (instance == null) {
                    instance = new MyFragmentManger();
                }
            }
        }
        return instance;
    }

    public SparseArray<Stack<Fragment>> getAllFragments() {
        return this.fragments;
    }

    public Stack<Fragment> getFragmentStack(int i) {
        LogUtil.d("getFragmentStack:index=" + i + ", fragments:" + this.fragments);
        if (this.fragments != null) {
            return this.fragments.get(i);
        }
        return null;
    }

    public int getPhotoLayoutId() {
        return this.photoLayoutId;
    }

    public int getVideoPlayerLayoutId() {
        return this.videoPlayerLayoutId;
    }

    public void init(FragmentManager fragmentManager) {
        LogUtil.d("init FragmentManager");
        this.fragmentManager = fragmentManager;
        this.currentFragment = null;
        if (this.fragments != null) {
            this.fragments.clear();
        }
    }

    public synchronized void pushFragment(int i, Fragment fragment) {
        LogUtil.d("pushFragment:index=" + i);
        if (this.fragments == null) {
            this.fragments = new SparseArray<>();
        }
        Stack<Fragment> stack = this.fragments.get(i);
        if (stack == null) {
            Stack<Fragment> stack2 = new Stack<>();
            stack2.push(fragment);
            this.fragments.append(i, stack2);
        } else {
            stack.push(fragment);
        }
    }

    public void release() {
        LogUtil.d("release MyFragmentManger");
        if (this.fragments == null) {
            return;
        }
        for (int i = 0; i < this.fragments.size(); i++) {
            Stack<Fragment> fragmentStack = getInstance().getFragmentStack(this.fragments.keyAt(i));
            if (fragmentStack != null) {
                FragmentTransaction fragmentTransactionBeginTransaction = this.fragmentManager.beginTransaction();
                while (fragmentStack.size() > 0) {
                    fragmentTransactionBeginTransaction.remove(fragmentStack.pop());
                }
                fragmentTransactionBeginTransaction.commitAllowingStateLoss();
            }
        }
        this.currentFragment = null;
        this.fragmentManager = null;
        this.fragments.clear();
        this.fragments = null;
    }

    public void removeFragmentStack(int i) {
        LogUtil.d("removeFragmentStack:index=" + i);
        if (this.fragments != null) {
            this.fragments.remove(i);
        }
    }

    public void setPhotoLayoutId(int i) {
        this.photoLayoutId = i;
    }

    public void setVideoPlayerLayoutId(int i) {
        this.videoPlayerLayoutId = i;
    }

    public synchronized void switchContent(Fragment fragment, int i) {
        if (this.fragmentManager == null) {
            throw new IllegalArgumentException("fragmentManager is null");
        }
        FragmentTransaction fragmentTransactionBeginTransaction = this.fragmentManager.beginTransaction();
        if (this.currentFragment == null && fragment.isAdded()) {
            fragmentTransactionBeginTransaction.show(fragment).commitNowAllowingStateLoss();
        } else if (this.currentFragment == null) {
            fragmentTransactionBeginTransaction.add(i, fragment).commitNowAllowingStateLoss();
        } else if (this.currentFragment != fragment) {
            if (fragment.isAdded()) {
                fragmentTransactionBeginTransaction.hide(this.currentFragment).show(fragment).commitNowAllowingStateLoss();
            } else {
                fragmentTransactionBeginTransaction.hide(this.currentFragment).add(i, fragment).commitNowAllowingStateLoss();
            }
        }
        this.currentFragment = fragment;
    }
}

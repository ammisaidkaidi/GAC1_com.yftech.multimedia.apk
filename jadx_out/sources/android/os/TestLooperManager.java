package android.os;

import android.util.ArraySet;
import java.util.concurrent.LinkedBlockingQueue;

/* JADX INFO: loaded from: classes2.dex */
public class TestLooperManager {
    private static final ArraySet<Looper> sHeldLoopers = new ArraySet<>();
    private final LinkedBlockingQueue<MessageExecution> mExecuteQueue = new LinkedBlockingQueue<>();
    private final Looper mLooper;
    private boolean mLooperBlocked;
    private final MessageQueue mQueue;
    private boolean mReleased;

    private class LooperHolder implements Runnable {
        private LooperHolder() {
        }

        private void processMessage(MessageExecution messageExecution) {
            synchronized (messageExecution) {
                try {
                    messageExecution.m.target.dispatchMessage(messageExecution.m);
                    messageExecution.response = null;
                } catch (Throwable th) {
                    messageExecution.response = th;
                }
                messageExecution.notifyAll();
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (TestLooperManager.this) {
                TestLooperManager.this.mLooperBlocked = true;
                TestLooperManager.this.notify();
            }
            while (!TestLooperManager.this.mReleased) {
                try {
                    MessageExecution messageExecution = (MessageExecution) TestLooperManager.this.mExecuteQueue.take();
                    if (messageExecution.m != null) {
                        processMessage(messageExecution);
                    }
                } catch (InterruptedException unused) {
                }
            }
            synchronized (TestLooperManager.this) {
                TestLooperManager.this.mLooperBlocked = false;
            }
        }
    }

    private static class MessageExecution {
        private Message m;
        private Throwable response;

        private MessageExecution() {
        }
    }

    public TestLooperManager(Looper looper) {
        synchronized (sHeldLoopers) {
            if (sHeldLoopers.contains(looper)) {
                throw new RuntimeException("TestLooperManager already held for this looper");
            }
            sHeldLoopers.add(looper);
        }
        this.mLooper = looper;
        this.mQueue = this.mLooper.getQueue();
        new Handler(looper).post(new LooperHolder());
    }

    private void checkReleased() {
        if (this.mReleased) {
            throw new RuntimeException("release() has already be called");
        }
    }

    public void execute(Message message) {
        checkReleased();
        if (Looper.myLooper() == this.mLooper) {
            message.target.dispatchMessage(message);
            return;
        }
        MessageExecution messageExecution = new MessageExecution();
        messageExecution.m = message;
        synchronized (messageExecution) {
            this.mExecuteQueue.add(messageExecution);
            try {
                messageExecution.wait();
            } catch (InterruptedException unused) {
            }
            if (messageExecution.response != null) {
                throw new RuntimeException(messageExecution.response);
            }
        }
    }

    public MessageQueue getMessageQueue() {
        checkReleased();
        return this.mQueue;
    }

    @Deprecated
    public MessageQueue getQueue() {
        return getMessageQueue();
    }

    public boolean hasMessages(Handler handler, Object obj, int i) {
        checkReleased();
        return this.mQueue.hasMessages(handler, i, obj);
    }

    public boolean hasMessages(Handler handler, Object obj, Runnable runnable) {
        checkReleased();
        return this.mQueue.hasMessages(handler, runnable, obj);
    }

    public Message next() {
        while (!this.mLooperBlocked) {
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException unused) {
                }
            }
        }
        checkReleased();
        return this.mQueue.next();
    }

    public void recycle(Message message) {
        checkReleased();
        message.recycleUnchecked();
    }

    public void release() {
        synchronized (sHeldLoopers) {
            sHeldLoopers.remove(this.mLooper);
        }
        checkReleased();
        this.mReleased = true;
        this.mExecuteQueue.add(new MessageExecution());
    }
}

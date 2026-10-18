package com.facebook.imagepipeline.producers;

import android.os.SystemClock;
import androidx.annotation.VisibleForTesting;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.instrumentation.FrescoInstrumenter;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.concurrent.GuardedBy;

/* JADX INFO: loaded from: classes3.dex */
public class JobScheduler {
    static final String QUEUE_TIME_KEY = "queueTime";
    private final Executor mExecutor;
    private final JobRunnable mJobRunnable;
    private final int mMinimumJobIntervalMs;
    private final Runnable mDoJobRunnable = new Runnable() { // from class: com.facebook.imagepipeline.producers.JobScheduler.1
        @Override // java.lang.Runnable
        public void run() {
            JobScheduler.this.doJob();
        }
    };
    private final Runnable mSubmitJobRunnable = new Runnable() { // from class: com.facebook.imagepipeline.producers.JobScheduler.2
        @Override // java.lang.Runnable
        public void run() {
            JobScheduler.this.submitJob();
        }
    };

    @GuardedBy("this")
    @VisibleForTesting
    EncodedImage mEncodedImage = null;

    @GuardedBy("this")
    @VisibleForTesting
    int mStatus = 0;

    @GuardedBy("this")
    @VisibleForTesting
    JobState mJobState = JobState.IDLE;

    @GuardedBy("this")
    @VisibleForTesting
    long mJobSubmitTime = 0;

    @GuardedBy("this")
    @VisibleForTesting
    long mJobStartTime = 0;

    public interface JobRunnable {
        void run(EncodedImage encodedImage, int i);
    }

    @VisibleForTesting
    static class JobStartExecutorSupplier {
        private static ScheduledExecutorService sJobStarterExecutor;

        JobStartExecutorSupplier() {
        }

        static ScheduledExecutorService get() {
            if (sJobStarterExecutor == null) {
                sJobStarterExecutor = Executors.newSingleThreadScheduledExecutor();
            }
            return sJobStarterExecutor;
        }
    }

    @VisibleForTesting
    enum JobState {
        IDLE,
        QUEUED,
        RUNNING,
        RUNNING_AND_PENDING
    }

    public JobScheduler(Executor executor, JobRunnable jobRunnable, int i) {
        this.mExecutor = executor;
        this.mJobRunnable = jobRunnable;
        this.mMinimumJobIntervalMs = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doJob() {
        EncodedImage encodedImage;
        int i;
        long jUptimeMillis = SystemClock.uptimeMillis();
        synchronized (this) {
            encodedImage = this.mEncodedImage;
            i = this.mStatus;
            this.mEncodedImage = null;
            this.mStatus = 0;
            this.mJobState = JobState.RUNNING;
            this.mJobStartTime = jUptimeMillis;
        }
        try {
            if (shouldProcess(encodedImage, i)) {
                this.mJobRunnable.run(encodedImage, i);
            }
        } finally {
            EncodedImage.closeSafely(encodedImage);
            onJobFinished();
        }
    }

    private void enqueueJob(long j) {
        Runnable runnableDecorateRunnable = FrescoInstrumenter.decorateRunnable(this.mSubmitJobRunnable, "JobScheduler_enqueueJob");
        if (j > 0) {
            JobStartExecutorSupplier.get().schedule(runnableDecorateRunnable, j, TimeUnit.MILLISECONDS);
        } else {
            runnableDecorateRunnable.run();
        }
    }

    private void onJobFinished() {
        long jMax;
        boolean z;
        long jUptimeMillis = SystemClock.uptimeMillis();
        synchronized (this) {
            if (this.mJobState == JobState.RUNNING_AND_PENDING) {
                jMax = Math.max(this.mJobStartTime + ((long) this.mMinimumJobIntervalMs), jUptimeMillis);
                z = true;
                this.mJobSubmitTime = jUptimeMillis;
                this.mJobState = JobState.QUEUED;
            } else {
                this.mJobState = JobState.IDLE;
                jMax = 0;
                z = false;
            }
        }
        if (z) {
            enqueueJob(jMax - jUptimeMillis);
        }
    }

    private static boolean shouldProcess(EncodedImage encodedImage, int i) {
        return BaseConsumer.isLast(i) || BaseConsumer.statusHasFlag(i, 4) || EncodedImage.isValid(encodedImage);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void submitJob() {
        this.mExecutor.execute(FrescoInstrumenter.decorateRunnable(this.mDoJobRunnable, "JobScheduler_submitJob"));
    }

    public void clearJob() {
        EncodedImage encodedImage;
        synchronized (this) {
            encodedImage = this.mEncodedImage;
            this.mEncodedImage = null;
            this.mStatus = 0;
        }
        EncodedImage.closeSafely(encodedImage);
    }

    public synchronized long getQueuedTime() {
        return this.mJobStartTime - this.mJobSubmitTime;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public boolean scheduleJob() {
        long j;
        long jUptimeMillis = SystemClock.uptimeMillis();
        synchronized (this) {
            boolean z = false;
            if (!shouldProcess(this.mEncodedImage, this.mStatus)) {
                return false;
            }
            switch (this.mJobState) {
                case IDLE:
                    long jMax = Math.max(this.mJobStartTime + ((long) this.mMinimumJobIntervalMs), jUptimeMillis);
                    this.mJobSubmitTime = jUptimeMillis;
                    this.mJobState = JobState.QUEUED;
                    j = jMax;
                    z = true;
                    break;
                case QUEUED:
                    j = 0;
                    break;
                case RUNNING:
                    this.mJobState = JobState.RUNNING_AND_PENDING;
                    j = 0;
                    break;
                default:
                    j = 0;
                    break;
            }
            if (z) {
                enqueueJob(j - jUptimeMillis);
            }
            return true;
        }
    }

    public boolean updateJob(EncodedImage encodedImage, int i) {
        EncodedImage encodedImage2;
        if (!shouldProcess(encodedImage, i)) {
            return false;
        }
        synchronized (this) {
            encodedImage2 = this.mEncodedImage;
            this.mEncodedImage = EncodedImage.cloneOrNull(encodedImage);
            this.mStatus = i;
        }
        EncodedImage.closeSafely(encodedImage2);
        return true;
    }
}

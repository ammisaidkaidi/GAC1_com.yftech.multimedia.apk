package org.greenrobot.eventbus;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;

/* JADX INFO: loaded from: classes4.dex */
public class EventBus {
    public static String TAG = "EventBus";
    static volatile EventBus defaultInstance;
    private final AsyncPoster asyncPoster;
    private final BackgroundPoster backgroundPoster;
    private final ThreadLocal<PostingThreadState> currentPostingThreadState;
    private final boolean eventInheritance;
    private final ExecutorService executorService;
    private final int indexCount;
    private final boolean logNoSubscriberMessages;
    private final boolean logSubscriberExceptions;
    private final Logger logger;
    private final Poster mainThreadPoster;
    private final MainThreadSupport mainThreadSupport;
    private final boolean sendNoSubscriberEvent;
    private final boolean sendSubscriberExceptionEvent;
    private final Map<Class<?>, Object> stickyEvents;
    private final SubscriberMethodFinder subscriberMethodFinder;
    private final Map<Class<?>, CopyOnWriteArrayList<Subscription>> subscriptionsByEventType;
    private final boolean throwSubscriberException;
    private final Map<Object, List<Class<?>>> typesBySubscriber;
    private static final EventBusBuilder DEFAULT_BUILDER = new EventBusBuilder();
    private static final Map<Class<?>, List<Class<?>>> eventTypesCache = new HashMap();

    interface PostCallback {
        void onPostCompleted(List<SubscriberExceptionEvent> list);
    }

    static final class PostingThreadState {
        boolean canceled;
        Object event;
        final List<Object> eventQueue = new ArrayList();
        boolean isMainThread;
        boolean isPosting;
        Subscription subscription;

        PostingThreadState() {
        }
    }

    public EventBus() {
        this(DEFAULT_BUILDER);
    }

    EventBus(EventBusBuilder eventBusBuilder) {
        this.currentPostingThreadState = new ThreadLocal<PostingThreadState>() { // from class: org.greenrobot.eventbus.EventBus.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // java.lang.ThreadLocal
            public PostingThreadState initialValue() {
                return new PostingThreadState();
            }
        };
        this.logger = eventBusBuilder.getLogger();
        this.subscriptionsByEventType = new HashMap();
        this.typesBySubscriber = new HashMap();
        this.stickyEvents = new ConcurrentHashMap();
        this.mainThreadSupport = eventBusBuilder.getMainThreadSupport();
        this.mainThreadPoster = this.mainThreadSupport != null ? this.mainThreadSupport.createPoster(this) : null;
        this.backgroundPoster = new BackgroundPoster(this);
        this.asyncPoster = new AsyncPoster(this);
        this.indexCount = eventBusBuilder.subscriberInfoIndexes != null ? eventBusBuilder.subscriberInfoIndexes.size() : 0;
        this.subscriberMethodFinder = new SubscriberMethodFinder(eventBusBuilder.subscriberInfoIndexes, eventBusBuilder.strictMethodVerification, eventBusBuilder.ignoreGeneratedIndex);
        this.logSubscriberExceptions = eventBusBuilder.logSubscriberExceptions;
        this.logNoSubscriberMessages = eventBusBuilder.logNoSubscriberMessages;
        this.sendSubscriberExceptionEvent = eventBusBuilder.sendSubscriberExceptionEvent;
        this.sendNoSubscriberEvent = eventBusBuilder.sendNoSubscriberEvent;
        this.throwSubscriberException = eventBusBuilder.throwSubscriberException;
        this.eventInheritance = eventBusBuilder.eventInheritance;
        this.executorService = eventBusBuilder.executorService;
    }

    static void addInterfaces(List<Class<?>> list, Class<?>[] clsArr) {
        for (Class<?> cls : clsArr) {
            if (!list.contains(cls)) {
                list.add(cls);
                addInterfaces(list, cls.getInterfaces());
            }
        }
    }

    public static EventBusBuilder builder() {
        return new EventBusBuilder();
    }

    private void checkPostStickyEventToSubscription(Subscription subscription, Object obj) {
        if (obj != null) {
            postToSubscription(subscription, obj, isMainThread());
        }
    }

    public static void clearCaches() {
        SubscriberMethodFinder.clearCaches();
        eventTypesCache.clear();
    }

    public static EventBus getDefault() {
        EventBus eventBus = defaultInstance;
        if (eventBus == null) {
            synchronized (EventBus.class) {
                eventBus = defaultInstance;
                if (eventBus == null) {
                    eventBus = new EventBus();
                    defaultInstance = eventBus;
                }
            }
        }
        return eventBus;
    }

    private void handleSubscriberException(Subscription subscription, Object obj, Throwable th) {
        if (!(obj instanceof SubscriberExceptionEvent)) {
            if (this.throwSubscriberException) {
                throw new EventBusException("Invoking subscriber failed", th);
            }
            if (this.logSubscriberExceptions) {
                this.logger.log(Level.SEVERE, "Could not dispatch event: " + obj.getClass() + " to subscribing class " + subscription.subscriber.getClass(), th);
            }
            if (this.sendSubscriberExceptionEvent) {
                post(new SubscriberExceptionEvent(this, th, obj, subscription.subscriber));
                return;
            }
            return;
        }
        if (this.logSubscriberExceptions) {
            this.logger.log(Level.SEVERE, "SubscriberExceptionEvent subscriber " + subscription.subscriber.getClass() + " threw an exception", th);
            SubscriberExceptionEvent subscriberExceptionEvent = (SubscriberExceptionEvent) obj;
            this.logger.log(Level.SEVERE, "Initial event " + subscriberExceptionEvent.causingEvent + " caused exception in " + subscriberExceptionEvent.causingSubscriber, subscriberExceptionEvent.throwable);
        }
    }

    private boolean isMainThread() {
        return this.mainThreadSupport == null || this.mainThreadSupport.isMainThread();
    }

    private static List<Class<?>> lookupAllEventTypes(Class<?> cls) {
        List<Class<?>> arrayList;
        synchronized (eventTypesCache) {
            arrayList = eventTypesCache.get(cls);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                for (Class<?> superclass = cls; superclass != null; superclass = superclass.getSuperclass()) {
                    arrayList.add(superclass);
                    addInterfaces(arrayList, superclass.getInterfaces());
                }
                eventTypesCache.put(cls, arrayList);
            }
        }
        return arrayList;
    }

    private void postSingleEvent(Object obj, PostingThreadState postingThreadState) throws Error {
        boolean zPostSingleEventForEventType;
        Class<?> cls = obj.getClass();
        if (this.eventInheritance) {
            List<Class<?>> listLookupAllEventTypes = lookupAllEventTypes(cls);
            int size = listLookupAllEventTypes.size();
            zPostSingleEventForEventType = false;
            for (int i = 0; i < size; i++) {
                zPostSingleEventForEventType |= postSingleEventForEventType(obj, postingThreadState, listLookupAllEventTypes.get(i));
            }
        } else {
            zPostSingleEventForEventType = postSingleEventForEventType(obj, postingThreadState, cls);
        }
        if (zPostSingleEventForEventType) {
            return;
        }
        if (this.logNoSubscriberMessages) {
            this.logger.log(Level.FINE, "No subscribers registered for event " + cls);
        }
        if (!this.sendNoSubscriberEvent || cls == NoSubscriberEvent.class || cls == SubscriberExceptionEvent.class) {
            return;
        }
        post(new NoSubscriberEvent(this, obj));
    }

    private boolean postSingleEventForEventType(Object obj, PostingThreadState postingThreadState, Class<?> cls) {
        CopyOnWriteArrayList<Subscription> copyOnWriteArrayList;
        synchronized (this) {
            copyOnWriteArrayList = this.subscriptionsByEventType.get(cls);
        }
        if (copyOnWriteArrayList == null || copyOnWriteArrayList.isEmpty()) {
            return false;
        }
        for (Subscription subscription : copyOnWriteArrayList) {
            postingThreadState.event = obj;
            postingThreadState.subscription = subscription;
            try {
                postToSubscription(subscription, obj, postingThreadState.isMainThread);
                boolean z = postingThreadState.canceled;
                postingThreadState.event = null;
                postingThreadState.subscription = null;
                postingThreadState.canceled = false;
                if (z) {
                    return true;
                }
            } catch (Throwable th) {
                postingThreadState.event = null;
                postingThreadState.subscription = null;
                postingThreadState.canceled = false;
                throw th;
            }
        }
        return true;
    }

    private void postToSubscription(Subscription subscription, Object obj, boolean z) {
        switch (subscription.subscriberMethod.threadMode) {
            case POSTING:
                invokeSubscriber(subscription, obj);
                return;
            case MAIN:
                if (z) {
                    invokeSubscriber(subscription, obj);
                    return;
                } else {
                    this.mainThreadPoster.enqueue(subscription, obj);
                    return;
                }
            case MAIN_ORDERED:
                if (this.mainThreadPoster != null) {
                    this.mainThreadPoster.enqueue(subscription, obj);
                    return;
                } else {
                    invokeSubscriber(subscription, obj);
                    return;
                }
            case BACKGROUND:
                if (z) {
                    this.backgroundPoster.enqueue(subscription, obj);
                    return;
                } else {
                    invokeSubscriber(subscription, obj);
                    return;
                }
            case ASYNC:
                this.asyncPoster.enqueue(subscription, obj);
                return;
            default:
                throw new IllegalStateException("Unknown thread mode: " + subscription.subscriberMethod.threadMode);
        }
    }

    private void subscribe(Object obj, SubscriberMethod subscriberMethod) {
        Class<?> cls = subscriberMethod.eventType;
        Subscription subscription = new Subscription(obj, subscriberMethod);
        CopyOnWriteArrayList<Subscription> copyOnWriteArrayList = this.subscriptionsByEventType.get(cls);
        if (copyOnWriteArrayList == null) {
            copyOnWriteArrayList = new CopyOnWriteArrayList<>();
            this.subscriptionsByEventType.put(cls, copyOnWriteArrayList);
        } else if (copyOnWriteArrayList.contains(subscription)) {
            throw new EventBusException("Subscriber " + obj.getClass() + " already registered to event " + cls);
        }
        int size = copyOnWriteArrayList.size();
        for (int i = 0; i <= size; i++) {
            if (i == size || subscriberMethod.priority > copyOnWriteArrayList.get(i).subscriberMethod.priority) {
                copyOnWriteArrayList.add(i, subscription);
                break;
            }
        }
        List<Class<?>> arrayList = this.typesBySubscriber.get(obj);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            this.typesBySubscriber.put(obj, arrayList);
        }
        arrayList.add(cls);
        if (subscriberMethod.sticky) {
            if (!this.eventInheritance) {
                checkPostStickyEventToSubscription(subscription, this.stickyEvents.get(cls));
                return;
            }
            for (Map.Entry<Class<?>, Object> entry : this.stickyEvents.entrySet()) {
                if (cls.isAssignableFrom(entry.getKey())) {
                    checkPostStickyEventToSubscription(subscription, entry.getValue());
                }
            }
        }
    }

    private void unsubscribeByEventType(Object obj, Class<?> cls) {
        CopyOnWriteArrayList<Subscription> copyOnWriteArrayList = this.subscriptionsByEventType.get(cls);
        if (copyOnWriteArrayList != null) {
            int size = copyOnWriteArrayList.size();
            int i = 0;
            while (i < size) {
                Subscription subscription = copyOnWriteArrayList.get(i);
                if (subscription.subscriber == obj) {
                    subscription.active = false;
                    copyOnWriteArrayList.remove(i);
                    i--;
                    size--;
                }
                i++;
            }
        }
    }

    public void cancelEventDelivery(Object obj) {
        PostingThreadState postingThreadState = this.currentPostingThreadState.get();
        if (!postingThreadState.isPosting) {
            throw new EventBusException("This method may only be called from inside event handling methods on the posting thread");
        }
        if (obj == null) {
            throw new EventBusException("Event may not be null");
        }
        if (postingThreadState.event != obj) {
            throw new EventBusException("Only the currently handled event may be aborted");
        }
        if (postingThreadState.subscription.subscriberMethod.threadMode != ThreadMode.POSTING) {
            throw new EventBusException(" event handlers may only abort the incoming event");
        }
        postingThreadState.canceled = true;
    }

    ExecutorService getExecutorService() {
        return this.executorService;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public <T> T getStickyEvent(Class<T> cls) {
        T tCast;
        synchronized (this.stickyEvents) {
            tCast = cls.cast(this.stickyEvents.get(cls));
        }
        return tCast;
    }

    public boolean hasSubscriberForEvent(Class<?> cls) {
        CopyOnWriteArrayList<Subscription> copyOnWriteArrayList;
        List<Class<?>> listLookupAllEventTypes = lookupAllEventTypes(cls);
        if (listLookupAllEventTypes != null) {
            int size = listLookupAllEventTypes.size();
            for (int i = 0; i < size; i++) {
                Class<?> cls2 = listLookupAllEventTypes.get(i);
                synchronized (this) {
                    copyOnWriteArrayList = this.subscriptionsByEventType.get(cls2);
                }
                if (copyOnWriteArrayList != null && !copyOnWriteArrayList.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    void invokeSubscriber(PendingPost pendingPost) {
        Object obj = pendingPost.event;
        Subscription subscription = pendingPost.subscription;
        PendingPost.releasePendingPost(pendingPost);
        if (subscription.active) {
            invokeSubscriber(subscription, obj);
        }
    }

    void invokeSubscriber(Subscription subscription, Object obj) {
        try {
            subscription.subscriberMethod.method.invoke(subscription.subscriber, obj);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Unexpected exception", e);
        } catch (InvocationTargetException e2) {
            handleSubscriberException(subscription, obj, e2.getCause());
        }
    }

    public synchronized boolean isRegistered(Object obj) {
        return this.typesBySubscriber.containsKey(obj);
    }

    public void post(Object obj) {
        PostingThreadState postingThreadState = this.currentPostingThreadState.get();
        List<Object> list = postingThreadState.eventQueue;
        list.add(obj);
        if (postingThreadState.isPosting) {
            return;
        }
        postingThreadState.isMainThread = isMainThread();
        postingThreadState.isPosting = true;
        if (postingThreadState.canceled) {
            throw new EventBusException("Internal error. Abort state was not reset");
        }
        while (!list.isEmpty()) {
            try {
                postSingleEvent(list.remove(0), postingThreadState);
            } catch (Throwable th) {
                postingThreadState.isPosting = false;
                postingThreadState.isMainThread = false;
                throw th;
            }
        }
        postingThreadState.isPosting = false;
        postingThreadState.isMainThread = false;
    }

    public void postSticky(Object obj) {
        synchronized (this.stickyEvents) {
            this.stickyEvents.put(obj.getClass(), obj);
        }
        post(obj);
    }

    public void register(Object obj) {
        List<SubscriberMethod> listFindSubscriberMethods = this.subscriberMethodFinder.findSubscriberMethods(obj.getClass());
        synchronized (this) {
            Iterator<SubscriberMethod> it = listFindSubscriberMethods.iterator();
            while (it.hasNext()) {
                subscribe(obj, it.next());
            }
        }
    }

    public void removeAllStickyEvents() {
        synchronized (this.stickyEvents) {
            this.stickyEvents.clear();
        }
    }

    public <T> T removeStickyEvent(Class<T> cls) {
        T tCast;
        synchronized (this.stickyEvents) {
            tCast = cls.cast(this.stickyEvents.remove(cls));
        }
        return tCast;
    }

    public boolean removeStickyEvent(Object obj) {
        synchronized (this.stickyEvents) {
            Class<?> cls = obj.getClass();
            if (!obj.equals(this.stickyEvents.get(cls))) {
                return false;
            }
            this.stickyEvents.remove(cls);
            return true;
        }
    }

    public String toString() {
        return "EventBus[indexCount=" + this.indexCount + ", eventInheritance=" + this.eventInheritance + "]";
    }

    public synchronized void unregister(Object obj) {
        List<Class<?>> list = this.typesBySubscriber.get(obj);
        if (list != null) {
            Iterator<Class<?>> it = list.iterator();
            while (it.hasNext()) {
                unsubscribeByEventType(obj, it.next());
            }
            this.typesBySubscriber.remove(obj);
        } else {
            this.logger.log(Level.WARNING, "Subscriber to unregister was not registered before: " + obj.getClass());
        }
    }
}

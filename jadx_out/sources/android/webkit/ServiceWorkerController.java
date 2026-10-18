package android.webkit;

/* JADX INFO: loaded from: classes2.dex */
public abstract class ServiceWorkerController {
    public static ServiceWorkerController getInstance() {
        return WebViewFactory.getProvider().getServiceWorkerController();
    }

    public abstract ServiceWorkerWebSettings getServiceWorkerWebSettings();

    public abstract void setServiceWorkerClient(ServiceWorkerClient serviceWorkerClient);
}

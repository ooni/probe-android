package org.openobservatory.ooniprobe.domain.callback;

public interface DomainCallback<T> {
    public abstract void onSuccess(T result);
    public abstract void onError(String msg);
}

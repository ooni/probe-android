package org.openobservatory.ooniprobe.domain.callback;

public interface DomainCallback<T> {
    void onSuccess(T result);
    void onError(String msg);
}

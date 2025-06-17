package ru.dsofarts.mutlithread.rx;

public interface Observer<T> {

    void onNext(T item);

    void onError(Throwable t);

    void onComplete();
}
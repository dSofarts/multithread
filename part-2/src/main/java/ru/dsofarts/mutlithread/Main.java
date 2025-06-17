package ru.dsofarts.mutlithread;

import lombok.extern.slf4j.Slf4j;
import ru.dsofarts.mutlithread.rx.Disposable;
import ru.dsofarts.mutlithread.rx.Observable;
import ru.dsofarts.mutlithread.rx.scheduler.ComputationScheduler;
import ru.dsofarts.mutlithread.rx.scheduler.IOThreadScheduler;
import ru.dsofarts.mutlithread.rx.scheduler.SingleThreadScheduler;

@Slf4j
public class Main {
    public static void main(String[] args) {
        
        IOThreadScheduler ioScheduler = new IOThreadScheduler();
        ComputationScheduler computationScheduler = new ComputationScheduler();
        SingleThreadScheduler singleThreadScheduler = new SingleThreadScheduler();

        log.info("Example 1: Using flatMap");
        Observable<Integer> numbers = Observable.create(observer -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    observer.onNext(i);
                }
                observer.onComplete();
            } catch (Exception e) {
                observer.onError(e);
            }
        });

        Disposable disposable = numbers
                .flatMap(number -> Observable.create(observer -> {
                    try {
                        // Simulate some work
                        Thread.sleep(100);
                        observer.onNext(number * 10);
                        observer.onNext(number * 20);
                        observer.onComplete();
                    } catch (Exception e) {
                        observer.onError(e);
                    }
                }))
                .subscribe(
                        item -> log.info("FlatMap result: {}", item),
                        error -> log.error("Error: {}", error.getMessage()),
                        () -> log.info("FlatMap completed!")
                );


        log.info("Example 2: Error handling");
        Observable.create(observer -> {
                    try {
                        observer.onNext(1);
                        observer.onNext(2);
                        throw new RuntimeException("Simulated error");
                    } catch (Exception e) {
                        observer.onError(e);
                    }
                })
                .subscribe(
                        item -> log.info("Received: {}", item),
                        error -> log.info("Error handled: {}", error.getMessage()),
                        () -> log.info("This won't be called due to error")
                );


        log.info("Example 3: Disposable usage");
        Observable<Integer> infinite = Observable.create(observer -> {
            int i = 0;
            while (true) {
                observer.onNext(i++);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Disposable infiniteDisposable = infinite
                .subscribeOn(ioScheduler)
                .observeOn(computationScheduler)
                .subscribe(
                        item -> log.info("Infinite: {}", item),
                        error -> log.error("Error: {}", error.getMessage()),
                        () -> log.info("This won't be called")
                );
        
        try {
            Thread.sleep(500);
            infiniteDisposable.dispose();
            log.info("Infinite stream disposed");
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
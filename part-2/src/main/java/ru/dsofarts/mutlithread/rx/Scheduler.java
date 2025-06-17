package ru.dsofarts.mutlithread.rx;

public interface Scheduler {
    void execute(Runnable task);
}

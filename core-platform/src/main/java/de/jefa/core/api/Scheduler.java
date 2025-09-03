package de.jefa.core.api;

public interface Scheduler {

    Runnable runLater(Runnable task, long delayTicks);
    Runnable runRepeating(Runnable task, long delayTicks, long periodTicks);
    void cancel(Runnable handle);
}

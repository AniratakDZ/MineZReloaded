package de.jefa.core.impl;

import de.jefa.core.api.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PaperScheduler implements Scheduler {

    private final Plugin plugin;

    public PaperScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Runnable runLater(Runnable task, long delayTicks) {
        final var bukkitTask = Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        return bukkitTask::cancel;
    }

    @Override
    public Runnable runRepeating(Runnable task, long delayTicks, long periodTicks) {
        final var bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
        return bukkitTask::cancel;
    }

    @Override
    public void cancel(Runnable handle) {
        if(handle != null) {
            handle.run();
        }
    }
}

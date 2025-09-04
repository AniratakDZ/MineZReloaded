package de.jefa.core.impl;

import de.jefa.core.api.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

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

    public Runnable runRepeatingTpsSafe(Runnable task, long targetMs) {
        final long[] nextDue = {
                System.nanoTime() + targetMs / 1_000_000L
        };
        final BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            final long now = System.nanoTime();
            if (now >= nextDue[0]) {
                try {
                    task.run();
                } finally {
                    nextDue[0] = now + targetMs * 1_000_000L;
                }
            }
        }, 1L, 1L);
        return bukkitTask::cancel;
    }

    @Override
    public void cancel(Runnable handle) {
        if(handle != null) {
            handle.run();
        }
    }
}

package de.jefa.core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ConfigReloadedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Set<String> changedFiles;

    public ConfigReloadedEvent(Set<String> changedFiles) {
        super(false);
        this.changedFiles = changedFiles;
    }

    public Set<String> getChangedFiles() {
        return changedFiles;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}

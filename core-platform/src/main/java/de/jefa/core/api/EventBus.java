package de.jefa.core.api;

import org.bukkit.event.Event;

public interface EventBus {

    void post(Event event);
}

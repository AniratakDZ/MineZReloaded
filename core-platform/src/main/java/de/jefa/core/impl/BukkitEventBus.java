package de.jefa.core.impl;

import de.jefa.core.api.EventBus;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public class BukkitEventBus implements EventBus {

    @Override
    public void post(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }
}

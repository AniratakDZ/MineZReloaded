package de.jefa.core.util;

import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static javax.swing.UIManager.put;

public class UuidNameLookup {

    private static final long TTL_MS = 5 * 60 * 1000;
    private final Map<UUID, Entry> uuidToName = new ConcurrentHashMap<>();
    private final Map<String, Entry> nameToUuid = new ConcurrentHashMap<>();

    public String nameOf(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        final Entry e = uuidToName.get(uuid);
        if (e != null && !e.expired()) return e.name;

        final Player p = Bukkit.getPlayer(uuid);
        if (p != null) {
            return put(p.getUniqueId(), p.getName()).name;
        }

        final OfflinePlayer off = Bukkit.getOfflinePlayer(uuid);
        if (off != null && off.getName() != null) {
            return put(off.getUniqueId(), off.getName()).name;
        }

        return null;
    }

    public UUID uuidOf(String name) {
        if (name == null) {
            return null;
        }
        final Entry e = nameToUuid.get(name.toLowerCase());
        if (e != null && !e.expired()) {
            return e.uuid;
        }

        final Player p = Bukkit.getPlayerExact(name);
        if (p != null) {
            return put(p.getUniqueId(), p.getName()).uuid;
        }

        final OfflinePlayer off = Bukkit.getOfflinePlayer(name);
        if (off != null && off.getUniqueId() != null){
            return put(off.getUniqueId(), off.getName()).uuid;
        }

        return null;
    }

    private Entry put(UUID uuid, String name) {
        if(uuid == null || name == null) {
            return null;
        }
        final Entry entry = new Entry(uuid, name, System.currentTimeMillis() + TTL_MS);
        uuidToName.put(uuid, entry);
        nameToUuid.put(name.toLowerCase(), entry);
        return entry;
    }

    private static final class Entry {

        final UUID uuid;
        final String name;
        final long until;

        public Entry(final UUID uuid, final String name, final long until) {
            this.uuid = uuid;
            this.name = name;
            this.until = until;
        }

        public boolean expired() {
            return System.currentTimeMillis() > until;
        }
    }
}
package de.jefa.core.impl;

import de.jefa.core.api.ConfigService;
import de.jefa.core.api.EventBus;
import de.jefa.core.events.ConfigReloadedEvent;
import de.jefa.core.model.objects.ConfigFile;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Boolean.parseBoolean;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.util.Objects.requireNonNull;

public class BukkitConfigService implements ConfigService {

    private final Plugin plugin;
    private final EventBus eventBus;

    private volatile Map<String, Object> cache = Map.of();

    private WatchService watchService;
    private final AtomicBoolean watcherRunning = new AtomicBoolean(false);
    private Runnable watcherCancelHandle;
    private final long debounceMs = 1000L;
    private BukkitTask watcherTask;

    private final List<ConfigFile> files = List.of(
            new ConfigFile("global.yml", "global"),
            new ConfigFile("survival.yml", "survival"),
            new ConfigFile("zombies.yml", "zombies"),
            new ConfigFile("weapons.yml", "weapons"),
            new ConfigFile("loot.yml", "loot")
    );

    public BukkitConfigService(Plugin plugin, EventBus eventBus) {
        this.plugin = plugin;
        this.eventBus = eventBus;
        loadAll();
        startWatcher();
    }

    @Override
    public String getString(String path, String def) {
        final Object o = cache.get(path);
        return o!=null ? String.valueOf(o) : def;
    }

    @Override
    public int getInt(String path, int def) {
        final Object o = cache.get(path);
        return number(o)!=null ? requireNonNull(number(o)).intValue() : def;
    }

    @Override
    public double getDouble(String path, double def) {
        final Object o = cache.get(path);
        return number(o)!=null ? requireNonNull(number(o)).doubleValue() : def;
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        final Object o = cache.get(path);
        return (o instanceof Boolean) ? (Boolean)o : parseBoolean(String.valueOf(o));
    }

    @Override
    public List<String> getStringList(String path) {
        final Object o = cache.get(path);
        if(o instanceof List<?> list) {
            final List<String> out = new ArrayList<>(list.size());
            for(Object it : list) {
                out.add(String.valueOf(it));
            }
            return out;
        }
        return Collections.emptyList();
    }

    @Override
    public boolean contains(String path) {
        return cache.containsKey(path);
    }

    @Override
    public Set<String> getKeys(String prefix, boolean deep) {
        if(prefix == null || prefix.isEmpty()) {
            return Set.copyOf(cache.keySet());
        }
        final String p = prefix.endsWith(".")? prefix : prefix + ".";
        final Set<String> out = new HashSet<>();
        for(String key : cache.keySet()) {
            if(key.startsWith(p)) {
                if(deep) {
                    out.add(key);
                } else {
                    final int idx = key.indexOf('.', p.length());
                    out.add(idx == -1 ? key : key.substring(0, idx));
                }
            }
        }
        return out;
    }

    @Override
    public void reload() {
        loadAll();
        eventBus.post(new ConfigReloadedEvent(Set.of()));
        plugin.getLogger().info("[Config] Reloaded.");
    }

    @Override
    public void close() {
        stopWatcher();
    }

    private void loadAll() {
        final Map<String, Object> snap = new LinkedHashMap<>();
        for(ConfigFile cf : files) {
            final File f = new File(plugin.getDataFolder(), cf.getFileName());
            if(!f.exists()) {
                continue;
            }
            final YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
            flattenSection(snap, yml, "", cf.getPrefix());
        }
        this.cache = Collections.unmodifiableMap(snap);
    }

    private void flattenSection(Map<String, Object> out, ConfigurationSection sec, String path, String prefix) {
        for(String key : sec.getKeys(false)) {
            final String childPath = path.isEmpty() ? key : path + "." + key;
            final Object val = sec.get(key);
            if(val instanceof ConfigurationSection cs) {
                flattenSection(out, cs, childPath, prefix);
            } else {
                final String fullKey = prefix + "." + childPath;
                out.put(fullKey, val);
            }
        }
    }

    private Number number(Object o) {
        if(o instanceof Number n) {
            return n;
        }
        try {
            return o!=null ? Double.parseDouble(String.valueOf(o)) : null;
        } catch(Exception ignored) {
            return null;
        }
    }

    private void startWatcher() {
        try {
            Path dir = plugin.getDataFolder().toPath();
            if (!Files.exists(dir)) return;
            this.watchService = FileSystems.getDefault().newWatchService();
            dir.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);

            watcherRunning.set(true);

            // asynchroner Watcher-Loop
            this.watcherTask = plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                Map<String,Long> touched = new ConcurrentHashMap<>();
                while (watcherRunning.get()) {
                    try {
                        WatchKey key = watchService.take();
                        for (WatchEvent<?> evt : key.pollEvents()) {
                            Path changed = (Path) evt.context();
                            if (changed != null && changed.toString().endsWith(".yml")) {
                                touched.put(changed.toString(), System.currentTimeMillis());
                            }
                        }
                        key.reset();

                        if (!touched.isEmpty()) {
                            Thread.sleep(debounceMs);
                            Set<String> changedSet = Set.copyOf(touched.keySet());
                            touched.clear();
                            plugin.getServer().getScheduler().runTask(plugin, () -> {
                                loadAll();
                                eventBus.post(new ConfigReloadedEvent(changedSet));
                                plugin.getLogger().info("[Config] Hot-reloaded: " + changedSet);
                            });
                        }
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception ex) {
                        plugin.getLogger().warning("[Config Watcher] " + ex.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            plugin.getLogger().warning("[Config] Watcher not started: " + e.getMessage());
        }
    }

    public void stopWatcher() {
        watcherRunning.set(false);
        try {
            if(watchService != null) {
                watchService.close();
            }
        } catch(Exception ignored) {

        }
    }
}
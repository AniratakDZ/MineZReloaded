package de.jefa.core.impl;

import de.jefa.core.api.ConfigService;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class BukkitConfigService implements ConfigService {

    private final Plugin plugin;
    private FileConfiguration globalCfg;

    public BukkitConfigService(Plugin plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        final File global = new File(plugin.getDataFolder(), "global.yml");
        this.globalCfg = YamlConfiguration.loadConfiguration(global);
    }

    @Override
    public String getString(String path, String def) {
        return globalCfg.getString(path, def);
    }

    @Override
    public int getInt(String path, int def) {
        return globalCfg.getInt(path, def);
    }

    @Override
    public double getDouble(String path, double def) {
        return globalCfg.getDouble(path, def);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return globalCfg.getBoolean(path, def);
    }

    @Override
    public List<String> getStringList(String path) {
        final List<String> list = globalCfg.getStringList(path);
        return list.isEmpty() ? Collections.emptyList() : list;
    }

    @Override
    public void reload() {
        load();
    }
}

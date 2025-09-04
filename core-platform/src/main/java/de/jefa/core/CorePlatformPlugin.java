package de.jefa.core;

import de.jefa.core.api.ConfigService;
import de.jefa.core.api.EventBus;
import de.jefa.core.api.Scheduler;
import de.jefa.core.commands.ReloadConfigCommand;
import de.jefa.core.impl.BukkitConfigService;
import de.jefa.core.impl.BukkitEventBus;
import de.jefa.core.impl.PaperScheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class CorePlatformPlugin extends JavaPlugin {

    private ConfigService configService;
    private EventBus eventBus;
    private Scheduler scheduler;

    @Override
    public void onLoad() {
        if(!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        saveIfMissing("global.yml");
        saveIfMissing("survival.yml");
        saveIfMissing("zombies.yml");
        saveIfMissing("weapons.yml");
        saveIfMissing("loot.yml");
        getLogger().info("Core onLoad: defaults ensured!");
    }

    @Override
    public void onEnable() {
        this.eventBus = new BukkitEventBus();
        this.scheduler = new PaperScheduler(this);
        this.configService = new BukkitConfigService(this, eventBus);

        final var serviceManager = Bukkit.getServicesManager();
        serviceManager.register(ConfigService.class, configService, this, ServicePriority.Normal);
        serviceManager.register(EventBus.class, eventBus, this, ServicePriority.Normal);
        serviceManager.register(Scheduler.class, scheduler, this, ServicePriority.Normal);

        requireNonNull(getCommand("mzreload")).setExecutor(new ReloadConfigCommand(this));

        getLogger().info("MineZReloaded-Core ready. Data dir: " + getDataFolder().getAbsolutePath());
    }

    @Override
    public void onDisable() {
        if(configService != null) {
            try {
                configService.close();
            } catch(Exception ignored) {

            }
        }
    }

    private void saveIfMissing(String name) {
        final File file = new File(getDataFolder(), name);
        if(!file.exists()) {
            saveResource(name, false);
        }
    }
}
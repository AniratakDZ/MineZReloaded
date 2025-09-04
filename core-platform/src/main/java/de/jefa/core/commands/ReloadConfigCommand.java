package de.jefa.core.commands;

import de.jefa.core.api.ConfigService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ReloadConfigCommand implements CommandExecutor {

    private final Plugin plugin;

    public ReloadConfigCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        final ConfigService configService = Bukkit.getServicesManager().load(ConfigService.class);
        if(configService == null) {
            commandSender.sendMessage("$c[MineZ] Kein ConfigService verfügbar!");
            return true;
        }

        configService.reload();
        commandSender.sendMessage("$a[MineZ] Configs wurden neu geladen!");
        plugin.getLogger().info(commandSender.getName() + " hat die Configs neu geladen!");
        return true;
    }
}
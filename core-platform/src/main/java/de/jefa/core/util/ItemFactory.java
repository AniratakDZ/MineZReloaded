package de.jefa.core.util;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES;
import static org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE;
import static org.bukkit.persistence.PersistentDataType.STRING;

public class ItemFactory {

    private ItemFactory() {

    }

    public static ItemStack withName(ItemStack base, String displayName) {
        final ItemStack copy = base.clone();
        final ItemMeta meta = copy.getItemMeta();
        meta.displayName(text(displayName));
        copy.setItemMeta(meta);
        return copy;
    }

    public static ItemStack withLore(ItemStack base, List<Component> lore) {
        final ItemStack copy = base.clone();
        final ItemMeta meta = copy.getItemMeta();
        meta.lore(new ArrayList<>(lore));
        copy.setItemMeta(meta);
        return copy;
    }

    public static ItemStack withModel(ItemStack base, int customModelData) {
        final ItemStack copy = base.clone();
        final ItemMeta meta = copy.getItemMeta();
        final CustomModelDataComponent cmd = meta.getCustomModelDataComponent();
        cmd.setFloats(List.of((float) customModelData));
        meta.setCustomModelDataComponent(cmd);
        copy.setItemMeta(meta);
        return copy;
    }

    public static ItemStack hideAttributes(ItemStack base) {
        final ItemStack copy = base.clone();
        final ItemMeta meta = copy.getItemMeta();
        meta.addItemFlags(HIDE_ATTRIBUTES, HIDE_UNBREAKABLE);
        copy.setItemMeta(meta);
        return copy;
    }

    public static ItemStack setStringTag(ItemStack base, NamespacedKey key, String value) {
        final ItemStack copy = base.clone();
        final ItemMeta meta = copy.getItemMeta();
        final PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(key, STRING, value);
        copy.setItemMeta(meta);
        return copy;
    }

    public static String getStringTage(ItemStack stack, NamespacedKey key) {
        final ItemMeta meta = stack.getItemMeta();
        if(meta == null) {
            return null;
        }
        final PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.get(key, STRING);
    }
}
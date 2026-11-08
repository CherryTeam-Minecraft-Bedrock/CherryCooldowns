/**
 *
 *   ____ _                           _____
 *  / ___| |__   ___ _ __ _ __ _   _|_   _|__  __ _ _ __ ___
 * | |   | '_ \ / _ \ '__| '__| | | | | |/ _ \/ _` | '_ ` _ \
 * | |___| | | |  __/ |  | |  | |_| | | |  __/ (_| | | | | | |
 *  \____|_| |_|\___|_|  |_|   \__, |_|  \___|\__,_|_| |_| |_|
 *                             |___/
 *
 * Main.java
 * @authors CherryTeam
 * 
 */

package com.cherry.cooldowns;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.event.Listener;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemEdible;
import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.cherry.cooldowns.api.*;

public class Main extends PluginBase implements Listener {
    private CooldownsManager cooldownsManager = CooldownsManager.getInstance();
    private Map<UUID, Map<String, Long>> cooldowns = new ConcurrentHashMap<>(); 

    @Override 
    public void onEnable() {
        try {
            saveDefaultConfig();
            reloadConfig();
            cooldownsManager.loadDataFolder(this.getDataFolder().getAbsolutePath());
            if (getConfig().getSection("cooldowns") != null) {
                for (Map.Entry<String, Object> entry : getConfig().getSection("cooldowns").entrySet()) {
                    String id = entry.getKey();
                    Object time = entry.getValue();
                    if (!(time instanceof Integer)) continue;
                    if ((int) time <= 0) continue;
                    cooldownsManager.register(new Cooldown(id, (int) time));
                } 
            }
            getServer().getPluginManager().registerEvents(this, this);
            this.getLogger().info(TextFormat.RED + "  ____ _                           _____");
            this.getLogger().info(TextFormat.RED + " / ___| |__   ___ _ __ _ __ _   _|_   _|__  __ _ _ __ ___");
            this.getLogger().info(TextFormat.RED + "| |   | '_ \\ / _ \\ '__| '__| | | | | |/ _ \\/ _` | '_ ` _ \\");
            this.getLogger().info(TextFormat.RED + "| |___| | | |  __/ |  | |  | |_| | | |  __/ (_| | | | | | |");
            this.getLogger().info(TextFormat.RED + " \\____|_| |_|\\___|_|  |_|   \\__, |_|  \\___|\\__,_|_| |_| |_|");
            this.getLogger().info(TextFormat.RED + "                            |___/");
            this.getLogger().info(TextFormat.YELLOW + "MIT License");
            this.getLogger().info("GitHub repository: " + TextFormat.RED + "https://github.com/CherryTeam-Minecraft-Bedrock/CherryCooldowns");
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Item item = event.getItem();
        long cooldownTime = cooldowns.getOrDefault(uuid, Map.of()).getOrDefault(item.getNamespaceId(), (long) 0);
        if ((cooldownTime - System.currentTimeMillis()) > 0) {
            String message = TextFormat.colorize('&', getConfig().getString("message_format", "&cHey! You can't use this item, wait: %cooldown%s")).replaceAll("%cooldown%", String.valueOf((cooldownTime - System.currentTimeMillis()) / 1000));
            player.sendMessage(message);
            event.setCancelled(true);
            return;
        }
        int time = cooldownsManager.getTime(item.getNamespaceId());
        if (time <= 0) return;
        cooldowns.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>()).put(item.getNamespaceId(), (System.currentTimeMillis() + time * 1000));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onItemUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Item item = event.getItem();
        if ((item instanceof ItemEdible)) return;
        if (event.getAction() != PlayerInteractEvent.Action.RIGHT_CLICK_AIR && event.getAction() != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return;
        long cooldownTime = cooldowns.getOrDefault(uuid, Map.of()).getOrDefault(item.getNamespaceId(), (long) 0);
        if ((cooldownTime - System.currentTimeMillis()) > 0) {
            String message = TextFormat.colorize('&', getConfig().getString("message_format", "&cHey! You can't use this item, wait: %cooldown%s")).replaceAll("%cooldown%", String.valueOf((cooldownTime - System.currentTimeMillis()) / 1000));
            player.sendMessage(message);
            event.setCancelled(true);
            return;
        }
        int time = cooldownsManager.getTime(item.getNamespaceId());
        if (time <= 0) return;
        cooldowns.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>()).put(item.getNamespaceId(), (System.currentTimeMillis() + time * 1000));
    }
}

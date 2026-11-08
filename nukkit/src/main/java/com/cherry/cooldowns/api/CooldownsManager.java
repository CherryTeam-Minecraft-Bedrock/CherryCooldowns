/**
 *
 *   ____ _                           _____
 *  / ___| |__   ___ _ __ _ __ _   _|_   _|__  __ _ _ __ ___
 * | |   | '_ \ / _ \ '__| '__| | | | | |/ _ \/ _` | '_ ` _ \
 * | |___| | | |  __/ |  | |  | |_| | | |  __/ (_| | | | | | |
 *  \____|_| |_|\___|_|  |_|   \__, |_|  \___|\__,_|_| |_| |_|
 *                             |___/
 *
 * CooldownsManager.java
 * @authors CherryTeam
 * 
 */

package com.cherry.cooldowns.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class CooldownsManager {
    private static final CooldownsManager INSTANCE = new CooldownsManager();
    private final Map<String, Integer> cooldowns = new ConcurrentHashMap<>();
    private String folder = null;

    private CooldownsManager() {}

    public static CooldownsManager getInstance() {
        return INSTANCE; 
    }

    public synchronized void loadDataFolder(String loadedFolder) {
        if (loadedFolder == null || this.folder != null) return;
        this.folder = loadedFolder;
    }

    private Path getPath() {
        return this.folder != null ? Path.of(this.folder, "cooldowns.properties") : null;
    }

    private String normalizeId(String id) {
        return id == null ? "" : id.replaceAll(" ", "_");
    }

    public synchronized void register(Cooldown cooldown) {
        String id = normalizeId(cooldown.getId());
        if (cooldowns.containsKey(id)) return;
        int timeInSeconds = cooldown.getTime();
        cooldowns.put(id, timeInSeconds);
        Path path = getPath();
        if (path == null) return;
        try {
            if (path.getParent() != null) Files.createDirectories(path.getParent());
            Properties props = new Properties();
            if (Files.exists(path)) {
                try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                    props.load(reader);
                }
            }
            props.setProperty(id, String.valueOf(timeInSeconds));
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                props.store(writer, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void unregister(String rawId) {
        String id = normalizeId(rawId);
        if (!cooldowns.containsKey(id)) return;
        cooldowns.remove(id);
        Path path = getPath();
        if (path == null || !Files.exists(path)) return;
        try {
            Properties props = new Properties();
            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                props.load(reader);
            }
            props.remove(id);
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                props.store(writer, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getTime(String rawId) {
        String id = normalizeId(rawId);
        if (cooldowns.isEmpty()) {
            synchronized (this) {
                if (cooldowns.isEmpty()) loadCooldowns();
            }
        }
        return cooldowns.getOrDefault(id, 0);
    }

    public synchronized void setTime(String rawId, int time) {
        String id = normalizeId(rawId);
        if (cooldowns.isEmpty()) loadCooldowns();
        if (!cooldowns.containsKey(id)) return;
        int targetTime = Math.max(time, 0);
        cooldowns.put(id, targetTime);
        Path path = getPath();
        if (path == null) return;
        try {
            Properties props = new Properties();
            if (Files.exists(path)) {
                try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                    props.load(reader);
                }
            }
            props.setProperty(id, String.valueOf(targetTime));
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                props.store(writer, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCooldowns() {
        Path path = getPath();
        if (path == null || !Files.exists(path)) return;
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            Properties props = new Properties();
            props.load(reader);
            for (String key : props.stringPropertyNames()) {
                cooldowns.put(key, Integer.parseInt(props.getProperty(key)));
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
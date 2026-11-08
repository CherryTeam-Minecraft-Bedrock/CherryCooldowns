
/**
 *
 *   ____ _                           _____
 *  / ___| |__   ___ _ __ _ __ _   _|_   _|__  __ _ _ __ ___
 * | |   | '_ \ / _ \ '__| '__| | | | | |/ _ \/ _` | '_ ` _ \
 * | |___| | | |  __/ |  | |  | |_| | | |  __/ (_| | | | | | |
 *  \____|_| |_|\___|_|  |_|   \__, |_|  \___|\__,_|_| |_| |_|
 *                             |___/
 *
 * Cooldown.java
 * @authors CherryTeam
 * 
 */

package com.cherry.cooldowns.api;

import cn.nukkit.item.Item;

public class Cooldown {
    private String id = null;
    private int time = 0;

    public Cooldown(String id) {
        this.id = id != null && !id.isEmpty() ? id.replaceAll(" ", "_") : "";
        this.time = 15;
    }

    public Cooldown(String id, int time) {
        this.id = id != null && !id.isEmpty() ? id.replaceAll(" ", "_") : "";
        this.time = time > 0 ? time : 15;
    }

    public Cooldown(Item item) {
        this.id = item.getNamespaceId() != null && !item.getNamespaceId().isEmpty() ? item.getNamespaceId().replaceAll(" ", "_") : "";
        this.time = 15;
    }

    public Cooldown(Item item, int time) {
        this.id = item.getNamespaceId() != null && !item.getNamespaceId().isEmpty() ? item.getNamespaceId().replaceAll(" ", "_") : "";
        this.time = time > 0 ? time : 15;
    }

    public String getId() {
        return this.id;
    }

    public int getTime() {
        return this.time;
    }
}

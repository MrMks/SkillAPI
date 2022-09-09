package com.sucy.skill.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PluginReloadFinishEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}

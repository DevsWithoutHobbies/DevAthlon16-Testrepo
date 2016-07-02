package de.DevsWithoutHobbies.Test;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

class EventListener implements Listener {

    @EventHandler
    void playerMove(PlayerMoveEvent event) {
        double distance = event.getFrom().getY() - event.getTo().getY();
        Bukkit.broadcastMessage(event.getPlayer().getDisplayName() + " moved " + distance);
    }
}

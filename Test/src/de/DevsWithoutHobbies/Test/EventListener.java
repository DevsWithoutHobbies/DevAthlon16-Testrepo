package de.DevsWithoutHobbies.Test;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

class EventListener implements Listener {
    @EventHandler
    void playerMove(PlayerMoveEvent event) {
        double distance = event.getTo().getY() - event.getFrom().getY();

        if (distance < -0.2) {
            Block block = event.getPlayer().getWorld().getBlockAt((int) event.getTo().getX(), (int) event.getTo().getY() - 1, (int) event.getTo().getZ());
            block.setType(Material.WOOL);
            block.setData((byte) (block.getY() % 16));
        }
    }
}

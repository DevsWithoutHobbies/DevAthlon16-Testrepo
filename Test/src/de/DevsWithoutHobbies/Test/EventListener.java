package de.DevsWithoutHobbies.Test;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

class EventListener implements Listener {
    @EventHandler
    void playerMove(PlayerMoveEvent event) {
        byte block_data = 0;
        if (event.getPlayer().isSneaking()) {
            block_data = 6;
        } else if (event.getPlayer().isSprinting()) {
            block_data = 14;
        } else if (event.getPlayer().isFlying()) {
            block_data = 4;
        }

        int current_y = (int) event.getTo().getY() - 1;
        while (current_y > 0) {
            Block block = event.getPlayer().getWorld().getBlockAt(
                    (int) event.getTo().getX(),
                    current_y,
                    (int) event.getTo().getZ()
            );
            if (block.getType().isSolid()) {
                block.setType(Material.WOOL);
                block.setData(block_data);
                break;
            }
            current_y = current_y - 1;
        }
    }
}

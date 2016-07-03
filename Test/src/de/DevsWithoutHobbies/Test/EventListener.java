package de.DevsWithoutHobbies.Test;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Set;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

class EventListener implements Listener {

    private final Main plugin;

    EventListener(Main instance) {
        this.plugin = instance;
    }


    @EventHandler
    void playerLogin(PlayerLoginEvent event) {
        String name = event.getPlayer().getDisplayName();
        System.out.println(name);
        List<TableRow> table = plugin.database.getData("Users", new String[]{"UserID"}, "Username LIKE \"" + name + "\"");
        if (table.size() == 0) {
            TableRow row = new TableRow();
            row.put("Username", name);
            row.put("MaxSpeed", "0");
            plugin.database.addRow("Users", row);
        }
    }

    @EventHandler
    void playerMove(PlayerMoveEvent event) {

        Double speed = sqrt(
                pow(event.getTo().getX() - event.getFrom().getX(), 2) +
                pow(event.getTo().getZ() - event.getFrom().getZ(), 2)
        );

        plugin.updatePlayerSpeed(event.getPlayer(), speed);

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
            final Block block = event.getPlayer().getWorld().getBlockAt(
                    (int) event.getTo().getX() - 1,
                    current_y,
                    (int) event.getTo().getZ()
            );
            if (block.getType().isSolid()) {
                if (block.getType() != Material.WOOL) {
                    final Material old_block_material = block.getType();
                    final byte old_block_data = block.getData();
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            if (block.getType() == Material.WOOL) {
                                block.setType(old_block_material);
                                block.setData(old_block_data);
                            }
                        }
                    }.runTaskLater(plugin, 100);
                    block.setType(Material.WOOL);
                    block.setData(block_data);
                }
                break;
            }
            current_y = current_y - 1;
        }
    }



    @EventHandler
    public void onPlayerInteractBlock(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (player.getItemInHand().getType() == Material.FISHING_ROD) {
            Location target_block_loc = player.getTargetBlock((Set<Material>) null, 200).getLocation();
            player.getWorld().strikeLightning(target_block_loc);
            player.getWorld().createExplosion(target_block_loc, 8F);
        } else if (player.getItemInHand().getType() == Material.DIAMOND_AXE) {
            final Location target_block_loc = player.getTargetBlock((Set<Material>) null, 200).getLocation();
            for (int i = 0; i< 100; i++) {
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        player.getWorld().strikeLightning(target_block_loc);
                    }
                }.runTaskLater(plugin, 0);

            }
        } else if (player.getItemInHand().getType() == Material.BLAZE_ROD) {
            Location loc = player.getTargetBlock((Set<Material>) null, 200).getLocation();
            loc.setY(loc.getY() + 1);
            for (int i = 0; i< 100; i++) {
                player.getWorld().spawnEntity(loc, EntityType.BAT);
            }
            //player.getWorld().strikeLightning(player.getTargetBlock((Set<Material>) null, 200).getLocation());
        } else if (player.getItemInHand().getType() == Material.CARROT_STICK) {
            player.sendMessage(player.getTargetBlock((Set<Material>) null, 200).getType().toString());
        }
    }
}

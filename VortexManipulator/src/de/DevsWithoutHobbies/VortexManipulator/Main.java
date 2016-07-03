package de.DevsWithoutHobbies.VortexManipulator;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

/**
 * Created by noah on 03/07/16.
 */
public class Main extends JavaPlugin {

    Database database = null;
    final static int WORLD_JUMP_DIST = 1000;

    @Override
    public void onEnable() {
        createConfig();
        connectToMySQL();
    }

    private void connectToMySQL() {
        String server = this.getConfig().getString("mysql-server");
        String port = this.getConfig().getString("mysql-port");
        String user = this.getConfig().getString("mysql-username");
        String password = this.getConfig().getString("mysql-password");
        String database_name = this.getConfig().getString("mysql-database-name");

        database = new Database();
        database.connect(server, port, database_name, user, password);
    }

    private void createConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                getLogger().info("Config.yml not found, creating!");
                saveDefaultConfig();
            } else {
                getLogger().info("Config.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private double dist(Location loc1, Location loc2) {
        double dx = loc1.getX() - loc2.getX();
        double dy = loc1.getY() - loc2.getY();
        double dz = loc1.getZ() - loc2.getZ();
        double dw = 0;
        if (!loc1.getWorld().equals(loc1.getWorld())) {
            dw = WORLD_JUMP_DIST;
        }
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2) + Math.pow(dw, 2));
    }

    private int relocate(Player player, Location target_location) {
        int cost = 1 + (int) Math.round(dist(player.getLocation(), target_location) / 100);
        int xp = player.getTotalExperience();
        int max_players = xp / cost;
        List<Player> to_teleport = new ArrayList<Player>();
        if (max_players > 0) {
            to_teleport.add(player);
        } else {
           player.sendMessage("Not enough XP: You need at least " + cost + " but you only have " + xp);
        }
        for (Player next_player: player.getWorld().getPlayers()) {
            if (to_teleport.size() >= max_players) {
                break;
            } else if (!to_teleport.contains(next_player) && dist(player.getLocation(), next_player.getLocation()) < 2) {
                to_teleport.add(next_player);
            }
        }
        for (Player next_player: to_teleport) {
            next_player.teleport(target_location);
            player.setTotalExperience(player.getTotalExperience() - cost);
            next_player.sendMessage("VortexManipulator: " + player.getDisplayName() + " teleported " + to_teleport.size() + " players");
        }
        return to_teleport.size();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
        } else {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("vm_info") && args.length == 0) {
                Location loc = player.getLocation();
                player.sendMessage(loc.getWorld().getName() + " @ (" + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ")");
            } else if (cmd.getName().equalsIgnoreCase("vm_home") && args.length == 0) {
                Location home_loc = player.getBedSpawnLocation();
                if (home_loc != null) {
                    relocate(player, player.getBedSpawnLocation());
                } else {
                    player.sendMessage("You don't have a home!");
                }
            } else if (cmd.getName().equalsIgnoreCase("vm_location") && args.length == 3) {
                relocate(player, new Location(player.getWorld(), Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2])));
            } else {
                return false;
            }
            return true;
        }
        return false;
    }
}

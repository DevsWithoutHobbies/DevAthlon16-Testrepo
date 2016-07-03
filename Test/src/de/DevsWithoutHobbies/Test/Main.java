package de.DevsWithoutHobbies.Test;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;

public class Main extends JavaPlugin {

    private Connection con = null;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
        createConfig();
        connectToMySQL();
    }

    private void connectToMySQL() {
        String server = this.getConfig().getString("mysql-server");
        String port = this.getConfig().getString("mysql-port");
        String user = this.getConfig().getString("mysql-username");
        String password = this.getConfig().getString("mysql-password");
        String database_name = this.getConfig().getString("mysql-database-name");

        try {
            Class.forName("com.mysql.jdbc.Driver");

            con = DriverManager.getConnection("jdbc:mysql://"+server+":"+ port+"/"+ database_name +"?"+"user="+user+"&"+"password="+password);
        } catch (ClassNotFoundException e) {
            getLogger().warning("Driver not found");
        } catch (SQLException e) {
            getLogger().warning("No Connection Possible");
            getLogger().warning("SQLException: " + e.getMessage());
            getLogger().warning("SQLState: " + e.getSQLState());
            getLogger().warning("VendorError: " + e.getErrorCode());
        }
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

        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
    }

    void updatePlayerSpeed(String playername, double maxspeed) {
        try {
            Statement query = con.createStatement();

            String sql_request = "SELECT Username, MaxSpeed, UserID FROM Users WHERE Username LIKE \"" + playername + "\"";
            ResultSet result = query.executeQuery(sql_request);

            while (result.next()) {
                if (maxspeed > Float.parseFloat(result.getString("MaxSpeed"))) {
                    String sql_updated = "UPDATE Users SET MaxSpeed = " + maxspeed + " WHERE UserID = " + result.getString("UserID");
                    query.executeUpdate(sql_updated);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("color")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command can only be run by a player.");
            } else {
                if (args.length >= 2) {
                    Player player = (Player) sender;
                    Location loc = player.getLocation();
                    int r = Integer.parseInt(args[0]);
                    boolean is_block = args[1].equalsIgnoreCase("yes");

                    int start_x = (int) loc.getX();
                    int start_y = (int) loc.getY();
                    int start_z = (int) loc.getZ();

                    for (int x = start_x - r; x <= start_x + r; x++) {
                        for (int y = start_y - r; y <= start_y + r; y++) {
                            for (int z = start_z - r; z <= start_z + r; z++) {
                                boolean block_in_list = false;
                                Block block = player.getWorld().getBlockAt(x, y, z);
                                for (int arg_id = 2; arg_id < args.length; arg_id++) {
                                    if (PatternMatcher.match(args[arg_id], block.getType().toString())) {
                                        block_in_list = true;
                                    }
                                }
                                if (block_in_list == is_block) {
                                    block.setType(Material.WOOL);
                                    block.setData((byte) (y % 16));
                                }
                            }
                        }
                    }
                }
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("wildcard")) {
            if (args.length >= 2) {
                if (PatternMatcher.match(args[0], args[1])) {
                    sender.sendMessage(args[0] + " -> " + args[1]);
                } else {
                    sender.sendMessage(args[0] + " +> " + args[1]);
                }
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("up")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command can only be run by a player.");
            } else {
                if (args.length == 1) {
                    Player player = (Player) sender;
                    Location loc = player.getLocation();
                    loc.setY(loc.getY() + Integer.parseInt(args[0]));
                    player.teleport(loc);
                    loc.setY(loc.getY() - 1);
                    player.getWorld().getBlockAt(loc).setType(Material.GLASS);
                    return true;
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("speed")) {
            if (!(sender instanceof Player || sender instanceof ConsoleCommandSender)) {
                sender.sendMessage("This command can only be run by a player.");
            } else {
                if (args.length == 1) {
                    try {
                        Statement query = con.createStatement();

                        String sql = "SELECT Username, MaxSpeed FROM Users WHERE Username LIKE \"" + args[0] + "\"";
                        ResultSet result = query.executeQuery(sql);

                        while (result.next()) {
                            sender.sendMessage(result.getString("Username") + " has a max speed of " + result.getString("MaxSpeed"));
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }


                    return true;
                }
            }
        }
        return false;
    }

}

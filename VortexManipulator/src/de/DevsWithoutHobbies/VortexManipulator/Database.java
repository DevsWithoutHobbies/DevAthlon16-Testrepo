package de.DevsWithoutHobbies.VortexManipulator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.bukkit.Bukkit.getLogger;

/**
 * Created by noah on 03/07/16.
 */
class Database {
    private Connection con = null;

    boolean connect(String server, String port, String database_name, String username, String password) {
        try {
            con = DriverManager.getConnection("jdbc:mysql://"+server+":"+ port+"/"+ database_name +"?"+"user="+username+"&"+"password="+password);
            return true;
        } catch (SQLException e) {
            getLogger().warning("No Connection Possible");
            getLogger().warning("SQLException: " + e.getMessage());
            getLogger().warning("SQLState: " + e.getSQLState());
            getLogger().warning("VendorError: " + e.getErrorCode());
            return false;
        }
    }

    private ResultSet executeQuery(String sql) {
        try {
            return con.createStatement().executeQuery(sql);
        } catch  (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int executeUpdate(String sql) {
        try {
            return con.createStatement().executeUpdate(sql);
        } catch (SQLException e) {
            getLogger().warning("SQL Query failed: " + sql);
            e.printStackTrace();
            return -1;
        }
    }
}

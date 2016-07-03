package de.DevsWithoutHobbies.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.bukkit.Bukkit.getLogger;

/**
 * Created by noah on 03/07/16.
 * This class represents a database and handles the communication
 */

@SuppressWarnings("Since15")
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

    List<TableRow> getData(String table_name, String[] columns, String matches) {
        String columns_string = String.join("`, `", columns);

        String sql = "SELECT `" + columns_string + "` FROM " + table_name + " WHERE " + matches + ";";
        ResultSet table = executeQuery(sql);

        if (table != null) {
            try {
                List<TableRow> result = new ArrayList<TableRow>();
                while (table.next()) {
                    TableRow row = new TableRow();
                    for (String column : columns) {
                        row.put(column, table.getString(column));
                    }
                    result.add(row);
                }
                return result;
            } catch (SQLException e) {
                getLogger().warning("SQL Query failed: " + sql);
                e.printStackTrace();
            }
        }
        return new ArrayList<TableRow>();
    }

    int updateData(String table, String data, String matches) {
        String sql = "UPDATE " + table + " SET " + data + " WHERE " + matches + ";";
        return executeUpdate(sql);
    }

    int addRow(String table, TableRow data) {
        String columns = String.join("`, `", data.keySet());
        String values = String.join("', '", data.values());
        String sql = "INSERT INTO " + table + " (`" + columns + "`) VALUES ('" + values + "');";
        return executeUpdate(sql);
    }
}

class TableRow extends HashMap<String, String> {}
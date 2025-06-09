package rpg.rpg_base.Data;

import net.md_5.bungee.api.ChatColor;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;
import rpg.rpg_base.RPG_Base;

import java.sql.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataBaseManager {
    private static final String DB_URL = "jdbc:sqlite:" + RPG_Base.getInstance().getDataFolder() + "/playerData.db";

    public static Connection databaseConnection;
    private static Statement stmt;

    public static void connectToDb(){
        try{
            databaseConnection = DriverManager.getConnection(DB_URL);
            stmt = databaseConnection.createStatement();

            createTables();
            updateAllTables();
            RPG_Base.getInstance().getLogger().info(ChatColor.GREEN + "Connected to SQLite database.");
        }catch(SQLException e){
            RPG_Base.getInstance().getLogger().severe("Database connection error!!! " + e.getMessage());
        }
    }

    public static void createTables(){
        String sql ="CREATE TABLE IF NOT EXISTS userData (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "UUID STRING," +
                    "USERNAME STRING," +
                    "LVL INTEGER," +
                    "XP INTEGER," +
                    "TOTALXP INTEGER," +
                    "ELVL INTEGER," +
                    "SLVL INTEGER," +
                    "DLVL INTEGER," +
                    "ILVL INTEGER," +
                    "ALVL INTEGER," +
                    "GOLD INTEGER," +
                    "RUNICSIGILS INTEGER," +
                    "GUILDMEDALS INTEGER," +
                    "SPENTSKILLPOINTS INTEGER," +
                    "SPENTABILITYPOINTS INTEGER," +
                    "UNLOCKEDABILITIES INTEGER)";

        String sql1 ="CREATE TABLE IF NOT EXISTS itemData (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "itemSerial STRING, " +
                    "playerUUID STRING, " +
                    "place STRING)";
        try{
            stmt.execute(sql);
            stmt.execute(sql1);
        } catch (SQLException e) {
            RPG_Base.getInstance().getLogger().warning("Database table creation attempt FAILED!!! " + e.getMessage());
        }
    }

    public static void updateAllTables() {
        updateTableColumns(databaseConnection, "userData", Map.ofEntries(
                Map.entry("UUID", "STRING"),
                Map.entry("USERNAME", "STRING"),
                Map.entry("LVL", "INTEGER"),
                Map.entry("XP", "INTEGER"),
                Map.entry("TOTALXP", "INTEGER"),
                Map.entry("ELVL", "INTEGER"),
                Map.entry("SLVL", "INTEGER"),
                Map.entry("DLVL", "INTEGER"),
                Map.entry("ILVL", "INTEGER"),
                Map.entry("ALVL", "INTEGER"),
                Map.entry("GOLD", "INTEGER"),
                Map.entry("RUNICSIGILS", "INTEGER"),
                Map.entry("GUILDMEDALS", "INTEGER"),
                Map.entry("SPENTSKILLPOINTS", "INTEGER"),
                Map.entry("SPENTABILITYPOINTS", "INTEGER"),
                Map.entry("UNLOCKEDABILITIES", "INTEGER")
        ));

        updateTableColumns(databaseConnection, "itemData", Map.ofEntries(
                Map.entry("itemSerial", "STRING"),
                Map.entry("playerUUID", "STRING"),
                Map.entry("place", "STRING")
        ));
    }

    private static void updateTableColumns(Connection conn, String tableName, Map<String, String> requiredColumns) {
        Set<String> existingColumns = new HashSet<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + tableName + ")")) {
            while (rs.next()) {
                existingColumns.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            RPG_Base.getInstance().getLogger().warning("Failed to read table info for " + tableName + ": " + e.getMessage());
            return;
        }

        for (Map.Entry<String, String> column : requiredColumns.entrySet()) {
            if (!existingColumns.contains(column.getKey())) {
                String alterSQL = "ALTER TABLE " + tableName + " ADD COLUMN " + column.getKey() + " " + column.getValue() + ";";
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(alterSQL);
                    RPG_Base.getInstance().getLogger().info("[" + tableName + "] Added missing column: " + column.getKey());
                } catch (SQLException e) {
                    RPG_Base.getInstance().getLogger().warning("[" + tableName + "] Failed to add column " + column.getKey() + ": " + e.getMessage());
                }
            }
        }
    }

    public static void addColumnValueToUserTable(String column, String value, CPlayer player){
        try {
            String uuid = player.getPlayer().getUniqueId().toString();
            ResultSet rs = stmt.executeQuery("SELECT " + column + " FROM userData WHERE UUID = '" + uuid + "'");
            if(!rs.next()){
                stmt.execute("INSERT INTO userData (UUID) VALUES ('" + uuid + "')");
            }
            stmt.execute("UPDATE userData SET " + column + " = '" + value + "' WHERE UUID = '" + uuid + "'");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addColumnValueToUserTable(String column,  int value, CPlayer player){
        try {
            String uuid = player.getPlayer().getUniqueId().toString();
            ResultSet rs = stmt.executeQuery("SELECT " + column + " FROM userData WHERE UUID = '" + uuid + "'");
            if(!rs.next()){
                stmt.execute("INSERT INTO userData (UUID) VALUES ('" + uuid + "')");
            }
            stmt.execute("UPDATE userData SET " + column + " = '" + value + "' WHERE UUID = '" + uuid + "'");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getValueOfCellInUserTable(String data, CPlayer player){
        String str = "0";
        String uuid = player.getPlayer().getUniqueId().toString();
        try {
            ResultSet rs = stmt.executeQuery("SELECT " + data + " FROM userData WHERE UUID = '" + uuid + "'");

            if(rs.next()){
                str = rs.getString(1);
            }
        } catch (SQLException e) {
            RPG_Base.getInstance().getLogger().warning("Error retrieving column from database" + e.getMessage());
        }
        return str;
    }

    public static void addColumnValueToItemTable(String column, String value, CPlayer player){
        try {
            String uuid = player.getPlayer().getUniqueId().toString();
            ResultSet rs = stmt.executeQuery("SELECT " + column + " FROM itemData WHERE playerUUID = '" + uuid + "'");
            if(!rs.next()){
                stmt.execute("INSERT INTO itemData (playerUUID) VALUES ('" + uuid + "')");
            }
            stmt.execute("UPDATE itemData SET " + column + " = '" + value + "' WHERE playerUUID = '" + uuid + "'");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addColumnValueToItemTable(String column,  int value, CPlayer player){
        try {
            String uuid = player.getPlayer().getUniqueId().toString();
            ResultSet rs = stmt.executeQuery("SELECT " + column + " FROM itemData WHERE playerUUID = '" + uuid + "'");
            if(!rs.next()){
                stmt.execute("INSERT INTO itemData (playerUUID) VALUES ('" + uuid + "')");
            }
            stmt.execute("UPDATE itemData SET " + column + " = '" + value + "' WHERE playerUUID = '" + uuid + "'");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getValueOfCellInItemTable(String data, CPlayer player){
        String str = "0";
        String uuid = player.getPlayer().getUniqueId().toString();
        try {
            ResultSet rs = stmt.executeQuery("SELECT " + data + " FROM itemData WHERE playerUUID = '" + uuid + "'");

            if(rs.next()){
                str = rs.getString(1);
            }
        } catch (SQLException e) {
            RPG_Base.getInstance().getLogger().warning("Error retrieving column from database" + e.getMessage());
        }
        return str;
    }


    public static void disconnectFromDB(){
        try {
            if (stmt != null) stmt.close();
            if (databaseConnection != null) databaseConnection.close();
        } catch (SQLException e) {
            RPG_Base.getInstance().getLogger().severe("Database closing error!!! " + e.getMessage());
            e.printStackTrace();  // Optional: prints the stack trace for debugging
        }
    }
}
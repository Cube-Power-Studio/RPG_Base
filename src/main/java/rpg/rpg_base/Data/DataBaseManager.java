package rpg.rpg_base.Data;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import rpg.rpg_base.CustomizedClasses.Entities.MobClasses.spawning.RawSpawningNode;
import rpg.rpg_base.CustomizedClasses.Entities.MobClasses.spawning.SpawningNode;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;
import rpg.rpg_base.CustomizedClasses.items.RpgItem;
import rpg.rpg_base.RPG_Base;

import java.sql.*;
import java.util.*;

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

        String sql1 ="""
                    CREATE TABLE IF NOT EXISTS registeredItems (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        regName STRING UNIQUE,
                        itemParameters STRING
                    )
                    """;

        String sql3 = """
                    CREATE TABLE IF NOT EXISTS spawnNodes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nodeId STRING UNIQUE,
                        location STRING,
                        spawns STRING
                    )
                """;

        try{
            stmt.execute(sql);
            stmt.execute(sql1);
            stmt.execute(sql3);
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

        updateTableColumns(databaseConnection, "registeredItems", Map.ofEntries(
                Map.entry("regName", "STRING"),
                Map.entry("itemParameters", "STRING")
        ));

        updateTableColumns(databaseConnection, "spawnNodes", Map.ofEntries(
                Map.entry("nodeId", "STRING UNIQUE"),
                Map.entry("location", "STRING"),
                Map.entry("spawns", "STRING")
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

    public static void addSpawningNodeToDb(SpawningNode spawningNode){
        try {
            String nodeId = spawningNode.getNodeId();

            Location loc = spawningNode.getLocation();
            String saveLocation = String.format("%s;%d;%d;%d",
                    loc.getWorld().getName(),
                    loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

            String possibleSpawns = String.join(
                    ";",
                    spawningNode.getPossibleSpawns().entrySet()
                            .stream()
                            .filter(Objects::nonNull)
                            .map(x -> x.getKey() + "," + x.getValue())
                            .toList()
            );

            String query = """
            INSERT INTO spawnNodes (nodeId, location, spawns)
            VALUES (?, ?, ?)
            ON CONFLICT(nodeId) DO UPDATE SET
                location = excluded.location,
                spawns = excluded.spawns;
            """;

            try (PreparedStatement pstmt = databaseConnection.prepareStatement(query)) {
                pstmt.setString(1, nodeId);
                pstmt.setString(2, saveLocation);
                pstmt.setString(3, possibleSpawns);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Optional: log better or wrap with a custom error
            throw new RuntimeException(e);
        }
    }

    public static void removeSpawningNodeFromDb(SpawningNode node) {
        try {
            String nodeId = node.getNodeId();

            String querry = """
                    DELETE FROM spawnNodes WHERE nodeId = ?;
                    """;

            try (PreparedStatement pstmt = databaseConnection.prepareStatement(querry)){
                pstmt.setString(1, nodeId);
                pstmt.executeUpdate();
            }
        }catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static List<RawSpawningNode> getSpawningNodes(){
        List<RawSpawningNode> rawNodesList = new ArrayList<>();
        try{
            ResultSet result = stmt.executeQuery("SELECT * FROM spawnNodes");
            while(result.next()){
                RawSpawningNode node = new RawSpawningNode();
                node.nodeId = result.getString("nodeId");
                node.location = result.getString("location");
                node.mobSpawns = result.getString("spawns");
                rawNodesList.add(node);
            }
        }catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return rawNodesList;
    }

    public static void addItemToDb(String itemReg, String item){
        try {
            String query = """
            INSERT INTO registeredItems (regName, itemParameters)
            VALUES (?, ?)
            ON CONFLICT(regName) DO UPDATE SET
                itemParameters = excluded.itemParameters;
            """;

            try (PreparedStatement pstmt = databaseConnection.prepareStatement(query)) {
                pstmt.setString(1, itemReg);
                pstmt.setString(2, item);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Optional: log better or wrap with a custom error
            throw new RuntimeException(e);
        }
    }

    public static void removeItemFromDb(RpgItem rpgItem) {
        try {
            String itemReg = rpgItem.getRegName();

            String querry = """
                    DELETE FROM registeredItems WHERE regName = ?;
                    """;

            try (PreparedStatement pstmt = databaseConnection.prepareStatement(querry)){
                pstmt.setString(1, itemReg);
                pstmt.executeUpdate();
            }
        }catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static HashMap<String, String> getItems(){
        HashMap<String, String> itemMap = new HashMap<>();
        try{
            ResultSet result = stmt.executeQuery("SELECT * FROM registeredItems");
            while(result.next()){
                itemMap.put(result.getString("regName"), result.getString("itemParameters"));
            }
        }catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return itemMap;
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
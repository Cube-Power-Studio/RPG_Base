package rpg.rpg_base.Data;

import net.md_5.bungee.api.ChatColor;
import rpg.rpg_base.CustomizedClasses.PlayerHandler.CPlayer;
import rpg.rpg_base.RPG_Base;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataBaseManager {
    private static final String DB_URL = "jdbc:sqlite:" + RPG_Base.getInstance().getDataFolder() + "/playerData.db";

    public static Connection databaseConnection;
    private static Statement stmt;
    private static final String userDataTable = "userData";
    private static final List<String> registeredColumns = new ArrayList<>();


    public static void connectToDb(){
        try{
            databaseConnection = DriverManager.getConnection(DB_URL);
            stmt = databaseConnection.createStatement();


            ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + userDataTable + ")");
            while (rs.next()) {
                registeredColumns.add(rs.getString("name"));
            }
            System.out.println(registeredColumns);
            createTables();
            RPG_Base.getInstance().getLogger().info(ChatColor.GREEN + "Connected to SQLite database.");
        }catch(SQLException e){
            RPG_Base.getInstance().getLogger().severe("Database connection error!!! " + e.getMessage());
        }
    }
    public static void createTables(){
        String sql = "CREATE TABLE IF NOT EXISTS " + userDataTable + " (id INTEGER PRIMARY KEY AUTOINCREMENT)";
        try{
            stmt.execute(sql);
            createColumns();
        } catch (SQLException e) {
            RPG_Base.getInstance().getLogger().warning("Database table creation attempt FAILED!!! " + e.getMessage());
        }
    }

    public static void createColumns(){
        for(DataBaseColumn column : DataBaseColumn.values()){
            createColumn(column.name(), column.getColumnType());
        }
    }

    public static void createColumn(String columnName, String type){
        if(!registeredColumns.contains(columnName)) {
            try {
                stmt.execute("ALTER TABLE " + userDataTable + " ADD COLUMN " + columnName + " " + type);
            } catch (SQLException e) {
                RPG_Base.getInstance().getLogger().warning("Something went wrong while adding column to database!!! " + e.getMessage());
            }
        }
    }


    public static void addColumnValue(DataBaseColumn data, String value, CPlayer player){
        try {
            String uuid = player.getPlayer().getUniqueId().toString();
            ResultSet rs = stmt.executeQuery("SELECT " + data + " FROM " + userDataTable + " WHERE UUID = '" + uuid + "'");
            if(!rs.next()){
                stmt.execute("INSERT INTO " + userDataTable + " (UUID) VALUES ('" + uuid + "')");
            }
            stmt.execute("UPDATE " + userDataTable + " SET " + data + " = '" + value + "' WHERE UUID = '" + uuid + "'");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void addColumnValue(DataBaseColumn data, int value, CPlayer player){
        try {
            String uuid = player.getPlayer().getUniqueId().toString();
            ResultSet rs = stmt.executeQuery("SELECT " + data + " FROM " + userDataTable + " WHERE UUID = '" + uuid + "'");
            if(!rs.next()){
                stmt.execute("INSERT INTO " + userDataTable + " (UUID) VALUES ('" + uuid + "')");
            }
            stmt.execute("UPDATE " + userDataTable + " SET " + data + " = '" + value + "' WHERE UUID = '" + uuid + "'");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static String getValueOfCell(DataBaseColumn data, CPlayer player){
        String str = "0";
        String uuid = player.getPlayer().getUniqueId().toString();
        try {
            ResultSet rs = stmt.executeQuery("SELECT " + data + " FROM " + userDataTable + " WHERE UUID = '" + uuid + "'");

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
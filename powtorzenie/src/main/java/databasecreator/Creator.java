package databasecreator;


import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Creator {

    public static void main(String[] args) {
        String url = "jdbc:sqlite:sample.db";
        Creator creator = new Creator();
        creator.create(url);

    }
    public void create(String url){
        String sql = "CREATE TABLE IF NOT EXISTS eeg_signals ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username TEXT NOT NULL,"
                + "electrode_number INTEGER NOT NULL,"
                + "plot_base64 TEXT NOT NULL"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table created successfully.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void delete(String url) {
        String filepath = url.substring(url.indexOf("\\"));
        File dbFile = new File(filepath);
        if (dbFile.exists()) {
            if (!dbFile.delete()){
                System.out.println("Error during delete database");
            }
        }else{
            System.out.println("Error database dosent exist");
        }
    }
}
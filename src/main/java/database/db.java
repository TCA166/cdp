package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import io.quarkus.logging.Log; 
import io.quarkus.scheduler.Scheduled;

//class that handles the database

public class db{

    public static Connection getConn(){
        String url = "jdbc:sqlite:/home/tca/java/cdp/src/main/resources/main.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            Log.error(e.getMessage());
        }
        return conn;
    }

    //every day at midnight cull keys that might be too old
    @Scheduled(cron="0 0 0 * * ?")     
    void cullKeys() {
        Log.info("Culling keys...");
        try(
            Connection conn = getConn();
            Statement stmt = conn.createStatement();
        ){
            int cnt = stmt.executeUpdate("DELETE FROM keys WHERE date(expiry) < date('now')");
            Log.info("Deleted " + Integer.toString(cnt) + " rows");
        }
        catch(SQLException e){
            Log.error(e.getMessage());
        }
    }
}
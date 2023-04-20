package structs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Random;

import org.json.JSONObject;

import database.db;
import io.quarkus.logging.Log;

public class key extends dbItem{
    //Each key has a uid, but it's handled by sql
    private final int uid;
    //The actual key value, read only and not null
    private final String hash;
    //Expiry date in yyyy-mm-dd, may be null
    private final String expiry;
    //Is the key admin type
    private final boolean admin;
    //Uid of owner
    private final int ownerId;

    public String generateHash(int size){
        byte[] array = new byte[8];
        new Random().nextBytes(array);
        return new String(Base64.getEncoder().encode(array));
    }

    public key(String expiry, boolean admin, int ownerId){
        this.uid = 0;
        this.hash = generateHash(8); //64 bit large random String
        this.expiry = expiry;
        this.admin = admin;
        this.ownerId = ownerId;
    }

    public key(int uid, String expiry, boolean admin, int ownerId){
        this.uid = uid;
        this.hash = generateHash(8); //64 bit large random String
        this.expiry = expiry;
        this.admin = admin;
        this.ownerId = ownerId;
    }

    public key(int uid, String hash, String expiry, boolean admin, int ownerId){
        this.uid = uid;
        this.hash = hash;
        this.expiry = expiry;
        this.admin = admin;
        this.ownerId = ownerId;
    }

    public JSONObject toJSON(){
        JSONObject obj = new JSONObject();
        obj.put("hash", this.hash);
        obj.put("expiry", this.expiry);
        obj.put("admin", this.admin);
        return obj;
    }

    public boolean insertInto(){
        try(
            Connection conn = db.getConn();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO keys(key, expiry, admin, owner) VALUES(?1, ?2, ?3, ?4)");
        ){
            pstmt.setString(1, this.hash);
            pstmt.setString(2, this.expiry);
            if(this.admin){
                pstmt.setString(3, "1");
            }
            else{
                pstmt.setString(3, "0");
            }
            pstmt.setString(4, Integer.toString(this.ownerId));
            pstmt.executeUpdate();
            pstmt.close();
            //conn.commit();
            conn.close();
            return true;
        }   
        catch(SQLException e){
            Log.error(e.getMessage());
            return false;
        }
    }

    public String getHash(){
        return this.hash;
    }

    public String getExpiry(){
        return this.expiry;
    }

    public Boolean isAdmin(){
        return this.admin;
    }

    public int getUid(){
        return this.uid;
    }

}

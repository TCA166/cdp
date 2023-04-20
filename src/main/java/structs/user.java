package structs;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.json.JSONObject;

import database.db;
import io.quarkus.logging.Log;

public class user extends dbItem{

    private final int uid;

    private final String login;

    private final String salt;

    private final String pass;

    private final boolean admin;

    //Creates a new salt password for this user
    public user(String login, String pass, Boolean admin) throws Exception{
        this.uid = 0;
        this.login = login;
        this.salt = getNewSalt();
        this.pass = getEncryptedPassword(pass, this.salt);
        this.admin = admin;
    }

    //Merely loads preexisting values
    public user(int uid, String login, String salt ,String pass, Boolean admin){
        this.uid = uid;
        this.login = login;
        this.salt = salt;
        this.pass = pass;
        this.admin = admin;
    }

    public boolean passMatch(String pass){
        try{
            return this.pass.equals(getEncryptedPassword(pass, this.salt));
        }
        catch(Exception e){
            Log.error(e.getMessage());
            return false;
        }
    }

    public boolean isAdmin(){
        return this.admin;
    }

    public int getUid(){
        return this.uid;
    }

    public boolean insertInto(){
        try(
            Connection conn = db.getConn();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users(login, salt, pass, admin) VALUES(?1, ?2, ?3, ?4)");
        ){
            pstmt.setString(1, this.login);
            pstmt.setString(2, this.salt);
            pstmt.setString(3, this.pass);
            if(this.admin){
                pstmt.setString(4, "1");
            }
            else{
                pstmt.setString(4, "0");
            }
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

    public JSONObject toJSON(){
        JSONObject obj = new JSONObject();
        obj.put("login", this.login);
        obj.put("salt", this.salt);
        obj.put("pass", this.pass);
        obj.put("admin", this.admin);
        return obj;
    }

    //encryption stuff based on: https://www.quickprogrammingtips.com/java/how-to-securely-store-passwords-in-java.html
    
    // Returns base64 encoded salt
    public String getNewSalt() throws Exception {
        // Don't use Random!
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        // NIST recommends minimum 4 bytes. We use 8.
        byte[] newSalt = new byte[8];
        random.nextBytes(newSalt);
        return Base64.getEncoder().encodeToString(newSalt);
    }

    // Get a encrypted password using PBKDF2 hash algorithm
    public String getEncryptedPassword(String password, String salt) throws Exception {
        String algorithm = "PBKDF2WithHmacSHA1";
        int derivedKeyLength = 160; // for SHA1
        int iterations = 20000; // NIST specifies 10000
 
        byte[] saltBytes = Base64.getDecoder().decode(salt);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, iterations, derivedKeyLength);
        SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);
 
        byte[] encBytes = f.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(encBytes);
    }
}

package org.acme;

import database.db;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

import io.quarkus.logging.Log;
import structs.key;
import structs.user; 

//endpoint for modifying database
@Path("/key")
public class keyTools {
    //Returns true if the key is valid. If Admin=True then it will also check if key is admin
    public static boolean isValid(String key, boolean Admin){
        //Select data from database
        try(
            Connection conn = db.getConn();
            PreparedStatement pstmt = conn.prepareStatement("SELECT expiry, admin FROM keys WHERE key=?1");
        )
        {
            pstmt.setString(1, key);
            ResultSet rs = pstmt.executeQuery();
            boolean exists = rs.next();
            String expiry = rs.getString("expiry");
            boolean admin = rs.getBoolean("admin");
            rs.close();
            pstmt.close();
            conn.close();
            boolean argTime = expiry == null || new SimpleDateFormat("yyyy-MM-DD").parse(expiry).before(new Date());
            boolean argAdmin = admin == true || Admin == false;
            return exists && argTime && argAdmin;
        } catch (Exception e) {
            //Else log error and return null
            Log.error(e.getMessage());
            return false;
        }
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/valid")
    public boolean getValid(@RestForm("key") String key){
        return isValid(key, false);
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/valid/admin")
    public boolean getAdmin(@RestForm("key") String key){
        return isValid(key, true);
    }

    //Essentially login
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get")
    public String getKey(@RestForm("login") String login, @RestForm("pass") String pass, @RestForm("expiry") String expiry){
        user u;
        try(
            Connection conn = db.getConn();
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE login=?1");
        )
        {
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            u = new user(rs.getInt("uid"), login, rs.getString("salt"), rs.getString("pass"), rs.getBoolean("admin"));
            rs.close();
            pstmt.close();
            conn.close();
            if(!u.passMatch(pass)){
                return "Login failed";
            }
        }
        catch(Exception e){
            Log.error(e.getMessage());
            return "Error during login";
        }
        //Expiry date attribute checks
        if(expiry.equals("auto")){
            Date today = new Date();
            Calendar c = Calendar.getInstance(); 
            c.setTime(today); 
            c.add(Calendar.DATE, 1);
            Date tomorrow = c.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            expiry = formatter.format(tomorrow);
        }
        else if(expiry != null){
            if(expiry.length() != 10){
                return "Date should be in format YYYY-MM-DD";
            }
        }
        else if(!u.isAdmin()){
            return "Unauthorized to create keys without an expiry date";
        }
        //Create key object
        key newKey = new key(expiry, u.isAdmin(), u.getUid());
        if(!newKey.insertInto()){
            return "Internal error during database insertion";
        }
        return newKey.toJSON().toString();
    }

    //Just plain user register
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/register")
    public RestResponse<Object> register(@RestForm("login") String login, @RestForm("pass") String pass){
        user u; 
        try{
            u = new user(login, pass, false); //you can't create admins using the API - that sounds like too much of a risk
        }
        catch(Exception e){
            Log.error(e.getMessage());
            return ResponseBuilder.create(500, "Internal error during object creation").build();
        }
        
        if(!u.insertInto()){
            return ResponseBuilder.create(500, "Internal error during insertion").build();
        }
        return ResponseBuilder.ok().build();
    }
}

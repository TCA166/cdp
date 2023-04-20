package org.acme;

import database.db;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestForm;

import io.quarkus.logging.Log; 

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
}

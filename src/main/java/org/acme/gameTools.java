package org.acme;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

import org.jboss.resteasy.reactive.RestForm;

import database.db;

import io.quarkus.logging.Log; 

@Path("games")
public class gameTools {
    
    @POST
    @Path("/delete")
    public RestResponse<Object> delete(@RestForm("uid") int uid, @RestForm("key") String key){
        //Authority checks
        if(!keyTools.isValid(key, true)){
            return ResponseBuilder.create(401, "Invalid key").build();
        }
        try(
            Connection conn = db.getConn();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM games WHERE uid=?1");
        ){
            pstmt.setString(1, Integer.toString(uid));
            pstmt.executeUpdate();
            pstmt.close();
            //conn.commit();
            conn.close();
            return ResponseBuilder.ok().build();
        }
        catch(SQLException e){
            Log.error(e.getMessage());
            return null;
        }
    }

    @POST
    @Path("/add")
    public RestResponse<Object> add(@RestForm("key") String key, @RestForm("name") String name, @RestForm("date") String date, @RestForm("studio") String studio){
        if(!keyTools.isValid(key, true)){
            return ResponseBuilder.create(401, "Unauthorized").build();
        }
        //Sanity checks
        if(name == null){
            return ResponseBuilder.create(400, "Name can't be null").build();
        }
        //Date and studio can be null (Games that have yet to be released, games by indie devs)
        if(date != null){
            if(date.length() != 10){
                return ResponseBuilder.create(400, "Date should be in format YYYY-MM-DD").build();
            }
        }
        else{
            date = "";
        }
        if(studio == null){
            studio = "";
        }
        try(
            Connection conn = db.getConn();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO games(name, date, studio) VALUES(?1, ?2, ?3)");
        ){
            pstmt.setString(1, name);
            pstmt.setString(2, date);
            pstmt.setString(3, studio);
            pstmt.executeUpdate();
            pstmt.close();
            //conn.commit();
            conn.close();
            return ResponseBuilder.ok().build();
        }   
        catch(SQLException e){
            Log.error(e.getMessage());
            return ResponseBuilder.create(500, "Internal error during deletion").build();
        }
    }

    @POST 
    @Path("/update")
    public RestResponse<Object> update(@RestForm("key") String key, @RestForm("uid") String uid, @RestForm("attr") String attr, @RestForm("val") String val){
        if(!keyTools.isValid(key, true)){
            return ResponseBuilder.create(401, "Unauthorized").build();
        }
        if(attr == null || val == null){
            return ResponseBuilder.create(400, "Attr and val cannot be null").build();
        }
        if(attr == "date"){
            if(val.length() != 10){
                return ResponseBuilder.create(400, "Date should be in format YYYY-MM-DD").build();
            }
        }
        try(
            Connection conn = db.getConn();
            PreparedStatement pstmt = conn.prepareStatement("UPDATE games SET " + attr + "=?2 WHERE uid=?3 ");
        ){
            pstmt.setString(2, val);
            pstmt.setString(3, uid);
            pstmt.executeUpdate();
            pstmt.close();
            //conn.commit();
            conn.close();
            return ResponseBuilder.ok().build();
        }   
        catch(SQLException e){
            Log.error(e.getMessage());
            return ResponseBuilder.create(500, "Internal error during update").build();
        }
    }
}

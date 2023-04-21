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
import structs.game; 

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
            int res = pstmt.executeUpdate();
            pstmt.close();
            //conn.commit();
            conn.close();
            if(res == 1){
                return ResponseBuilder.ok().build();
            }
            else{
                return ResponseBuilder.create(500, "SQL query did something unexpected: altered " + Integer.toString(res)).build();
            }
            
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
        game g = new game(name, date, studio);
        if(!g.insertInto()){
            return ResponseBuilder.create(500, "Internal error during deletion").build();
        }
        return ResponseBuilder.ok().build();
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
            int res = pstmt.executeUpdate();
            pstmt.close();
            //conn.commit();
            conn.close();
            if(res == 1){
                return ResponseBuilder.ok().build();
            }
            else{
                return ResponseBuilder.create(500, "SQL query did something unexpected: altered " + Integer.toString(res)).build();
            }
        }   
        catch(SQLException e){
            Log.error(e.getMessage());
            return ResponseBuilder.create(500, "Internal error during update").build();
        }
    }
}

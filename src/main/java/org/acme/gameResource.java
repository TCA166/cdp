package org.acme;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.POST;

import java.util.ArrayList;
import java.util.List;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import org.jboss.resteasy.reactive.RestForm;
import org.json.JSONArray;

import database.db;

import io.quarkus.logging.Log;
import structs.game; 

//endpoint for browsing games
@Path("/games")
public class gameResource {

    //Gets the data from the sql database
    private List<game> getGames(){
        //Select data from database
        try (
            Connection conn = db.getConn();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name, date, studio FROM games")
            )
        {
            List<game> result = new ArrayList<game>();
            while(rs.next()) {
                game g = new game(rs.getString("name"), rs.getString("date"), rs.getString("studio"));
                result.add(g);
            }
            rs.close();
            stmt.close();
            conn.close();
            return result;
        } catch (SQLException e) {
            //Else log error and return null
            Log.error(e.getMessage());
            return null;
        }
        
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String returnGames(@QueryParam("name") String name, @QueryParam("date") String date, @QueryParam("studio") String studio){
        List<game> result = getGames();
        //Parse into JSON
        JSONArray json = new JSONArray();
        for(game g : result){
            //we check if either no name filter or matches
            boolean arg1 = name == null || g.getName().contains(name);
            //date can be null we need to check that
            boolean arg2 = g.getDate() == null;
            if(!arg2){
                //if it ain't then we do the regular check
                arg2 = date == null || g.getDate().contains(date); 
            }
            
            boolean arg3 = g.getStudio() == null;
            if(!arg3){
                arg3 = studio == null || g.getStudio().contains(studio);
            } 

            if(arg1 && arg2 && arg3){
                json.put(g.toJSON());
            }
        }
        //Return
        return json.toString();
    }

    //very similar to getGames() but filters games by key
    private List<game> getOwnedGames(String key){
        try(
            Connection conn = db.getConn();
            PreparedStatement pstmt = conn.prepareStatement(
                //that databases class coming in real handy rn
                "SELECT name, date, studio FROM games WHERE uid IN (SELECT game FROM owners WHERE user=(SELECT uid FROM users JOIN(SELECT owner FROM keys WHERE key=?1) ON owner=uid))" 
            );
        )
        {
            pstmt.setString(1, key);
            ResultSet rs = pstmt.executeQuery();
            List<game> result = new ArrayList<game>();
            while(rs.next()) {
                game g = new game(rs.getString("name"), rs.getString("date"), rs.getString("studio"));
                result.add(g);
            }
            rs.close();
            pstmt.close();
            conn.close();
            return result;
        } catch (SQLException e) {
            Log.error(e.getMessage());
            return null;
        }
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String returnOwnedGames(@RestForm("key") String key){
        List<game> result = getOwnedGames(key);
        JSONArray json = new JSONArray();
        for(game g : result){
            json.put(g.toJSON());
        }
        //Return
        return json.toString();
    }
}
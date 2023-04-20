package org.acme;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.List;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;

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
}
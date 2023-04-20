package org.acme;

import org.json.JSONObject;

public class game {
    //Each game has a name, that is not null, but is changeable
    private String name;
    //Each game might have a release date, but once it's set it's unchangeable
    private final String date;
    //--|--
    private final String studio;
    //Uid is handled by sql, thus is here as read only and not that important
    private final int uid;

    public game(String name, String date, String studio){
        this.uid = 0; //uid will be determined later
        this.name = name;
        this.date = date;
        this.studio = studio;
    }

    public game(int uid, String name, String date, String studio){
        this.uid = uid;
        this.name = name;
        this.date = date;
        this.studio = studio;
    }

    public int getUid(){
        return this.uid;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String newName){
        this.name = newName;
    }

    public String getDate(){
        return this.date;
    }

    public String getStudio(){
        return this.studio;
    }
    
    public JSONObject toJSON(){
        JSONObject obj = new JSONObject();
        obj.put("name", this.name);
        obj.put("date", this.date);
        obj.put("studio", this.studio);
        return obj;
    }
}

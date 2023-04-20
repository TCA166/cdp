package structs;

import org.json.JSONObject;

public abstract class dbItem {
    private final int uid;

    public abstract boolean insertInto();

    public abstract JSONObject toJSON();

    public dbItem(){
        this.uid = 0;
    }

    public dbItem(int uid){
        this.uid = uid;
    }

    public int getUid(){
        return this.uid;
    }
}

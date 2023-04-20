package org.acme;

import java.nio.charset.Charset;
import java.util.Random;

public class key {
    //Each key has a uid, but it's handled by sql
    private final int uid;
    //The actual key value, read only and not null
    private final String hash;
    //Expiry date in yyyy-mm-dd, may be null
    private final String expiry;
    //Is the key admin type
    private final boolean admin;

    public String generateHash(int size){
        byte[] array = new byte[8];
        new Random().nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
    }

    public key(String expiry, boolean admin){
        this.uid = 0;
        this.hash = generateHash(8); //64 bit large random String
        this.expiry = expiry;
        this.admin = admin;
    }

    public key(int uid, String expiry, boolean admin){
        this.uid = uid;
        this.hash = generateHash(8); //64 bit large random String
        this.expiry = expiry;
        this.admin = admin;
    }

    public key(int uid, String hash, String expiry, boolean admin){
        this.uid = uid;
        this.hash = hash;
        this.expiry = expiry;
        this.admin = admin;
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

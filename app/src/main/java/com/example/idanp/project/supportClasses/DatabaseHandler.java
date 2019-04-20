package com.example.idanp.project.supportClasses;

import android.support.annotation.NonNull;

import com.google.firebase.database.FirebaseDatabase;

public class DatabaseHandler {
    private static FirebaseDatabase firebase;

    /**
     * Basic constructor of the DatabaseHandler
     * @param firebase the firebase database reference.
     */
    public DatabaseHandler(FirebaseDatabase firebase){
        this.firebase = firebase;
    }


    public static <T> T readFromDatabase(String key){
        return (T)firebase.getReference().child(key);
    }

    /**
     * Inputs into the firebase a value (an object) at the key (path).
     * @param key the path of the wanted directory
     * @param value the object that is to be inserted into the database
     * @param <T> the type of the object
     */
    public static <T> void writeToDatabase(String key, T value){
        firebase.getReference().child(key).setValue(value);
    }

    public static <T> void removeFromDatabase(String key){
        firebase.getReference().child(key).removeValue();
    }
}

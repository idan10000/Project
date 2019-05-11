package com.example.idanp.project.supportClasses;

import android.net.Uri;

import com.example.idanp.project.supportClasses.baseClasses.BaseGrade;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class Grade extends BaseGrade {

    private String id;
    private List<Uri> testPictures;
    private GregorianCalendar date;

    public Grade(String name, int grade, String id, int year, int month, int day) {
        super(name, grade);
        this.id = id;
        testPictures = new ArrayList<>();
        date = new GregorianCalendar(year,month,day);
    }

    public Grade(){

    }

    /**
     * Adds a picture both to the testPictures list, as well as to the firebase in the grade section under the
     * id of 'testPictures'
     * @param picture the URI of the picture that is being added to the Grade
     */
    public void addPicture(Uri picture){
        testPictures.add(picture);
        DatabaseHandler.writeToDatabase(id + "/testPictures", picture);
    }


    public void deletePicture(Uri picture){
        if(testPictures.remove(picture))
            DatabaseHandler.writeToDatabase(id + "/testPictures", testPictures);
        else
            throw new NullPointerException();
    }

    public GregorianCalendar getDate(){
        return  date;
    }

}

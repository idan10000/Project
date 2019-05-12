package com.example.idanp.project.supportClasses;

import android.net.Uri;

import com.example.idanp.project.supportClasses.baseClasses.BaseGrade;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class Grade extends BaseGrade {

    private Uri[] testPictures;
    private GregorianCalendar date;
    private String id;

    public Grade(String name, int grade, String id, int year, int month, int day, Uri[] testPictures) {
        super(name, grade);
        this.testPictures = testPictures;
        this.id = id;
        date = new GregorianCalendar(year,month,day);
    }

    public Grade(){

    }

    public Uri[] getTestPictures() {
        return testPictures;
    }

    public String getId() {
        return id;
    }

    public GregorianCalendar getDate(){
        return  date;
    }

}

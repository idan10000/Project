package com.example.idanp.project.supportClasses;

import com.example.idanp.project.supportClasses.baseClasses.BaseGrade;

import java.util.ArrayList;

public class Grade extends BaseGrade {

    private ArrayList<String> pictures;
    private Date date;
    private String id;

    public Grade(String name, int grade, String id, int year, int month, int day, int distribution, ArrayList<String> pictures) {
        super(name, grade);
        this.pictures = pictures;
        this.id = id;
        date = new Date(year,month,day);
    }

    public Grade(){

    }

    public ArrayList<String> getPictures() {
        return pictures;
    }

    public String getId() {
        return id;
    }

    public Date getDate(){
        return  date;
    }


}

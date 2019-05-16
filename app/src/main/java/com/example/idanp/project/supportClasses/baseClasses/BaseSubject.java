package com.example.idanp.project.supportClasses.baseClasses;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseSubject {
    protected String name;
    protected ArrayList<BaseGrade> grades;

    public BaseSubject(String name){
        this.name = name;
        grades = new ArrayList<>() ;
    }

    public BaseSubject(){

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return an arrayList of {@link BaseGrade} of all the grades in the subject.
     */
    public ArrayList<BaseGrade> getGradesObject() {
        return grades;
    }

    /**
     * @return A list with the int values of the grades
     */
    public ArrayList<Integer> getGrades() {
        ArrayList<Integer> list = new ArrayList<>();
        for(BaseGrade grade : grades){
            list.add(grade.grade);
        }
        return list;
    }

    /**
     * Adds a {@link BaseGrade} to the grade list
     * @param grade
     */
    public void addGrade(BaseGrade grade) {
        grades.add(grade);
    }

    public int getDefaultDistribution(){
        int totalDistribution = 0, defaultDistributionNum = 0;
        for(BaseGrade grade : grades){
            if(!grade.isDefaultDistribution())
                totalDistribution += grade.getDistribution();
            else
                defaultDistributionNum++;
        }
        return  (100 - totalDistribution) / defaultDistributionNum;
    }
}

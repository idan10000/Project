package com.example.idanp.project.supportClasses.baseClasses;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseSubject {
    protected String name;
    protected List<BaseGrade> grades;

    public BaseSubject(String name){
        this.name = name;
        grades = new ArrayList<>() ;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return a list of {@link BaseGrade} of all the grades in the subject.
     */
    public List<BaseGrade> getGradesObject() {
        return grades;
    }

    /**
     * @return A list with the int values of the grades
     */
    public List<Integer> getGrades() {
        List<Integer> list = new ArrayList<>();
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
}

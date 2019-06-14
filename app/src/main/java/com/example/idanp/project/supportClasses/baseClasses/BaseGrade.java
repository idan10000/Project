package com.example.idanp.project.supportClasses.baseClasses;

import java.util.Calendar;
import java.util.GregorianCalendar;

public abstract class BaseGrade {

    protected String name;
    protected int grade;
    protected int distribution; //Default distribution is -1


    //------Constructors------//
    /**
     * Base constructor, makes a default distribution (-1).
     * When calculating an average with a default distribution it will equally divide between all default distribution grades.
     * @param name the name of the test
     * @param grade the numerical grade of the test
     */
    public BaseGrade(String name, int grade){
        this.name = name;
        this.grade = grade;
        distribution = -1;
    }

    public BaseGrade(){

    }

    /**
     * Base constructor, creates a grade with a custom distribution, which will
     * @param name the name of the test
     * @param grade the numerical grade of the test
     * @param distribution the percentage of the average the test takes
     */
    public BaseGrade(String name, int grade, int distribution) throws OutOfDistributionRangeException{
        this.name = name;
        this.grade = grade;
        setDistribution(distribution);
    }


    //------Getters/Setters------//
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGrade() {
        return grade;
    }

    /**
     *
     * @param grade if the grade is between 0-100 (the allowed range) the grade changes to the inserted grade,
     *              otherwise the method throws an OutOfGradeRangeException.
     * @throws OutOfGradeRangeException an exception that indicates that the grade inserted is out of the allowed range.
     */
    public void setGrade(int grade) throws OutOfGradeRangeException {
        if(grade <= 100 && grade >= 0)
        this.grade = grade;
        else
            throw new OutOfGradeRangeException("The GradeTemplate " + grade + "is not in the allowed range of 0-100");
    }

    public int getDistribution() {
        return distribution;
    }

    /**
     * Sets the grades' distribution percentage. The allowed range of the distribution is between -1 (default) to 100.
     * If the distribution is out of the allowed range throws an OutOfDistributionRangeException.
     * @param distribution the wanted distribution percentile.
     * @throws OutOfDistributionRangeException
     */
    public void setDistribution(int distribution) throws OutOfDistributionRangeException {
        if(distribution >= -1 && distribution <=100)
            this.distribution = distribution;
        else
            throw new OutOfDistributionRangeException("The distribution " + distribution + "is not in the allowed range");
    }


    //------Utility Methods------//
    /**
     * The default distribution is -1.
     * @return true if the distribution is default, otherwise false.
     */
    public boolean isDefaultDistribution() { return distribution == -1; }

    //------Exceptions------//
    public class OutOfDistributionRangeException extends Exception {
        /**
         * A custom exception for out of distribution range.
         * @param errorMessage the message that will be sent when the error occurs.
         */
        public OutOfDistributionRangeException(String errorMessage) {
            super(errorMessage);
        }
    }

    public class OutOfGradeRangeException extends  Exception{
        /**
         * A custom exception for out of grade range.
         * @param errorMessage the message that will be sent when the error occurs.
         */
        public OutOfGradeRangeException(String errorMessage) { super(errorMessage);}
    }
}

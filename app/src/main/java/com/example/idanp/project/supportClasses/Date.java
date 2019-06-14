package com.example.idanp.project.supportClasses;

public class Date {
    private int year;
    private int month;
    private int day;

    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public Date(){

    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public Date[] orgenizeByDate(Date[] dates){
        Date earliest = new Date(2100,12,30);
        int earliestPosition = -1;
        Date[] organizedList = new Date[dates.length];
        for(int y = 0; y < dates.length; y++) {
            for (int i = 0; i < dates.length; i++) {
                if (dates[i] != null) {
                    if (earliest.isEarlier(dates[i])) {
                        earliest = dates[i];
                        earliestPosition = i;
                    }
                }
            }
            organizedList[y] = earliest;
            dates[earliestPosition] = null;
        }
        return organizedList;
    }

    /**
     * checks if date1 is earlier then date2
     * @param date the date that is used for the checking
     * @return true if the date1 is earlier than date2, false otherwise
     */
    public boolean isEarlier(Date date){
        if(this.year < date.year)
            return true;
        if(this.month<date.month)
            return true;
        if(this.day<date.day)
            return true;
        return false;
    }

    @Override
    public String toString() {
        return day + "/" + month + "/" + year;
    }
}

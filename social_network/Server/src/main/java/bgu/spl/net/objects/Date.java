package bgu.spl.net.objects;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Date {
    private int day;
    private int month;
    private int year;

    public Date(String date){
        String[] args = date.split("-");
        this.day = Integer.parseInt(args[0]);
        this.month = Integer.parseInt(args[1]);
        this.year = Integer.parseInt(args[2]);
    }

    public int getDay() {return day;}
    public int getMonth() {return month;}
    public int getYear() { return year;}

    /*
    Code idea from stackoverflow, got the idea from there since
    it's our first time using dates.
    https://stackoverflow.com/questions/1116123/how-do-i-calculate-someones-age-in-java
     */
    public int getAge() {
        LocalDate currentDate = LocalDate.now();
        LocalDate birthday = LocalDate.of(getYear(), getMonth(), getDay());
        return (int) ChronoUnit.YEARS.between(birthday, currentDate);
    }
}
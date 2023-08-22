package com.example.cs4120finalproject;

import java.time.LocalDate;

public class Bird implements java.io.Serializable{
    private String type;
    private String sex;
    private LocalDate date;

    public Bird(String type, String sex, LocalDate date){
        this.type = type;
        this.sex = sex;
        this.date = date;
    }

    public String getType(){
        return this.type;
    }

    public String getSex(){
        return this.sex;
    }

    public LocalDate getDate(){
        return this.date;
    }

    @Override
    public String toString(){
        return this.type + ", " + this.sex + ", Seen On: " + this.date.toString();
    }
}

package com.example.lightappcollege;

public class Lesson {
    private String number;
    private String name;
    private String teacher;
    private String cabinet;

    public Lesson(String number, String name, String teacher, String cabinet) {
        this.number = number;
        this.name = name;
        this.teacher = teacher;
        this.cabinet = cabinet;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getCabinet() {
        return cabinet;
    }
}
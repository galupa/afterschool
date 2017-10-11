package com.jemput.middup.jemputan.models;

/**
 * Created by asus on 4/25/2017.
 */

public class Teacher {
    private String name;
    private String school;
    private String group;

    public Teacher() {}

    public Teacher(String name, String school, String group) {
        this.name = name;
        this.school = school;
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}

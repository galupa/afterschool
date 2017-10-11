package com.jemput.middup.jemputan.models;

import java.util.HashMap;

/**
 * Created by Rangga on 4/15/2017.
 */

public class Student {
    private String name;
    private String group;
    private String school;
    private String parentId;
    private String parentNumber;
    private String id;
    private String parentName;
    private boolean approved;
    private int currentStatus;

    public Student() {
        // Needed for Firebase
    }

    public Student(String name, String group, String school, String parentId, String id,
                   String parentNumber, String parentName) {
        this.name = name;
        this.group = group;
        this.school = school;
        this.parentId = parentId;
        this.id = id;
        this.parentNumber = parentNumber;
        this.approved = false;
        this.parentName = parentName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentNumber() {
        return parentNumber;
    }

    public void setParentNumber(String parentNumber) {
        this.parentNumber = parentNumber;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public int getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(int currentStatus) {
        this.currentStatus = currentStatus;
    }
    public String getStatusString(){
        switch (currentStatus) {
            case -1:
                return "Status";
            case 1:
                return "At school";
            case 2:
                return "Ready to be picked";
            case 3:
                return "Parent is arriving";
            case -3:
                return "Picked Up";
            default:
                return "At home";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        return parentId.equals(student.parentId);

    }

    @Override
    public int hashCode() {
        return parentId.hashCode();
    }

    public HashMap<String, Object> updateParam(){
        HashMap<String, Object> map = new HashMap<String, Object>();

        if (name != null) {
            map.put("name", name);
        }
        if (group != null) {
            map.put("group", group);
        }
        if (school != null) {
            map.put("school", school);
        }
        if (parentId != null) {
            map.put("parentId", parentId);
        }
        if (parentNumber != null) {
            map.put("parentNumber", parentNumber);
        }
        if (id != null) {
            map.put("id", id);
        }
        if (parentName != null) {
            map.put("parentName", parentName);
        }
        return map;
    }
}

package com.jemput.rangga.jemputan.models;

import java.util.HashMap;

/**
 * Created by asus on 4/15/2017.
 */

public class PickUpStatus {
    private String studentId;
    private String date;
    private String pickUpTime;
    private int pickUpStatus;
    private String pickUpId;
    private String notes;
    private String picId;
    private String fromParentPicId;
    private int pickerApproved;
    private String notesFromParent;

    public static int TITLE = -1;
    public static int AT_HOME = 0;
    public static int AT_SCHOOL = 1;
    public static int READY_TO_PICK = 2;
    public static int PICKED_UP = 3;

    public static int PICKER_APPROVED = 1;
    public static int PICKER_REJECTED = 2;

    public PickUpStatus(){

    }

    public PickUpStatus(String studentId, String date, String pickUpTime, int pickUpStatus, String pickUpId, String notes, String picId) {
        this.studentId = studentId;
        this.date = date;
        this.pickUpTime = pickUpTime;
        this.pickUpStatus = pickUpStatus;
        this.pickUpId = pickUpId;
        this.notes = notes;
        this.picId = picId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPickUpTime() {
        return pickUpTime;
    }

    public void setPickUpTime(String pickUpTime) {
        this.pickUpTime = pickUpTime;
    }

    public int getPickUpStatus() {
        return pickUpStatus;
    }

    public void setPickUpStatus(int pickUpStatus) {
        this.pickUpStatus = pickUpStatus;
    }

    public String getPickUpId() {
        return pickUpId;
    }

    public void setPickUpId(String pickUpId) {
        this.pickUpId = pickUpId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPicId() {
        return picId;
    }

    public void setPicId(String picId) {
        this.picId = picId;
    }

    public int getPickerApproved() {
        return pickerApproved;
    }

    public void setPickerApproved(int pickerApproved) {
        this.pickerApproved = pickerApproved;
    }

    public String getNotesFromParent() {
        return notesFromParent;
    }

    public void setNotesFromParent(String notesFromParent) {
        this.notesFromParent = notesFromParent;
    }

    public String getFromParentPicId() {
        return fromParentPicId;
    }

    public void setFromParentPicId(String fromParentPicId) {
        this.fromParentPicId = fromParentPicId;
    }

    public HashMap<String, Object> updateParam(){
        HashMap<String, Object> map = new HashMap<String, Object>();
        if (studentId != null) {
            map.put("studentId", studentId);
        }
        if (date != null) {
            map.put("date", date);
        }
        if (pickUpTime != null) {
            map.put("pickUpTime", pickUpTime);
        }
        if (pickUpStatus != 0) {
            map.put("pickUpStatus", pickUpStatus);
        }
        if (pickerApproved != 0) {
            map.put("pickerApproved", pickerApproved);
        }
        if (pickUpId != null) {
            map.put("pickUpId", pickUpId);
        }
        if (notes != null) {
            map.put("notes", notes);
        }
        if (notesFromParent != null) {
            map.put("notesFromParent", notesFromParent);
        }
        if (picId != null) {
            map.put("picId", picId);
        }
        if (fromParentPicId != null) {
            map.put("fromParentPicId", fromParentPicId);
        }
        return map;
    }
}

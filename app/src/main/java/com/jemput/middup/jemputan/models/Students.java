package com.jemput.middup.jemputan.models;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by asus on 4/28/2017.
 */

public class Students {
    public List<Student> ITEMS = new ArrayList<Student>();

    public Map<String, Student> ITEM_MAP = new HashMap<String, Student>();

    public Students() {
    }

    public class StudentComparator implements Comparator<Student> {
        @Override
        public int compare(Student o1, Student o2) {

            return -(o1.getCurrentStatus()-o2.getCurrentStatus());
        }
    }

    public void addItem(Student item) {
        int idx = ITEMS.indexOf(item);
        if(idx >= 0){
            ITEMS.set(idx, item);
        }else {
            ITEMS.add(item);
        }
        Collections.sort(ITEMS, new StudentComparator());
        ITEM_MAP.put(item.getParentId(), item);
    }
    public void removeItem (Student item) {
        System.out.println(ITEMS.remove(item));
        System.out.println(ITEM_MAP.remove(item.getParentId()));
        Collections.sort(ITEMS, new StudentComparator());
    }
    public void editItem (Student item) {
        int idx = ITEMS.indexOf(item);
        System.out.println(idx);
        System.out.println(item.getParentId());
        if(idx >= 0) {
            Student temp = ITEMS.get(idx);
            temp.setCurrentStatus(item.getCurrentStatus());
            ITEMS.set(idx, temp);
            ITEM_MAP.put(temp.getParentId(), temp);
        }
        Collections.sort(ITEMS, new StudentComparator());
    }

}

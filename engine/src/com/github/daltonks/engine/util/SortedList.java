package com.github.daltonks.engine.util;

import java.util.ArrayList;
import java.util.Collections;

public class SortedList<T extends Comparable<? super T>> {
    private ArrayList<T> list;

    public SortedList() {
        list = new ArrayList<>();
    }

    public SortedList(int startingCapacity) {
        list = new ArrayList<>(startingCapacity);
    }

    public void add(T object) {
        int index = Collections.binarySearch(list, object);
        if(index < 0) {
            index = -index - 1;
        }
        list.add(index, object);
    }

    public void addWithoutSorting(T object) {
        list.add(object);
    }

    public boolean remove(T object) {
        int index = Collections.binarySearch(list, object);
        if(index >= 0) {
            list.remove(index);
            return true;
        } else {
            return false;
        }
    }

    public void sort() {
        Collections.sort(list);
    }

    public ArrayList<T> getUnderlyingList() {
        return list;
    }
}
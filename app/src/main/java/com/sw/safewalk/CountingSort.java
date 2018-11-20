package com.sw.safewalk;

import java.util.ArrayList;

public class CountingSort {
    private ArrayList<Incident> sortedArray;

    void sort(ArrayList<Incident> arr) {
        int n = arr.size();
        int output[] = new int[n];
        int count[] = new int[11];

        for (int i = 0; i <= 10; ++i)
            count[i] = 0;

        for (int i = 0; i < n; ++i)
            ++count[arr.get(i).getNivel()];

        for (int i = 1; i <= 10; ++i)
            count[i] += count[i-1];

        for (int i = n-1; i >= 0; i--) {
            output[count[arr.get(i).getNivel()]-1] = arr.get(i).getNivel();
            --count[arr.get(i).getNivel()];
        }

        for (int i = 0; i < n; ++i)
            arr.get(i).setNivel(output[(n-1)-i]);

        sortedArray = arr;
    }

    public ArrayList<Incident> getSortedArray() {
        return sortedArray;
    }
}

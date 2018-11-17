package com.sw.safewalk;

import android.util.Log;

import java.util.ArrayList;

public class CountingSort {
    private ArrayList<Incident> sortedArray;

    void sort(ArrayList<Incident> arr) {
        sortedArray = arr;

        int n = arr.size();
        int output[] = new int[n];
        int count[] = new int[256];

        for (int i = 0; i < 256; ++i)
            count[i] = 0;

        for (int i = 0; i < n; ++i)
            ++count[arr.get(i).getNivel()];

        for (int i = 1; i <= 255; ++i)
            count[i] += count[i-1];

        for (int i = n-1; i >= 0; i--) {
            output[count[arr.get(i).getNivel()]-1] = arr.get(i).getNivel();
            --count[arr.get(i).getNivel()];
        }

        for (int i = 0; i < n; ++i)
            arr.get(i).setNivel(output[i]);
        printArray(arr);
    }

    void printArray(ArrayList<Incident> arr) {
        int n = arr.size();
        for (int i = 0; i < n; i++)
            Log.d("vetor",arr.get(i).getNivel()+" ");

    }

    public ArrayList<Incident> getSortedArray() {
        return sortedArray;
    }
}

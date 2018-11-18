package com.sw.safewalk;

import java.util.ArrayList;

class QuickSort {
    private ArrayList<Incident> sortedArray;

    int partition(ArrayList<Incident> arr, int low, int high) {
        Long pivot = arr.get(high).getHorario();
        int i = (low-1);
        for (int j = low; j < high; j++) {
            if (arr.get(j).getHorario() <= pivot) {
                i++;

                Long temp = arr.get(i).getHorario();
                arr.get(i).setHorario(arr.get(j).getHorario());
                arr.get(j).setHorario(temp);
            }
        }

        Long temp = arr.get(i+1).getHorario();
        arr.get(i+1).setHorario(arr.get(high).getHorario());
        arr.get(high).setHorario(temp);

        return i+1;
    }

    void sort(ArrayList<Incident> arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);

            sort(arr, low, pi-1);
            sort(arr, pi+1, high);
        }

        sortedArray = arr;
    }

    static void printArray(int arr[]) {
        int n = arr.length;
        for (int i=0; i<n; ++i)
            System.out.print(arr[i]+" ");
        System.out.println();
    }

    public ArrayList<Incident> getSortedArray() {
        return sortedArray;
    }

    public static void main(String args[]) {
        int arr[] = {10, 7, 8, 9, 1, 5};
        int n = arr.length;

//        QuickSort ob = new QuickSort();
//        ob.sort(arr, 0, n-1);

        System.out.println("sorted array");
        printArray(arr);
    }
}

package com.rozer.reportprocessor;

import java.util.*;

public class Solution {
    public boolean solution(int[] A, int[] B , int S) {
        int N = A.length;
        // Initialize an array to keep track of assigned slots
        boolean[] assignedSlots = new boolean[S + 1];

        // Initialize a list to store the patients with their preferences
        List<Patient> patients = new ArrayList<>();

        // Populate the patients list with each patient's preferences
        for (int i = 0; i < N; i++) {
            patients.add(new Patient(A[i], B[i]));
        }

        // Sort patients based on their first preference, then second preference
        patients.sort((p1, p2) -> {
            if (p1.pref1 == p2.pref1) {
                return Integer.compare(p1.pref2, p2.pref2);
            }
            return Integer.compare(p1.pref1, p2.pref1);
        });

        // Try to assign patients to one of their preferred slots
        for (Patient p : patients) {
            if (!assignedSlots[p.pref1]) {
                // Assign the patient to their first preference
                assignedSlots[p.pref1] = true;
            } else if (!assignedSlots[p.pref2]) {
                // Assign the patient to their second preference
                assignedSlots[p.pref2] = true;
            } else {
                // If both preferred slots are taken, return false
                return false;
            }
        }

        // If all patients are successfully assigned, return true
        return true;
    }

    // Helper class to store patient preferences
    static class Patient {
        int pref1;
        int pref2;

        Patient(int pref1, int pref2) {
            this.pref1 = pref1;
            this.pref2 = pref2;
        }
    }

    // Example usage
    public static void main(String[] args) {


        int[] A1 = {1,1,3};
        int[] B1 = {2,2,1};

        int S1 = 3;
        System.out.println(new Solution().solution(A1,B1,S1));


        int[] A2 = {3,2,3,1};
        int[] B2 = {1,3,1,2};

        int S2 = 3;
        System.out.println(new Solution().solution(A2,B2,S2));

        int[] A3 = {2,5,6,5};
        int[] B3 = {5,4,2,2};

        int S3 = 8;
        System.out.println(new Solution().solution(A2,B3,S3));

        int[] A = {1,2,1,6,8,7,8};
        int[] B = {2,3,4,7,7,8,7};
        int N = 7;
        int S = 10;
        System.out.println(new Solution().solution(A,B,S));
    }
}

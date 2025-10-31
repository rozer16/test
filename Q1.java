
import java.util.*;

class Solution {
    public boolean solution(int[] A, int[] B, int S) {
        int N = A.length;
        List<List<Integer>> graph = new ArrayList<>();
        
        // Build graph: each patient → list of preferred slots
        for (int i = 0; i < N; i++) {
            graph.add(Arrays.asList(A[i], B[i]));
        }

        // slotAssigned[slot] = which patient currently assigned to this slot (0 = unassigned)
        int[] slotAssigned = new int[S + 1];
        Arrays.fill(slotAssigned, 0);

        // Try to assign each patient
        for (int patient = 1; patient <= N; patient++) {
            boolean[] visited = new boolean[S + 1];
            if (!canAssign(graph, patient, slotAssigned, visited)) {
                return false; // cannot find a slot for this patient
            }
        }
        return true;
    }

    private boolean canAssign(List<List<Integer>> graph, int patient, int[] slotAssigned, boolean[] visited) {
        for (int slot : graph.get(patient - 1)) {
            if (visited[slot]) continue;
            visited[slot] = true;

            // If slot is free OR reassign current occupant to another slot
            if (slotAssigned[slot] == 0 || canAssign(graph, slotAssigned[slot], slotAssigned, visited)) {
                slotAssigned[slot] = patient;
                return true;
            }
        }
        return false;
    }
}

// V2


import java.util.*;

class Solution {
    public boolean solution(int[] A, int[] B, int S) {
        int N = A.length;
        if (N > S) return false; // more patients than slots => impossible
        
        int[] slotAssigned = new int[S + 1];
        int[] visited = new int[S + 1];
        int visitId = 0;

        for (int patient = 1; patient <= N; patient++) {
            visitId++;
            if (!canAssign(patient, A, B, slotAssigned, visited, visitId)) {
                return false;
            }
        }
        return true;
    }

    private boolean canAssign(int patient, int[] A, int[] B, int[] slotAssigned, int[] visited, int visitId) {
        // Check both preferred slots
        int[] prefs = {A[patient - 1], B[patient - 1]};
        for (int slot : prefs) {
            if (visited[slot] == visitId) continue;
            visited[slot] = visitId;

            // If slot free OR current occupant can move
            if (slotAssigned[slot] == 0 || canAssign(slotAssigned[slot], A, B, slotAssigned, visited, visitId)) {
                slotAssigned[slot] = patient;
                return true;
            }
        }
        return false;
    }
}

//V3
import java.util.*;

class Solution {
    public boolean solution(int[] A, int[] B, int S) {
        int N = A.length;
        if (N > S) return false; // more patients than slots => impossible

        int[] slotAssigned = new int[S + 1];  // slot -> patient
        int[] visited = new int[S + 1];
        int visitId = 0;

        for (int patient = 1; patient <= N; patient++) {
            visitId++;
            if (!canAssign(patient, A, B, slotAssigned, visited, visitId)) {
                return false;
            }
        }
        return true;
    }

    private boolean canAssign(int patient, int[] A, int[] B, int[] slotAssigned, int[] visited, int visitId) {
        int a = A[patient - 1];
        int b = B[patient - 1];

        // Try first preference
        if (trySlot(a, patient, A, B, slotAssigned, visited, visitId)) return true;
        // Try second preference
        return trySlot(b, patient, A, B, slotAssigned, visited, visitId);
    }

    private boolean trySlot(int slot, int patient, int[] A, int[] B, int[] slotAssigned, int[] visited, int visitId) {
        if (visited[slot] == visitId) return false;
        visited[slot] = visitId;

        int assignedPatient = slotAssigned[slot];
        if (assignedPatient == 0 || canAssign(assignedPatient, A, B, slotAssigned, visited, visitId)) {
            slotAssigned[slot] = patient;
            return true;
        }
        return false;
    }

    // ------------------- TEST DRIVER -------------------
    public static void main(String[] args) {
        Solution s = new Solution();

        // Basic examples
        System.out.println("Test 1: " + s.solution(new int[]{1,3}, new int[]{2,1}, 3)); // true
        System.out.println("Test 2: " + s.solution(new int[]{1,1}, new int[]{2,2}, 2)); // true
        System.out.println("Test 3: " + s.solution(new int[]{1,1}, new int[]{1,1}, 1)); // false

        // Edge cases
        System.out.println("Test 4: " + s.solution(new int[]{1}, new int[]{1}, 1)); // true
        System.out.println("Test 5: " + s.solution(new int[]{1,2,3}, new int[]{1,2,3}, 3)); // true
        System.out.println("Test 6: " + s.solution(new int[]{1,2,3,4}, new int[]{1,2,3,4}, 3)); // false
        System.out.println("Test 7: " + s.solution(new int[]{1,2,3,4}, new int[]{1,1,1,1}, 4)); // true
        System.out.println("Test 8: " + s.solution(new int[]{1,2,3,4}, new int[]{1,1,1,1}, 3)); // false
        System.out.println("Test 9: " + s.solution(new int[]{1,2,2,3}, new int[]{2,3,3,4}, 4)); // true

        // Performance tests
        int N = 100000, S = 100000;
        int[] A = new int[N], B = new int[N];
        for (int i = 0; i < N; i++) { A[i] = i + 1; B[i] = i + 1; }
        System.out.println("Perf Test 1 (large feasible): " + s.solution(A, B, S)); // true

        N = 100000; S = 99999;
        A = new int[N]; B = new int[N];
        for (int i = 0; i < N; i++) { A[i] = 1; B[i] = 1; }
        System.out.println("Perf Test 2 (large infeasible): " + s.solution(A, B, S)); // false

        N = 100000; S = 100000;
        A = new int[N]; B = new int[N];
        for (int i = 0; i < N; i++) {
            A[i] = (i % 2) + 1;
            B[i] = ((i + 1) % 2) + 1;
        }
        System.out.println("Perf Test 3 (alternating prefs): " + s.solution(A, B, S)); // true
    }
}

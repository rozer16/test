
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

private boolean canAssign(int patient, int[] A, int[] B, int[] slotAssigned, int[] visited, int visitId) {
    int a = A[patient - 1], b = B[patient - 1];
    if (trySlot(a, patient, A, B, slotAssigned, visited, visitId)) return true;
    return trySlot(b, patient, A, B, slotAssigned, visited, visitId);
}

private boolean trySlot(int slot, int patient, int[] A, int[] B, int[] slotAssigned, int[] visited, int visitId) {
    if (visited[slot] == visitId) return false;
    visited[slot] = visitId;
    if (slotAssigned[slot] == 0 || canAssign(slotAssigned[slot], A, B, slotAssigned, visited, visitId)) {
        slotAssigned[slot] = patient;
        return true;
    }
    return false;
}

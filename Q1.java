
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

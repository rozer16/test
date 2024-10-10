import java.util.ArrayList;
import java.util.List;

class Solution {
    public int solution(int[] A, int[] B, int N, int S) {
        if (N > S) {
            return 0; // Not enough slots for all patients
        }

        // Create an adjacency list to represent the graph
        List<Integer>[] graph = new ArrayList[S + 1]; // +1 to handle 1-based indexing of slots
        for (int i = 0; i <= S; i++) {
            graph[i] = new ArrayList<>();
        }

        // Add edges to the graph: patient -> slot
        for (int i = 0; i < N; i++) {
            graph[A[i]].add(i);
            graph[B[i]].add(i);
        }

        // Array to track visited patients and their assigned slots
        int[] visited = new int[N];
        Arrays.fill(visited, -1); // Initialize as not visited

        // Perform Depth First Search (DFS) for each patient
        for (int i = 0; i < N; i++) {
            if (visited[i] == -1) { // If patient i is not visited
                if (!dfs(graph, visited, i, A[i])) { // Try assigning to slot A[i]
                    return 0; // No valid assignment found
                }
            }
        }

        return 1; // All patients assigned successfully
    }

    // Depth First Search (DFS) to check for valid assignment
    private boolean dfs(List<Integer>[] graph, int[] visited, int patient, int slot) {
        if (visited[patient] == slot) {
            return true; // Already assigned to this slot
        }
        if (visited[patient] != -1) {
            return false; // Already assigned to a different slot
        }

        visited[patient] = slot; // Assign patient to the slot

        // Check for conflicts with other patients who prefer this slot
        for (int neighbor : graph[slot]) {
            if (neighbor != patient && !dfs(graph, visited, neighbor, neighbor == patient ? B[neighbor] : A[neighbor])) {
                return false; // Conflict found
            }
        }

        return true; // No conflicts found
    }
}

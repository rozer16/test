import java.util.*;

class Solution {
    public int[] solution(int[] A) {
        int n = A.length;
        int[][] buildings = new int[n][2];

        // Pair (maxHeight, originalIndex)
        for (int i = 0; i < n; i++) {
            buildings[i][0] = A[i];
            buildings[i][1] = i;
        }

        // Sort by allowed max height ascending
        Arrays.sort(buildings, (a, b) -> Integer.compare(a[0], b[0]));

        int[] result = new int[n];
        int nextHeight = Integer.MAX_VALUE;

        // Assign heights greedily from right to left
        for (int i = n - 1; i >= 0; i--) {
            int maxAllowed = buildings[i][0];
            int assigned = Math.min(maxAllowed, nextHeight - 1);
            if (assigned < 1) assigned = 1;  // always positive
            result[buildings[i][1]] = assigned;
            nextHeight = assigned;
        }

        return result;
    }
}

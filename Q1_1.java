class Solution {
    public int solution(int[] A) {
        if (A == null) return 1;
        int n = A.length;
        // Use n+2 so indexes 0..n+1 are valid and we can check up to n+1
        boolean[] present = new boolean[n + 2];

        // Mark found positive integers in range 1..n+1
        for (int val : A) {
            if (val > 0 && val <= n + 1) {
                present[val] = true;
            }
        }

        // Find the smallest positive integer not present
        for (int i = 1; i <= n + 1; i++) {
            if (!present[i]) {
                return i;
            }
        }

        // Should never reach here because loop returns
        return n + 1;
    }
}

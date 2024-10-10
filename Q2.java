
int N = A.length;
        int[] B = new int[N]; // Array to store assigned heights
        Set<Integer> usedHeights = new HashSet<>(); // Keep track of used heights

        for (int i = 0; i < N; i++) {
            int maxHeight = A[i];
            int assignedHeight = maxHeight;

            // Find the largest possible height that's not already used
            while (usedHeights.contains(assignedHeight)) {
                assignedHeight--;
            }

            B[i] = assignedHeight; // Assign the height
            usedHeights.add(assignedHeight); // Mark the height as used
        }

        return B;

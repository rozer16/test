public class Solution {
    public int solution(String letters) {
        // Boolean arrays to track seen lowercase and valid letters
        boolean[] lowercaseSeen = new boolean[26];  // for 'a' to 'z'
        boolean[] validLetters = new boolean[26];   // for 'a' to 'z'
        
        // Traverse through each character in the input string
        for (char c : letters.toCharArray()) {
            if (Character.isLowerCase(c)) {
                // Mark the lowercase letter as seen
                lowercaseSeen[c - 'a'] = true;
            } else {
                // Check if the lowercase of this uppercase letter has been seen
                if (lowercaseSeen[Character.toLowerCase(c) - 'a']) {
                    // Mark this letter as valid
                    validLetters[Character.toLowerCase(c) - 'a'] = true;
                }
            }
        }
        
        // Count the number of valid letters
        int count = 0;
        for (boolean isValid : validLetters) {
            if (isValid) {
                count++;
            }
        }
        
        return count;
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        // Test case 1
        String test1 = "aaAbcCABBc";
        System.out.println(sol.solution(test1)); // Output: 2
        
        // Test case 2
        String test2 = "xyxZYZabcABC";
        System.out.println(sol.solution(test2)); // Output: 6
        
        // Test case 3
        String test3 = "ABCabcAFegF";
        System.out.println(sol.solution(test3)); // Output: 0
    }
}  

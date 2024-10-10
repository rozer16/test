import java.util.*;

public class Solution {
    public int solution(String letters) {
        // Track lowercase and uppercase letter appearances
        boolean[] lowercaseSeen = new boolean[26];   // To track if we've seen lowercase (a-z)
        boolean[] uppercaseSeen = new boolean[26];   // To track if we've seen uppercase (A-Z)
        boolean[] validLetter = new boolean[26];     // To track if a letter is valid (a-z)
        Arrays.fill(validLetter, false);  // Initially assume all letters are invalid

        // Traverse through the string
        for (int i = 0; i < letters.length(); i++) {
            char ch = letters.charAt(i);
            if (Character.isLowerCase(ch)) {
                int index = ch - 'a';
                if (!uppercaseSeen[index]) {
                    // If we haven't seen an uppercase letter for this character, mark it as seen
                    lowercaseSeen[index] = true;
                } else {
                    // If we've already seen an uppercase letter for this character, it's invalid
                    validLetter[index] = false;
                }
            } else if (Character.isUpperCase(ch)) {
                int index = Character.toLowerCase(ch) - 'a';
                if (lowercaseSeen[index]) {
                    // If we've seen the lowercase before, mark the letter as valid
                    validLetter[index] = true;
                } else {
                    // If we haven't seen any lowercase, mark this as invalid
                    validLetter[index] = false;
                }
                // Mark the uppercase letter as seen
                uppercaseSeen[index] = true;
            }
        }

        // Count how many valid letters exist
        int validCount = 0;
        for (int i = 0; i < 26; i++) {
            if (validLetter[i]) {
                validCount++;
            }
        }

        return validCount;
    }

    public static void main(String[] args) {
        Solution solution = new Solution();
        
        // Test cases
        System.out.println(solution.solution("aaAbcCABBc")); // Expected: 2
        System.out.println(solution.solution("xyzXYZabcABC")); // Expected: 6
        System.out.println(solution.solution("ABCabcAefG")); // Expected: 0
    }
}

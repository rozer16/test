/*
Here is the text from the screenshot you provided:

Challenge 1 description

You are given a string letters made of N English letters. Count the number of different letters that appear in both uppercase and lowercase where all lowercase occurrences of the given letter appear before any uppercase occurrence.

For example, for letters = "aaAbcCABBc" the answer is 2. The condition is met for letters ‘a’ and ‘b’, but not for ‘c’.

Write a function:

class Solution { public int solution(String letters); }

that, given a string letters, returns the number of different letters fulfilling the conditions above.

Examples:

	1.	Given letters = "aaAbcCABBc", the function should return 2, as explained above.
	2.	Given letters = "xyxZYZabcABC", the function should return 6.
	3.	Given letters = "ABCabcAFegF", the function should return 0.

Write an efficient algorithm for the following assumptions:

	•	N is an integer within the range [1..100,000];
	•	string letters is made only of letters (a-z and/or A-Z).

Let me know if you need further clarifications!

*/


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

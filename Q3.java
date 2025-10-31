
import java.util.*;

class Solution {
    public int solution(String letters) {
        // Track lowercase and uppercase occurrences
        boolean[] seenLower = new boolean[26];
        boolean[] seenUpper = new boolean[26];
        boolean[] invalid = new boolean[26]; // mark letters that violate rule

        for (char c : letters.toCharArray()) {
            if (Character.isLowerCase(c)) {
                int idx = c - 'a';
                if (seenUpper[idx]) {
                    // Lowercase appears after uppercase — invalid
                    invalid[idx] = true;
                }
                seenLower[idx] = true;
            } else if (Character.isUpperCase(c)) {
                int idx = c - 'A';
                seenUpper[idx] = true;
                // Any lowercase that appears later is invalid — handled above
            }
        }

        int count = 0;
        for (int i = 0; i < 26; i++) {
            if (seenLower[i] && seenUpper[i] && !invalid[i]) {
                count++;
            }
        }
        return count;
    }

    // Small test driver
    public static void main(String[] args) {
        Solution sol = new Solution();
        String[] tests = {
            "aaAbcCABBc",       // 2
            "xyxXYZabcABC",     // 6
            "ABCabcACfg",       // 0
            "aA",               // 1
            "A",                // 0
            "a",                // 0
            "aAaAaA",           // 0 (lowercase not before all uppercase)
            "aaaAAA",           // 1
            "zzzZZZyyyYYY",     // 2
            "abcABC",           // 3
            "ABC",              // 0
            "abc",              // 0,
            "AaBbCc",           // 3 (each lowercase before uppercase)
            "aAbBcCdDeE",       // 5
            "aAaBbBBbAaA"       // 0 (violations)
        };

        int t = 1;
        for (String s : tests) {
            System.out.printf("Test %2d: %-15s → %d%n", t++, s, sol.solution(s));
        }
    }
}

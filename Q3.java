
import java.util.*;

class Solution {
   class Solution {
    public int solution(String letters) {
       int lowerMask = 0;   // bit i = lowercase 'a'+i seen
        int upperMask = 0;   // bit i = uppercase 'A'+i seen
        int invalidMask = 0; // bit i = invalid letter

        for (int i = 0; i < letters.length(); i++) {
            char c = letters.charAt(i);
            if (c >= 'a' && c <= 'z') {
                int bit = 1 << (c - 'a');
                if ((upperMask & bit) != 0) invalidMask |= bit;
                lowerMask |= bit;
            } else {
                int bit = 1 << (c - 'A');
                upperMask |= bit;
            }
        }

        // Letters that appear both lower+upper and not invalid
        int bothMask = lowerMask & upperMask & ~invalidMask;
        return Integer.bitCount(bothMask);
    }
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

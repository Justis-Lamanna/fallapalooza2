package com.github.lucbui.birb;

public class RoundUtils {
    public static String getRoundName(int round) {
        switch (round) {
            case 0: return "Round 1";
            case 1: return "Round 2";
            case 2: return "Quarterfinals";
            case 3: return "Semifinals";
            case 4: return "Finals";
            case 5: return "Winners";
            default: throw new IllegalStateException("Round # " + round + " is unexpected");
        }
    }
}

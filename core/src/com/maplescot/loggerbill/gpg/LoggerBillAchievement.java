package com.maplescot.loggerbill.gpg;

/**
 * Created by james on 22/09/14.
 */
public class LoggerBillAchievement extends Achievement {

    @Override
    public boolean test(int chunk_counter, float cps, long totalPlays, long totalChunks, long bestChunks, float bestCPS) {
        float testValue = 0f;
        if (getType().equals("chunks"))
            testValue = totalChunks;

        if (getType().equals("cps"))
            testValue = cps;

        return doTest(testValue);
    }
}

/*
 * (C) Copyright 2014 MapleScot Development
 * This file licensed under a Creative Commons 3.0 by attribution licence
 * https://creativecommons.org/licenses/by/3.0/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * https://github.com/duriej/LoggerBill
 */
package com.maplescot.loggerbill.misc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.maplescot.loggerbill.gpg.Achievement;
import com.maplescot.loggerbill.gpg.GPG;
import com.maplescot.loggerbill.gpg.Leaderboard;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents an individual profile.
 * Created by james on 23/07/14.
 */
public class Profile implements Json.Serializable {
    private final static String TAG = Profile.class.toString();

    private final static String soundOnStr = "soundOn";
    private final static String musicOnStr = "musicOn";
    private final static String totalPlaysStr = "totalPlays";
    private final static String totalChunksStr = "totalChunks";
    private final static String totalTimeStr = "totalTime";
    private final static String bestChunksStr = "bestChunks";
    private final static String bestCPSStr = "bestCPS";
    private final static String achievementsStr = "achievements";
    private final static String gplusStr = "gplus";
    private static final String CKSUM = "profile_id";
    private static final String CKSUMV = "profile_id_v";
    private final static String tweetedStr = "_tweeted";
    private boolean soundOn = true;
    private boolean musicOn;

    private long totalPlays = 0l;
    private long totalChunks = 0l;
    private long totalTime = 0l;
    private long bestChunks = 0l;
    private float bestCPS = 0f;
    private String ckSum;
    private long ckSumV;
    private static final long ckVersion=1;
    private String gplusId = "";
    private Map<String, Boolean> achievementsMap = new HashMap<String, Boolean>();

    public boolean isSoundOn() {
        return soundOn;
    }

    public void setSoundOn(boolean soundOn) {
        this.soundOn = soundOn;
    }

    public boolean isMusicOn() {
        return musicOn;
    }

    public void setMusicOn(boolean musicOn) {
        this.musicOn = musicOn;
    }

    public long getTotalPlays() {
        return totalPlays;
    }

    public void setTotalPlays(long totalPlays) {
        this.totalPlays = totalPlays;
    }

    public long getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(long totalChunks) {
        this.totalChunks = totalChunks;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public long getBestChunks() {
        return bestChunks;
    }

    public void setBestChunks(long bestChunks) {
        this.bestChunks = bestChunks;
    }

    public float getBestCPS() {
        return bestCPS;
    }

    public void setBestCPS(float bestCPS) {
        this.bestCPS = bestCPS;
    }

    public String getGplusId() {
        return gplusId;
    }

    public void setGplusId(String gplusId) {
        this.gplusId = gplusId;
    }

    public BigDecimal getBestCPSFormatted() {
        return new BigDecimal(bestCPS).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getAvgChunksFormatted() {
        if (totalPlays>0)
            return new BigDecimal((float)totalChunks / totalPlays).setScale(2, RoundingMode.HALF_UP);
        else
            return new BigDecimal(0);
    }

    public void init(Preferences prefs) {
        ckSumV = prefs.getLong(CKSUMV, 0);
        soundOn = prefs.getBoolean(soundOnStr, true);
        musicOn = prefs.getBoolean(musicOnStr, true);
        totalPlays = prefs.getLong(totalPlaysStr, 0l);
        totalChunks = prefs.getLong(totalChunksStr, 0l);
        totalTime = prefs.getLong(totalTimeStr, 0l);
        bestChunks = prefs.getLong(bestChunksStr, 0l);
        bestCPS = prefs.getFloat(bestCPSStr, 0f);
        gplusId = prefs.getString(gplusStr, "");
        ckSum = prefs.getString(CKSUM, null);

        loadAchievements(prefs.getString(achievementsStr, ""));
        if (totalPlays==0 && totalChunks==0 && bestCPS==0f && bestChunks==0 && totalTime==0) {
            ckSum = makeCkSum();
            ckSumV = ckVersion;
        }

        if (!isCkSumValid())
            reset();
    }

    private void reset() {
        soundOn=true;
        musicOn=true;
        totalPlays=0l;
        totalChunks=0l;
        totalTime=0l;
        bestChunks=0l;
        bestCPS=0f;
        gplusId="";
        loadAchievements("");
        ckSum = makeCkSum();
        ckSumV=ckVersion;
    }

    private String makeCkSum() {
        double value = totalPlays*totalChunks*totalTime*bestChunks*bestCPS;

        StringBuilder b = new StringBuilder();
        b.append(value);
        for (Achievement a : ProfileManager.getAchievementManager().getAchievements()) {
            b.append(isAchieved(a));
            b.append(isTweeted(a));
        }
        b.append(gplusId);
        return getMD5EncryptedString(b.toString());
    }

    public boolean isCkSumValid() {
        return ckSumV == ckVersion && makeCkSum().equals(ckSum);
    }

    private void loadAchievements(String achievements) {
        achievementsMap = new HashMap<String, Boolean>();
        for (String s : achievements.split("[|]")) {
            if (!s.isEmpty()) {
                String[] pair = s.split(":");
                achievementsMap.put(pair[0], Boolean.valueOf(pair[1]));
            }
        }
    }

    private String saveAchievements() {
        StringBuilder result = new StringBuilder();
        for (String key : achievementsMap.keySet()) {
            result.append(key).append(":").append(achievementsMap.get(key).booleanValue()).append("|");
        }
        if (result.length() == 0)
            return "";
        return result.substring(0, result.lastIndexOf("|"));
    }

    public void save(Preferences prefs) {
        ckSum = makeCkSum();
        ckSumV=ckVersion;
        prefs.putBoolean(soundOnStr, soundOn);
        prefs.putBoolean(musicOnStr, musicOn);
        prefs.putLong(totalPlaysStr, totalPlays);
        prefs.putLong(totalChunksStr, totalChunks);
        prefs.putLong(totalTimeStr, totalTime);
        prefs.putLong(bestChunksStr, bestChunks);
        prefs.putFloat(bestCPSStr, bestCPS);
        prefs.putString(gplusStr, gplusId);
        prefs.putString(CKSUM, ckSum);
        prefs.putLong(CKSUMV, ckSumV);

        prefs.putString(achievementsStr, saveAchievements());
    }

    public void addPlay(int chunk_counter, float totalTime) {
        // calculate stats
        // CPS only counts if we have a minimum number of chunks chunked
        float cps = chunk_counter>Constants.MIN_CHUNKS_FOR_CPS ? ((float) chunk_counter) / totalTime : 0.01f;

        totalPlays += 1;
        totalChunks += chunk_counter;
        this.totalTime += totalTime;

        if (chunk_counter > bestChunks)
            bestChunks = chunk_counter;

        if (cps > bestCPS)
            bestCPS = cps;

        // test our achievements
        for (Achievement a : ProfileManager.getAchievementManager().getAchievements()) {
            if (a.isIncremental()) {
                GPG.getInstance().setAchievementIncrement(a.getId(), (int) totalChunks, (long)a.getValue());
            }
            if (a.test(chunk_counter, cps, totalPlays, totalChunks, bestChunks, bestCPS)) {
                achievementsMap.put(a.getId(), true);
                GPG.getInstance().unlockAchievement(a.getId());
            }
        }

        // push our scores to the leaderboards
        for (Leaderboard l : ProfileManager.getLeaderboardManager().getLeaderboards()) {
            if (l.getName().equals(bestChunksStr))
                GPG.getInstance().submitScore(l.getId(), (int) bestChunks);

            if (l.getName().equals(totalChunksStr))
                GPG.getInstance().submitScore(l.getId(), (int) totalChunks);

            if (l.getName().equals(totalTimeStr))
                GPG.getInstance().submitScore(l.getId(), (int) (this.totalTime*1000));

            if (l.getName().equals("bestChunksPerSecond"))
                GPG.getInstance().submitScore(l.getId(), (int) (bestCPS * 1000f));
        }
    }

    public boolean isAchieved(Achievement a) {
        Boolean done = achievementsMap.get(a.getId());
        return done == null ? false : done;
    }

    public boolean isTweeted(Achievement a) {
        Boolean done = achievementsMap.get(a.getId() + tweetedStr);
        return done == null ? false : done;
    }

    public void setTweeted(Achievement a) {
        achievementsMap.put(a.getId() + tweetedStr, true);
    }


    public boolean hasUntweetedAchievements() {
        for (String a : achievementsMap.keySet()) {
            if (a.contains(tweetedStr))
                continue;
            if (achievementsMap.get(a) && (!achievementsMap.containsKey(a+tweetedStr) || !achievementsMap.get(a+tweetedStr)))
                return true;
        }
        return false;
    }

    public byte[] serialize() {
        ckSum = makeCkSum();
        ckSumV=ckVersion;
        Json json = new Json();

        String out = json.prettyPrint(this);

        Gdx.app.debug(TAG, out);
        return out.getBytes();
    }

    public Profile deserialize(byte[] profile) {
        if (profile == null)
            return null;

        String in = new String(profile);
        Gdx.app.debug(TAG, in);
        Json json = new Json();
        try {
            Profile p = json.fromJson(Profile.class, in);
            return !p.isCkSumValid() ? null : p;
        } catch (Exception e) {
            Gdx.app.error(TAG, "Failed to parse json profile", e);
        }
        return null;
    }

    public boolean selectConflictedProfile(String account, Profile p1, Profile p2) {
        if (p1 == null)
            return false;
        if (p2 == null)
            return true;

        // If the gplus account has changed then switch to that profile
        if (!account.equals(p2.gplusId))
            return true;

        if (p1.totalPlays == p2.totalPlays)
            return p1.bestChunks > p2.bestChunks;
        else return p1.totalPlays > p2.totalPlays;
    }

    public static String getMD5EncryptedString(String encTarget){
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception while encrypting to md5");
            e.printStackTrace();
        } // Encryption algorithm
        mdEnc.update(encTarget.getBytes(), 0, encTarget.length());
        String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
        while ( md5.length() < 32 ) {
            md5 = "0"+md5;
        }
        return md5;
    }

    /*
     * As we use Proguard on Android its best to do this explicitly via an interface override
     * than implicitly through reflection. Works better with GWT too.
     */
    @Override
    public void write(Json json) {
        json.writeValue(soundOnStr, soundOn);
        json.writeValue(musicOnStr, musicOn);
        json.writeValue(totalPlaysStr, totalPlays);
        json.writeValue(totalChunksStr, totalChunks);
        json.writeValue(totalTimeStr, totalTime);
        json.writeValue(bestChunksStr, bestChunks);
        json.writeValue(bestCPSStr, bestCPS);
        json.writeValue(gplusStr, gplusId);
        json.writeValue(CKSUM, ckSum);
        json.writeValue(CKSUMV, ckSumV);

        json.writeValue(achievementsStr, saveAchievements());
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        soundOn = jsonData.get(soundOnStr).asBoolean();
        musicOn = jsonData.get(musicOnStr).asBoolean();
        totalPlays = jsonData.get(totalPlaysStr).asLong();
        totalChunks = jsonData.get(totalChunksStr).asLong();
        totalTime = jsonData.get(totalTimeStr).asLong();
        bestChunks = jsonData.get(bestChunksStr).asLong();
        bestCPS = jsonData.get(bestCPSStr).asFloat();
        gplusId = jsonData.get(gplusStr).asString();
        ckSum = jsonData.get(CKSUM).asString();
        ckSumV = jsonData.get(CKSUMV).asLong();

        loadAchievements(jsonData.get(achievementsStr).asString());
    }
}

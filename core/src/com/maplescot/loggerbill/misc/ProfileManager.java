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
import com.maplescot.loggerbill.gpg.AchievementManager;
import com.maplescot.loggerbill.gpg.CloudSave;
import com.maplescot.loggerbill.gpg.LeaderboardManager;

/**
 * Look after profiles
 * <p/>
 * Created by james on 27/07/14.
 */
public class ProfileManager implements CloudSave.ConflictResolver {
    private static Profile profile;
    private static AchievementManager achievementManager;
    private static LeaderboardManager leaderboardManager;
    private static Preferences prefs;

    public static Profile getProfile() {
        return profile;
    }

    public static AchievementManager getAchievementManager() {
        return achievementManager;
    }

    public static LeaderboardManager getLeaderboardManager() {
        return leaderboardManager;
    }

    public static void init() {
        // initialise these first
        achievementManager = new AchievementManager();
        achievementManager.init();
        leaderboardManager = new LeaderboardManager();
        leaderboardManager.init();

        prefs = Gdx.app.getPreferences("profile");
        profile = new Profile();
        profile.init(prefs);
        // try to restore profile from cloudsave
        CloudSave.getInstance().register(new ProfileManager());
    }

    public static void saveProfile() {
        profile.save(prefs);
        prefs.flush();

        // try to save profile to cloudsave
        CloudSave.getInstance().save(profile.serialize());
    }

    /*
     * Build both profiles and if p1 has more plays than p2
     * return true else return false
     */
    @Override
    public void resolveConflicts(String currentAccountName, byte[] stream1, byte[] stream2) {
        Profile p1 = profile.deserialize(stream1);
        Profile p2 = profile.deserialize(stream2);

        Profile p;
        if (profile.selectConflictedProfile(currentAccountName, p1, p2))
            p = p1;
        else
            p = p2;

        if (profile.selectConflictedProfile(currentAccountName, p, profile))
            profile = p;

        if (currentAccountName != null)
            profile.setGplusId(currentAccountName);
    }
}

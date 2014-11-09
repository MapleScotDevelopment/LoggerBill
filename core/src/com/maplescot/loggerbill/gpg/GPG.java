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
package com.maplescot.loggerbill.gpg;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.maplescot.loggerbill.misc.Assets;
import com.maplescot.loggerbill.ui.AchievementsDialog;
import com.maplescot.loggerbill.ui.StatsDialog;

/**
 * Google Play Game Services wrapper
 * Created by james on 15/07/14.
 */
public class GPG {

    public interface Resolver {
        public boolean getSignedIn();

        public void login();

        public void submitScore(String leaderboardId, long score);

        public void unlockAchievement(String achievementId);

        public boolean canShowLeaderboards();
        public void showLeaderboards(Stage stage);

        public boolean canShowAchievements();
        public void showAchievements(Stage stage);

        public void setAchievementIncrement(String id, long value, long max);
    }

    private static GPG instance;
    private static Resolver actual;

    private GPG() {
    }

    public static GPG getInstance() {
        if (instance == null)
            instance = new GPG();
        return instance;
    }


    public static void init(Resolver actual) {
        GPG.actual = actual;
    }

    public boolean getSignedIn() {
        return actual.getSignedIn();
    }

    public void login() {
        actual.login();
    }

    public void submitScore(String leaderboardId, long score) {
        actual.submitScore(leaderboardId, score);
    }

    public void unlockAchievement(String achievementId) {
        actual.unlockAchievement(achievementId);
    }

    public void showLeaderboards(Stage stage) {
        if (getSignedIn() && actual.canShowLeaderboards())
            actual.showLeaderboards(stage);
        else {
            StatsDialog s = new StatsDialog("Player Statistics", Assets.getInstance().uiSkin, "dialog");
            s.show(stage);
        }
    }

    public void showAchievements(Stage stage) {
        if (getSignedIn() && actual.canShowAchievements())
            actual.showAchievements(stage);
        else {
            AchievementsDialog a = new AchievementsDialog("Achievements", Assets.getInstance().uiSkin, "dialog");
            a.show(stage);
        }
    }

    public void setAchievementIncrement(String id, long value, long max) {
        actual.setAchievementIncrement(id, value, max);
    }
}

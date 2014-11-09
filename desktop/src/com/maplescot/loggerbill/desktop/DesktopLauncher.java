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
package com.maplescot.loggerbill.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.maplescot.loggerbill.LoggerBillGame;
import com.maplescot.loggerbill.gpg.Ads;
import com.maplescot.loggerbill.gpg.CloudSave;
import com.maplescot.loggerbill.gpg.GPG;
import com.maplescot.loggerbill.misc.Constants;

public class DesktopLauncher {
    // To re-build the texutre atlases set 'rebuildAtlas' to true and run once. Then return to false right away,
    // Set drawDebug to true to enable debugging outlines in the texture atlas.
    private static boolean rebuildAtlas = true;
    private static boolean drawDebug = false;
    private static String TAG = DesktopLauncher.class.toString();


    public static void main (String[] arg) {

        if (rebuildAtlas) {
            TexturePacker.Settings settings = new TexturePacker.Settings();
            settings.maxWidth = 1024;
            settings.maxHeight = 2048;
            settings.debug = drawDebug;
            settings.paddingY=5;


            try {
                TexturePacker.process(settings, "../assets-raw/images-ui", "images", "loggerbill-ui");
            } catch (Exception Ignored) {
            }
            try {
                TexturePacker.process(settings, "../assets-raw/skin", "images", "uiskin");
            } catch (Exception Ignored) {
            }


            try {
                TexturePacker.process(settings, "../assets-raw/gameSprites", "images", "game-sprites");
            } catch (Exception Ignored) {
            }
            try {
                TexturePacker.process(settings, "../assets-raw/splashSprites", "images", "splash-sprites");
            } catch (Exception Ignored) {
            }

        }

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Logger Bill";
        config.width = (int) Constants.DESKTOP_GUI_WIDTH;
        config.height = (int) Constants.DESKTOP_GUI_HEIGHT;

        // GooglePlayGames
        GPG.Resolver gpg = new GPG.Resolver() {
            @Override
            public boolean getSignedIn() {
                return false;
            }

            @Override
            public void login() {

            }

            @Override
            public void submitScore(String leaderboardId, long score) {

            }

            @Override
            public void unlockAchievement(String achievementId) {

            }

            @Override
            public boolean canShowLeaderboards() {
                return false;
            }

            @Override
            public void showLeaderboards(Stage stage) {

            }

            @Override
            public boolean canShowAchievements() {
                return false;
            }

            @Override
            public void showAchievements(Stage stage) {
            }

            @Override
            public void setAchievementIncrement(String id, long value, long max) {

            }
        };

        // Ads
        Ads.Resolver ads = new Ads.Resolver() {
            @Override
            public void showBanner(boolean show) {

            }

            @Override
            public void showInterstitial() {

            }

            @Override
            public void endAds() {

            }
        };

        // CloudSave
        CloudSave.Resolver cloudsave = new CloudSave.Resolver() {

            @Override
            public void register(CloudSave.ConflictResolver conflictResolver) {

            }

            @Override
            public void save(byte[] profile) {

            }
        };

		new LwjglApplication(new LoggerBillGame(gpg, ads, cloudsave, null, null, "http://maplescot.itch.io/logger-bill"), config);
	}
}

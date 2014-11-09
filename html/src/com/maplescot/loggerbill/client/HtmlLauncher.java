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
package com.maplescot.loggerbill.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.maplescot.loggerbill.LoggerBillGame;
import com.maplescot.loggerbill.gpg.Ads;
import com.maplescot.loggerbill.gpg.CloudSave;
import com.maplescot.loggerbill.gpg.GPG;

public class HtmlLauncher extends GwtApplication {

    @Override
    public GwtApplicationConfiguration getConfig () {
        return new GwtApplicationConfiguration(540, 960);
    }

    public static native boolean nativeGetSignedIn() /*-{
        return $wnd.login.loggedIn;
    }-*/;

    public static native void nativeLogin() /*-{
        $wnd.login.init();
    }-*/;

    @Override
    public ApplicationListener getApplicationListener () {
            // GooglePlayGames
        GPG.Resolver gpg = new GPG.Resolver() {
            @Override
            public boolean getSignedIn() {
                return nativeGetSignedIn();
            }

            @Override
            public void login() {
                nativeLogin();
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

        return new LoggerBillGame(gpg, ads, cloudsave, null, null, "http://maple.scot/index.php/articles/6-loggerbill-online");
    }
}
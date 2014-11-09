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
package com.maplescot.loggerbill;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.maplescot.loggerbill.gpg.Ads;
import com.maplescot.loggerbill.gpg.CloudSave;
import com.maplescot.loggerbill.gpg.GPG;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

public class IOSLauncher extends IOSApplication.Delegate {
    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
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

        return new IOSApplication(new LoggerBillGame(gpg, ads, cloudsave, null, null, "https://itunes.apple.com/gb/app/timberman/id871809581?mt=8&uo=4"), config);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }
}
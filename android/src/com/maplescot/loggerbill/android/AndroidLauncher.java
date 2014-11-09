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
package com.maplescot.loggerbill.android;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.google.android.gms.ads.*;
import com.google.android.gms.appstate.AppStateManager;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.maplescot.loggerbill.LoggerBillGame;
import com.maplescot.loggerbill.android.basegameutils.BaseGameActivity;
import com.maplescot.loggerbill.gpg.Ads;
import com.maplescot.loggerbill.gpg.CloudSave;
import com.maplescot.loggerbill.gpg.GPG;
import com.maplescot.loggerbill.misc.Emailer;
import com.maplescot.loggerbill.misc.Tweeter;

import java.util.List;

public class AndroidLauncher extends BaseGameActivity implements GPG.Resolver, Ads.Resolver, Tweeter.Resolver, Emailer.Resolver, CloudSave.Resolver {
    // Result code we use with startActivityForResult() when invoking the
    // game UIs like achievements and leaderboard
    private static final int RC_UNUSED = 11999;
    private static final String AD_UNIT_ID_BANNER = "ca-app-pub-blah/blah";
    private static final String AD_UNIT_ID_INTERSTITIAL = "ca-app-pub-blah2/blah2";

    private static final String TAG = AndroidLauncher.class.toString();
    private AdView adView;
    private InterstitialAd interstitial;
    private FrameLayout layout;
    private AdRequest adRequest;
    private int STATE_KEY=0;
    private CloudSave.ConflictResolver conflictResolver;

    public AndroidLauncher() {
        super(BaseGameActivity.CLIENT_APPSTATE|BaseGameActivity.CLIENT_GAMES);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        View gameView = initializeForView(new LoggerBillGame(this, this, this, this, this, getString(R.string.app_url)), config);
        // Create an ad.
        adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(AD_UNIT_ID_BANNER);

        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(AD_UNIT_ID_INTERSTITIAL);
        adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        adView.loadAd(adRequest);

        layout = new FrameLayout(this);
        layout.addView(gameView);

        FrameLayout.LayoutParams adParams =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
        adParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        adView.setVisibility(View.INVISIBLE);
        layout.addView(adView, adParams);

        Log.d("Advertising", "You get an interstitial ad this time...");
        interstitial.loadAd(adRequest);

        setContentView(layout);
    }

    @Override
    public boolean getSignedIn() {
        return getGameHelper().isSignedIn();
    }

    @Override
    public void login() {
        try {
            runOnUiThread(new Runnable() {
                public void run() {
                    getGameHelper().beginUserInitiatedSignIn();
                }
            });
        } catch (final Exception ex) {
        }
    }

    @Override
    public void submitScore(String leaderboardId, long score) {
        try {
            if (isSignedIn())
                Games.Leaderboards.submitScore(getApiClient(), leaderboardId, score);
        } catch (Exception e) {
            Log.e(TAG, "Failed to submit score", e);
            mHelper.getApiClient().disconnect();
        }
    }

    @Override
    public void unlockAchievement(String achievementId) {
        try {
            if (isSignedIn())
                Games.Achievements.unlockImmediate(getApiClient(), achievementId);
        } catch (Exception e) {
            Log.e(TAG, "Failed to unlock achievement", e);
            mHelper.getApiClient().disconnect();
        }
    }

    @Override
    public void setAchievementIncrement(String id, long value, long max) {
        try {
            if (isSignedIn())
                Games.Achievements.setSteps(getApiClient(), id, (int) value);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set achievement steps", e);
            mHelper.getApiClient().disconnect();
        }
    }

    @Override
    public boolean canShowLeaderboards() {
        return true;
    }

    @Override
    public void showLeaderboards(Stage stage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isSignedIn()) {
                    try {
                        startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(getApiClient()), RC_UNUSED);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to show leaderboards", e);
                        mHelper.getApiClient().disconnect();
                    }
                } else {
                    beginUserInitiatedSignIn();
                }
            }
        });
    }

    @Override
    public boolean canShowAchievements() {
        return true;
    }

    @Override
    public void showAchievements(Stage stage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isSignedIn()) {
                    try {
                        startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()), RC_UNUSED);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to show achievements", e);
                        mHelper.getApiClient().disconnect();
                    }
                } else {
                    beginUserInitiatedSignIn();
                }
            }
        });
    }

    @Override
    public void onSignInFailed() {

    }

    @Override
    public void onSignInSucceeded() {
        if (conflictResolver != null) {
            loadProfile();
        }
   }

    private void loadProfile() {
        AppStateManager.load(getApiClient(), STATE_KEY).setResultCallback(new ResultCallback<AppStateManager.StateResult>() {
            @Override
            public void onResult(AppStateManager.StateResult stateResult) {
                AppStateManager.StateConflictResult conflictResult = stateResult.getConflictResult();
                AppStateManager.StateLoadedResult loadedResult = stateResult.getLoadedResult();
                if (loadedResult != null)
                    conflictResolver.resolveConflicts(Games.getCurrentAccountName(getApiClient()), loadedResult.getLocalData(), null);
                else if (conflictResult != null)
                    conflictResolver.resolveConflicts(Games.getCurrentAccountName(getApiClient()), conflictResult.getServerData(), conflictResult.getLocalData());
            }
        });
    }

    @Override
    public void register(final CloudSave.ConflictResolver conflictResolver) {
        this.conflictResolver = conflictResolver;
        if (isSignedIn()) {
            loadProfile();
        }
    }

    @Override
    public void save(byte[] profile) {
        if (isSignedIn())
            AppStateManager.update(getApiClient(), STATE_KEY, profile);
    }

    @Override
    public void showBanner(final boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (show)
                    adView.setVisibility(View.VISIBLE);
                else
                    adView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void showInterstitial() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (interstitial.isLoaded()) interstitial.show();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                interstitial.loadAd(adRequest);
            }
        });
    }

    @Override
    public void endAds() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adView.destroy();
            }
        });
    }

    @Override
    public void postTweet(String tweet) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweet));

        // Narrow down to official Twitter app, if available:
        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                intent.setPackage(info.activityInfo.packageName);
            }
        }
        startActivityForResult(intent, 1);
    }

    @Override
    public void postEmail(String to, String subject, String body) {
        Intent share = new Intent(Intent.ACTION_VIEW);
        share.setType("message/rfc822");
        share.setAction(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_EMAIL, to.split(","));
        share.putExtra(Intent.EXTRA_SUBJECT, subject);
        share.putExtra(Intent.EXTRA_TEXT, body);
        startActivityForResult(share, 4);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == RC_UNUSED
                && resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
            mHelper.getApiClient().disconnect();
            // update your logic here (show login btn, hide logout btn).
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

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

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.maplescot.loggerbill.gpg.Ads;
import com.maplescot.loggerbill.gpg.CloudSave;
import com.maplescot.loggerbill.gpg.GPG;
import com.maplescot.loggerbill.misc.*;
import com.maplescot.loggerbill.ui.SplashScreen;

public class LoggerBillGame extends Game {

    public LoggerBillGame(GPG.Resolver gpgResolver, Ads.Resolver adsResolver, CloudSave.Resolver cloudResolver,
                          Tweeter.Resolver tweetResolver, Emailer.Resolver emailerResolver, String app_specific_url) {
        GPG.init(gpgResolver);
        Ads.init(adsResolver);
        CloudSave.init(cloudResolver);
        Tweeter.init(tweetResolver);
        Emailer.init(emailerResolver);
        Constants.app_specific_url = app_specific_url;
    }

	@Override
	public void create () {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        ProfileManager.init();

        Assets.getInstance().splashScreen = new SplashScreen(this);
        setScreen(Assets.getInstance().splashScreen);
	}

	@Override
	public void render () {
        super.render();
	}

    @Override
    public void dispose() {
        ProfileManager.saveProfile();
        super.dispose();


    }
}

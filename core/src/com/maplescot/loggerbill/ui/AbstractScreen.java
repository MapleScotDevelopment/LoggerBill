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
package com.maplescot.loggerbill.ui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.maplescot.loggerbill.misc.Assets;
import com.maplescot.loggerbill.misc.ProfileManager;

/**
 * There are some common @overrides that need to be done for every screen. This class just wraps them for
 * us for neatness
 *
 * Created by troy on 13/09/14.
 */
public abstract class AbstractScreen implements Screen {
    private static final String TAG = AbstractScreen.class.toString();

    protected Game game;
    protected Skin skin;
    protected Skin uiSkin;

    public AbstractScreen(Game game) {
        skin = Assets.getInstance().skin;
        uiSkin = Assets.getInstance().uiSkin;
        this.game = game;
    }

    @Override
    public void resize(int width, int height) {
        Assets.getInstance().playMusic(ProfileManager.getProfile().isMusicOn());
    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        Assets.getInstance().playMusic(ProfileManager.getProfile().isMusicOn());
        while (!Assets.getInstance().updateLoading()) {
            Gdx.app.debug(TAG, "Reloading assets");
        }
        ; // Reload assets if necessary
    }

    @Override
    public void dispose() {

    }

}

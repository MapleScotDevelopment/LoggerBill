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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.maplescot.loggerbill.misc.Assets;
import com.maplescot.loggerbill.misc.Constants;
import com.maplescot.loggerbill.misc.ProfileManager;

import static com.maplescot.loggerbill.misc.Constants.VIEWPORT_GUI_WIDTH;
import static com.maplescot.loggerbill.misc.Constants.getViewportHeight;

/**
 * This 'Splash screen' shows a small animation to distract the player while the assets for the game are loading.
 * Loading the assets ahead of time prevents noticeable pauses during the game when they are fetched.
 * <p/>
 * Created by troy on 10/09/14.
 */
public class SplashScreen extends AbstractScreen {
    private static final String TAG = SplashScreen.class.toString();
    private OrthographicCamera camera;
    private Animation morphAnimation;
    private SpriteBatch batch;
    private TextureRegion nameRegion;
    private TextureAtlas atlas;
    private float stateTime = 0f;
    private float prePause = 1.0f;
    private float alpha = 0f;
    private boolean assetsLoaded = false;


    public SplashScreen(Game game) {
        super(game);
    }

    @Override
    public void render(float delta) {

        if (prePause <= 0) {
            stateTime += delta;
            if (assetsLoaded && stateTime >= 2.0) {
                Assets.getInstance().playMusic(ProfileManager.getProfile().isMusicOn());
                Assets.getInstance().mainMenu = new MainMenu(game);
                game.setScreen(Assets.getInstance().mainMenu);

            }
            if (stateTime >= 0.5f && alpha < 1.0) alpha += delta;
        } else prePause -= delta;

        if (!assetsLoaded) assetsLoaded = Assets.getInstance().updateLoading();

        TextureRegion reg = morphAnimation.getKeyFrame(stateTime, false);

        Gdx.gl.glClearColor(0.036f, 0.058f, 0.0988f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (alpha > 1) alpha = 1;
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(reg, (VIEWPORT_GUI_WIDTH / 4) - (reg.getRegionWidth() / 2), 100);
        batch.setColor(1f, 1f, 1f, alpha);
        batch.draw(nameRegion, (VIEWPORT_GUI_WIDTH / 4) - (nameRegion.getRegionWidth() / 2), 300);
        batch.end();
    }

    @Override
    public void show() {

        Gdx.app.debug(TAG, "STARTING");
        camera = new OrthographicCamera();
        camera.position.set(0, 0, 0);
        camera.setToOrtho(true, VIEWPORT_GUI_WIDTH / 2, getViewportHeight() / 2);
        camera.update();

        batch = new SpriteBatch();
        batch.enableBlending();

        atlas = new TextureAtlas(Constants.TEXTURE_ATLAS_SPLASH);
        Array<TextureAtlas.AtlasRegion> morphRegions = atlas.findRegions("morph");
        for (TextureRegion r : morphRegions) r.flip(false, true);
        morphAnimation = new Animation(10.0f / 60.0f, morphRegions, Animation.PlayMode.NORMAL);

        nameRegion = atlas.findRegion("name2");
        nameRegion.flip(false, true);
        Gdx.app.debug(TAG, "Queuing Asset Load");
        Assets.getInstance().load(); // Start loading...
        Gdx.app.debug(TAG, "Asset Load queued");
    }

    @Override
    public void dispose() {
        atlas.dispose();
        batch.dispose();
    }

}

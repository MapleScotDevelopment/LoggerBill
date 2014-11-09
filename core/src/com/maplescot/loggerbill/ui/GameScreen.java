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
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.maplescot.loggerbill.game.GameEngine;
import com.maplescot.loggerbill.game.world.GameRenderer;
import com.maplescot.loggerbill.misc.Assets;
import com.maplescot.loggerbill.misc.ProfileManager;

import static com.maplescot.loggerbill.misc.Constants.*;

/**
 * This is the main Play screen.
 * <p/>
 * Created by troy on 21/07/14.
 */
public class GameScreen extends AbstractScreen {

    private static String TAG = GameScreen.class.toString();

    private GameEngine engine;
    private GameRenderer renderer;

    private OrthographicCamera camera;
    private OrthographicCamera hudCamera; // We'll use a different camera for the hud.
    private SpriteBatch batch;
    private FPSLogger fps = new FPSLogger();


    /**
     * For cases when a single engine implements both GameEngine and GameRenderer
     *
     * @param game   A reference to the game class
     * @param engine a reference to the Game engine and renderer class
     */
    public GameScreen(Game game, GameEngine engine) {
        super(game);
        this.engine = engine;
        this.renderer = (GameRenderer) engine;
    }

    public GameScreen(Game game, GameEngine engine, GameRenderer renderer) {
        super(game);
        this.engine = engine;
        this.renderer = renderer;
    }


    /**
     * This is called when our game screen is shown. We will initialize the game here.
     */
    @Override
    public void show() {
        Gdx.app.debug(TAG, "Initializing game");

        camera = new OrthographicCamera(VIEW_WIDTH, VIEW_HEIGHT);
        camera.position.set(0, VIEW_HEIGHT / 2, 0f);
        camera.update();

        hudCamera = new OrthographicCamera();
        hudCamera.position.set(0, 0, 0);
        hudCamera.setToOrtho(true, VIEWPORT_GUI_WIDTH, getViewportHeight());
        hudCamera.update();


        batch = new SpriteBatch();
        batch.enableBlending();

        engine.init();
        engine.reset();

        Gdx.input.setInputProcessor(engine.getInputProcessor()); // No. 5 need input.... Innnnppuuuuut. (This class will read touch events)
        Gdx.input.setCatchBackKey(true);

        Assets.getInstance().playMusic(ProfileManager.getProfile().isMusicOn());
    }


    /**
     * The meat of the matter... This is the main game loop. We first draw eveything in order of furthest objects to
     * closer ones. Then after we've drawn stuff we check to see if Bill has died.
     *
     * @param delta Time between frames for sync
     */
    @Override
    public void render(float delta) {


        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        renderer.drawWorld(batch, delta);
        batch.end();


        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        renderer.drawHUD(batch, delta);
        batch.end();

        if (!engine.run(delta)) {
            Gdx.app.debug(TAG, "Returning to menu.");
            game.setScreen(Assets.getInstance().mainMenu);
        }


        fps.log(); // log framerate

    }


    @Override
    public void pause() {
        Gdx.app.log(TAG, "Pause");
        engine.setPause(true);
    }


    @Override
    public void dispose() {
        batch.dispose();
    }






}

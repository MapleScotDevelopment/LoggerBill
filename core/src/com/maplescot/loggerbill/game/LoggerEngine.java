/*
 *
 *  * (C) Copyright 2014 MapleScot Development
 *  * This file licensed under a Creative Commons 3.0 by attribution licence
 *  * https://creativecommons.org/licenses/by/3.0/
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *  * https://github.com/duriej/LoggerBill
 *
 */

package com.maplescot.loggerbill.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.maplescot.loggerbill.game.world.*;
import com.maplescot.loggerbill.gpg.Ads;
import com.maplescot.loggerbill.misc.Assets;
import com.maplescot.loggerbill.misc.ProfileManager;
import com.maplescot.loggerbill.ui.PausedDialog;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

import static com.maplescot.loggerbill.misc.Constants.*;

/**
 * This is where the guts of the game go.
 * <p/>
 * Created by troy on 20/09/14.
 */
public class LoggerEngine implements GameEngine, GameRenderer, InputProcessor {

    private static String TAG = LoggerEngine.class.toString();

    private ShaderProgram ghostShader;
    private ShaderProgram nightShader;
    private ParticleEffect chips;
    private PausedDialog pausedDialog;
    private FireFlies fireFlies_front;
    private FireFlies fireFlies_back;

    private static Random rnum = new Random();
    private static boolean isNight = false;
    private static int dayCounter = 0;

    private Bill bill = new Bill();
    private boolean showHelp = true;
    private boolean isPaused = false;
    private boolean playing = true;
    private float offset = 0; // add a bit of animation to or fingers...
    // A tree consists of.... chunkTypes.
    //  ---> How many chunks could a woodchuck chuck, if a woodchuck could chuck chunks?
    private LinkedList<Chunk> tree = new LinkedList<Chunk>();
    private LinkedList<EjectedChunk> ejectedChunks = new LinkedList<EjectedChunk>();
    private float fall_dist = 0; // To animate the tree falling down to replace a chunk.
    private boolean billAlive = true;
    private BillGhost billGhost = null;
    private float totalTime = 0f;
    private float idleTime = 0.5f; // Idle time is a percentage... 100%. This is decremented each frame... if it gets to zero bill dies.
    private float speed;
    private int chunk_counter = 0;

    private static float width;


    private float xMul, yMul; // Multipliers used to transform real screen coords to virtual


    @Override
    public void init() {
        BackgroundScenery.getInstance().init();


        Gdx.app.debug(TAG, "Compiling Shaders");

        ghostShader = new ShaderProgram(Gdx.files.internal(VERTEX_SHADER), Gdx.files.internal(FRAG_SHADER));
        if (!ghostShader.isCompiled()) {
            String msg = "Could not compile shader program: " + ghostShader.getLog();
            throw new GdxRuntimeException(msg);
        }

        nightShader = new ShaderProgram(Gdx.files.internal(NIGHT_VERTEX_SHADER), Gdx.files.internal(NIGHT_FRAG_SHADER));

        if (!nightShader.isCompiled()) {
            String msg = "Could not compile shader program: " + nightShader.getLog();
            throw new GdxRuntimeException(msg);
        }

        chips = new ParticleEffect();
        chips.load(Gdx.files.internal("particles/chips.pfx"), Gdx.files.internal("particles"));
        pausedDialog = new PausedDialog(this);

        // Calculate multiplier for relative screen coords.
        xMul = VIEWPORT_GUI_WIDTH / Gdx.graphics.getWidth();
        yMul = getViewportHeight() / Gdx.graphics.getHeight();
    }

    @Override
    public InputProcessor getInputProcessor() {
        return this;
    }

    @Override
    public void reset() {
        dayCounter++;
        if (dayCounter > 3) { // Every 3 plays change from day to night.
            isNight = !isNight;
            dayCounter = 0;
        }

        playing = true;
        if (isNight) {
            fireFlies_front = new FireFlies(10, 1);
            fireFlies_back = new FireFlies(10, 195);
        } else {
            fireFlies_front = null;
            fireFlies_back = null;
        }


        BackgroundScenery.getInstance().setNight(isNight);
        BackgroundScenery.getInstance().setNightShader(nightShader);
        billAlive = true;
        bill.setDead(false);
        showHelp = true;

        //billGhost = null;
        // Reset score
        idleTime = 0.5f;
        totalTime = 0f;
        chunk_counter = 0;
        speed = START_SPEED;
        bill.setSide(LEFT);
        bill.setChopping(false);

        tree.clear();
        buildTree();

        // trigger loading a new ad for those ad networks that need it
        Ads.getInstance().showBanner(true);

    }

    @Override
    public boolean run(float delta) {
        if (fall_dist > 0) fall_dist -= CHUNK_SIZE * (delta * 10);
        else fall_dist = 0;

        if (isPaused && pausedDialog != null) pausedDialog.render(delta);
        else {
            checkForDead();
            if (billAlive && !showHelp) {
                idleTime -= delta * speed;
                totalTime += delta;
            }
        }

        return playing;
    }

    @Override
    public void drawWorld(SpriteBatch batch, float delta) {
        if (isNight) {
            // Night sky.
            Gdx.gl.glClearColor(0.036f, 0.058f, 0.0988f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        } else {
            // Colour my world blue.
            Gdx.gl.glClearColor(0.36f, 0.58f, 0.988f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }
        BackgroundScenery.getInstance().draw(delta);


        // The Grass...
        setShader(batch);
        batch.setColor(0f, 1f, 0f, 1f); // Green Grass
        batch.draw(Assets.getInstance().grassRegion, -(VIEW_WIDTH / 2) - 50, 0f, VIEW_WIDTH + 100, BILL_HEIGHT + 20);

        if (isNight) {
            batch.setShader(null);
            fireFlies_back.draw(batch, delta);
        }

        setShader(batch);
        // The stump...
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(Assets.getInstance().stumpRegion, -(Assets.getInstance().stumpRegion.getRegionWidth() / 2) - 7, 40f);


        drawTree(batch); // Draw the actual tree

        ListIterator<EjectedChunk> ejectIterator = ejectedChunks.listIterator();
        while (ejectIterator.hasNext()) {
            EjectedChunk myChunk = ejectIterator.next();
            if (!myChunk.draw(batch, delta)) ejectIterator.remove(); // Clean up ejected chunks when off screen.
        }

        bill.draw(batch, delta);

        if (billGhost != null) {
            billGhost.draw(batch, ghostShader, delta);
        }

        chips.draw(batch);
        if (!chips.isComplete()) chips.update(delta);

        if (isNight) {
            batch.setShader(null);
            fireFlies_front.draw(batch, delta);
        }


    }

    @Override
    public void drawHUD(SpriteBatch batch, float delta) {
        batch.setShader(null); // No darkening of the HUD at night
        String str = String.valueOf(chunk_counter);
        BitmapFont.TextBounds tb = Assets.getInstance().font.big.getBounds(str);
        Assets.getInstance().font.big.setColor(0f, 1f, 0f, 1f);
        Assets.getInstance().font.big.draw(batch, str, VIEWPORT_GUI_WIDTH / 2 - (tb.width / 2), 250);

        Assets.getInstance().pauseButtonDrawable.draw(batch, VIEWPORT_GUI_WIDTH - 150, 135, 100, 100);
        drawTimerGuage(batch);
        if (showHelp) drawHelp(batch, delta);
    }

    @Override
    public void setPause(boolean paused) {
        isPaused = paused;
        if (paused) showPause(false); // No animation.

    }

    @Override
    public boolean isPaused() {
        return isPaused;
    }

    public void setShader(SpriteBatch batch) {
        if (isNight) {
            batch.setShader(nightShader);
            nightShader.setUniformf("u_amount", 0.85f);
        } else batch.setShader(null);
    }

    public void endGame() {
        Gdx.app.debug(TAG, "Ending game.");
        playing = false;
    }

    /**
     * This method builds the initial tree.
     */
    private void buildTree() {

        // Start out with at least 2 CLEAR chunks...
        tree.add(new Chunk(Chunk.Type.CLEAR));
        tree.add(new Chunk(Chunk.Type.CLEAR));

        for (int i = 2; i < CHUNK_COUNT; i++) {
            addChunk();
        }

    }

    /**
     * Add a chunk to the tree. The rules are simple. We have three types of chunks - LEFT, RIGHT, or CLEAR. This
     * indicates the side that the branch is on (or no branch in the case of CLEAR). Every second chunk at minimum
     * must be clear - i.e. you cannot have two branches following each other. However after a clear section you can
     * have any of the three. Simples?
     */
    private void addChunk() {
        Chunk lastChunk = tree.getLast();
        if (lastChunk.getType() == Chunk.Type.CLEAR) {
            int r = rnum.nextInt(5);
            // give a slight bias to having a branch over not having a branch
            if (r == 0) tree.add(new Chunk(Chunk.Type.CLEAR));
            else if (r % 2 == 0) tree.add(new Chunk(Chunk.Type.RIGHT));
            else tree.add(new Chunk(Chunk.Type.LEFT));
        } else tree.add(new Chunk(Chunk.Type.CLEAR));

    }

    private void drawHelp(SpriteBatch batch, float delta) {

        offset += delta;
        if (offset >= 360) offset = 0;

        batch.draw(Assets.getInstance().tapIcon_left, ((VIEWPORT_GUI_WIDTH / 5) - 50), (float) ((getViewportHeight() - 300) + (Math.sin(offset) * 25)), 100, 100);
        batch.draw(Assets.getInstance().tapIcon_right, ((VIEWPORT_GUI_WIDTH / 5) * 4) - 50, (float) ((getViewportHeight() - 300) - (Math.sin(offset) * 25)), 100, 100);

        Assets.getInstance().font.normal.setColor(1f, 0f, 0f, 1f);

        String helpText;
        if (Gdx.app.getType() == Application.ApplicationType.Desktop ||
                Gdx.app.getType() == Application.ApplicationType.WebGL ||
                Gdx.app.getType() == Application.ApplicationType.Applet) helpText = "Press";
        else helpText = "Tap";

        Assets.getInstance().font.normal.draw(batch, helpText, ((VIEWPORT_GUI_WIDTH / 5) - 35), (getViewportHeight() - 200));
        Assets.getInstance().font.normal.draw(batch, helpText, (((VIEWPORT_GUI_WIDTH / 5) * 4) - 30), (getViewportHeight() - 200));

    }

    private void drawTimerGuage(SpriteBatch batch) {
        String str = "";
        batch.draw(Assets.getInstance().guage_empty, VIEWPORT_GUI_WIDTH / 2 - (Assets.getInstance().guage_empty.getRegionWidth() / 2), 150f);
        // the bezel around the fuel guage is 6 pixels... so I need to adjust some dimensions by 7 and 12
        float w = ((Assets.getInstance().guage_empty.getRegionWidth() - 12) * idleTime);
        batch.draw(Assets.getInstance().guage_full, VIEWPORT_GUI_WIDTH / 2 - (Assets.getInstance().guage_empty.getRegionWidth() / 2) + 6, 158f, w, Assets.getInstance().guage_full.getRegionHeight());

        if (idleTime < 0.24f && idleTime > 0) {
            str = "Chop Faster!";
            Assets.getInstance().font.normal.setColor(1f, 1f, 0f, 1f);
        } else if (idleTime <= 0.0) {
            str = "Too Slow!";
            Assets.getInstance().font.normal.setColor(1f, 0f, 0f, 1f);
        }
        BitmapFont.TextBounds tb = Assets.getInstance().font.normal.getBounds(str);
        Assets.getInstance().font.normal.draw(batch, str, (VIEWPORT_GUI_WIDTH / 2) - (tb.width / 2), 156 + tb.height / 2);
    }


    /**
     * Helper function for drawing the tree.
     *
     * @param batch The sprite batch to use for improving performance
     */
    public void drawTree(SpriteBatch batch) {
        Sprite mySprite;
        float yPos = 192 - (CHUNK_SIZE / 2) + fall_dist;
        for (Chunk thisChunk : tree) {
            mySprite = thisChunk.getSprite();

            mySprite.setX(-CHUNK_SIZE / 2 - (thisChunk.getType() == Chunk.Type.LEFT ? CHUNK_SIZE : 0));
            mySprite.setY(yPos);
            mySprite.draw(batch);
            yPos += CHUNK_SIZE - 1;
        }
    }


    /**
     * Check conditions for bill's possible demise..
     */
    private void checkForDead() {
        if (!billAlive) return;
        if (fall_dist == 0 &&
                tree.getFirst().getType() == Chunk.Type.LEFT && bill.getSide() == LEFT ||
                tree.getFirst().getType() == Chunk.Type.RIGHT && bill.getSide() == RIGHT)
            killBill();

        if (billAlive && idleTime <= 0) {
            Gdx.app.log(TAG, "Too slow");
            killBill();
        }

    }

    /**
     * Game over...
     */
    private void killBill() {
        billAlive = false; // He's dead Jim
        Gdx.input.vibrate(500);
        bill.setDead(true);
        billGhost = new BillGhost(bill.getSide());
        if (ProfileManager.getProfile().getTotalPlays() % 5 == 0)
            Ads.getInstance().showInterstitial();
        showPause(true);
    }

    /**
     * Spew one the top chunk of the tree off to one side, and increment chunk counter.
     *
     * @param direction Direction to fly off -1 = left, 1 = right.
     */
    private void eject(int direction) {
        if (!billAlive) return; //Dead men chop no trees.
        if (showHelp) showHelp = false; // Turn off help
        bill.chop();
        Gdx.input.vibrate(50);
        if (ProfileManager.getProfile().isSoundOn()) Assets.getInstance().thwack.play();
        ejectedChunks.add(new EjectedChunk(tree.getFirst().getSprite(), direction));
        tree.removeFirst();
        addChunk();
        chunk_counter++;
        if (chunk_counter % LEVEL_THRESHOLD == 0) {
            speed += SPEED_INCREASE;
        }
        idleTime += RECHARGE_RATE;
        if (idleTime > 1) idleTime = 1;
        fall_dist = CHUNK_SIZE;
        chips.findEmitter("Chips").getVelocity().setHigh(150 * bill.getSide(), 300 * bill.getSide());
        chips.setPosition(bill.getSide() * CHUNK_SIZE / 2, BILL_HEIGHT + 45 + CHUNK_SIZE / 2);
        chips.start();
    }

    private void moveBill(int side) {
        if (side == bill.getSide() || !billAlive)
            return; // Don't do anything if we're already on this side.. or if we're dead.... Jim
        bill.setSide(side);
        checkForDead();
    }

    private void showPause(boolean anim) {

        Gdx.app.debug(TAG, "showing pause dialog");
        isPaused = true;
        pausedDialog.show(chunk_counter, totalTime, anim);
        if (!billAlive) ProfileManager.getProfile().addPlay(chunk_counter, totalTime);
    }


    @Override
    public boolean isAlive() {
        return billAlive;
    }


    @Override
    public void dispose() {
        Gdx.app.debug(TAG, "Disposing resources");
        pausedDialog.dispose();
    }


    // ------ Input Processor stuff
    @Override
    public boolean keyDown(int keycode) {
        if (!billAlive) return false;
        if (keycode == Input.Keys.RIGHT) {
            moveBill(RIGHT);
            eject(LEFT);
        }
        if (keycode == Input.Keys.LEFT) {
            moveBill(LEFT);
            eject(RIGHT);
        }
        if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE)
            showPause(true);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        // Noy used
        return false;
    }

    /**
     * This is not American football... But we will celebrate touchdown events with some chopping action.
     *
     * @param screenX X location of touch in real pixels
     * @param screenY X location of touch in real pixels
     * @param pointer only relevant for multitouch
     * @param button  button pressed
     * @return false
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        // Check for hit of the pause button.

        float vx = screenX * xMul;
        float vy = screenY * yMul;
        Gdx.app.debug(TAG, "x: " + vx + "  Y: " + vy);
        //VIEWPORT_GUI_WIDTH - 150, 135, 100, 100
        if (vx > (VIEWPORT_GUI_WIDTH - 200) && vx < VIEWPORT_GUI_WIDTH &&
                vy > 135 && vy < 235) {
            showPause(true);
            return false;
        }
        if (!billAlive) return false;
        if (screenX < Gdx.graphics.getWidth() / 2) {
            moveBill(LEFT);
            eject(RIGHT);
        } else {
            moveBill(RIGHT);
            eject(LEFT);
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // Not used
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // Not used
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        // Not used
        return false;
    }
}

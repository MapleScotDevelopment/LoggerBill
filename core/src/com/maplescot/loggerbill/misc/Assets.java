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
package com.maplescot.loggerbill.misc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.maplescot.loggerbill.game.world.Chunk;

import java.util.Random;

import static com.maplescot.loggerbill.misc.Constants.MUSIC;

/**
 * This singleton class wraps our asset manager. It does background loading of the various assets we need in our game
 * and serves them up statically on demand.
 * <p/>
 * Created by james on 19/07/14.
 */
public class Assets implements Disposable, AssetErrorListener {

    private static final String TAG = Assets.class.getName();
    private static final Assets myInstance = new Assets(); // Singleton
    private static final int SPRITE_COUNT = 2;

    private Sprite clearSprite[] = new Sprite[SPRITE_COUNT];
    private Sprite rightBranchSprite[] = new Sprite[SPRITE_COUNT];
    private Sprite leftBranchSprite[] = new Sprite[SPRITE_COUNT];
    private static Random rnum = new Random();
    public TextureRegion billDeadRegion;
    public TextureRegion tombstoneRegion;
    public TextureRegion stumpRegion;
    public TextureRegion grassRegion;
    public TextureRegion fireFly;
    public TextureRegion moon;
    public TextureRegionDrawable hillsDrawable;
    public TextureRegionDrawable pauseButtonDrawable;
    public TextureRegion tapIcon_left;
    public TextureRegion tapIcon_right;
    public TextureRegion biPlaneRegion;
    public TextureRegion guage_full;
    public TextureRegion guage_empty;
    public AssetFonts font;
    public Animation birdAnimation;
    public Animation billAnimation;
    public Skin skin;
    public Skin uiSkin;
    public Clouds clouds;
    public Sound thwack;
    private AssetManager assetManager;
    private boolean initialized = false;

    public static Assets getInstance() {
        return myInstance;
    }


    /**
     * Initialize the loading of our assets. Actual loading will be done incrementally in the update method;
     */
    public void load() {
        assetManager = new AssetManager();
        assetManager.setErrorListener(this);
        initialized = false;

        Gdx.app.debug(TAG, "Loading Texture atlases");
        assetManager.load(Constants.TEXTURE_ATLAS_GAME, TextureAtlas.class);
        Gdx.app.debug(TAG, "Loading Music/Sounds");
        assetManager.load(MUSIC, Music.class);
        assetManager.load(Constants.THWACK, Sound.class);
        Gdx.app.debug(TAG, "Loading UI skins");
        assetManager.load(Constants.MAIN_STD_UI, Skin.class, new SkinLoader.SkinParameter(Constants.TEXTURE_ATLAS_STD_UI));
        assetManager.load(Constants.MAIN_UI, Skin.class, new SkinLoader.SkinParameter(Constants.TEXTURE_ATLAS_UI));


    }

    /**
     * Incrementally load a bit of the textures... and return back.
     */
    public boolean updateLoading() {
        boolean finished = assetManager.update();
        if (finished) {
            Gdx.app.log(TAG, assetManager.getAssetNames().size + " Assets loaded.");
            if (!initialized) initTextures();
        }

        return finished;
    }


    /**
     * Get some pointer references to our loaded assets and initialize sprites.
     */
    public void initTextures() {

        Gdx.app.log(TAG, "Preparing sprites.");


        TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_GAME);
        // enable texture filtering for pixel smoothing
        for (Texture t : atlas.getTextures()) {
            t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }

        // Get pointers to our textures
        hillsDrawable = new TextureRegionDrawable(atlas.findRegion("hills"));
        stumpRegion = new TextureRegion(atlas.findRegion("stump"));
        grassRegion = new TextureRegion(atlas.findRegion("grass"));
        fireFly = new TextureRegion(atlas.findRegion("firefly"));
        moon = new TextureRegion(atlas.findRegion("moon"));
        pauseButtonDrawable = new TextureRegionDrawable(atlas.findRegion("pause"));
        billDeadRegion = new TextureRegion(atlas.findRegion("dead_bill"));
        tombstoneRegion = new TextureRegion(atlas.findRegion("tombstone"));
        biPlaneRegion = new TextureRegion(atlas.findRegion("biplane"));
        guage_full = new TextureRegion(atlas.findRegion("guage_full"));
        guage_empty = new TextureRegion(atlas.findRegion("guage_empty"));
        tapIcon_left = new TextureRegion(atlas.findRegion("tap"));
        tapIcon_right = new TextureRegion(atlas.findRegion("tap"));
        tapIcon_left.flip(true, true);
        tapIcon_right.flip(false, true);

        clouds = new Clouds(atlas);
        font = new AssetFonts();

        Array<TextureAtlas.AtlasRegion> birdRegions = atlas.findRegions("bird");
        birdAnimation = new Animation(0.15f, birdRegions, Animation.PlayMode.LOOP);

        Array<TextureAtlas.AtlasRegion> billRegions = atlas.findRegions("bill");
        billAnimation = new Animation(Constants.BILL_FRAME_TIME, billRegions, Animation.PlayMode.LOOP_PINGPONG);

        // Initialize our sprites.
        clearSprite[0] = new Sprite(new TextureRegion(atlas.findRegion("chunk_clear1")));
        clearSprite[1] = new Sprite(new TextureRegion(atlas.findRegion("chunk_clear2")));

        rightBranchSprite[0] = new Sprite(new TextureRegion(atlas.findRegion("chunk_branch1")));
        rightBranchSprite[1] = new Sprite(new TextureRegion(atlas.findRegion("chunk_branch2")));
        leftBranchSprite[0] = new Sprite(rightBranchSprite[0]);
        leftBranchSprite[1] = new Sprite(rightBranchSprite[1]);

        // We calculate the origin like this so we can use the same origin on a branch chunk. If we just used
        // LibGDX's originCenter it would get the wrong position on the branch image.
        float xOrigin = clearSprite[0].getWidth() / 2; // Sprites must be the same size
        float yOrigin = clearSprite[0].getHeight() / 2;
        for (int i = 0; i < SPRITE_COUNT; i++) {
            clearSprite[i].setSize(Constants.CHUNK_SIZE, Constants.CHUNK_SIZE);
            rightBranchSprite[i].setSize(Constants.CHUNK_SIZE * 2, Constants.CHUNK_SIZE);
            clearSprite[i].setOrigin(xOrigin, yOrigin);
            rightBranchSprite[i].setOrigin(xOrigin, yOrigin);
            leftBranchSprite[i].setSize(Constants.CHUNK_SIZE * 2, Constants.CHUNK_SIZE);
            leftBranchSprite[i].flip(true, false); // Left Branch is just the right branch flipped.
            leftBranchSprite[i].setOrigin(rightBranchSprite[i].getWidth() - xOrigin, yOrigin - 10);
        }

        skin = assetManager.get(Constants.MAIN_UI, Skin.class);
        uiSkin = assetManager.get(Constants.MAIN_STD_UI, Skin.class);

        thwack =  assetManager.get(Constants.THWACK, Sound.class);

        initialized = true;

    }


    /**
     * Play that funky music, white boy.
     *
     * @param play True to play, else stop.
     */
    public void playMusic(boolean play) {
        if (!initialized || !assetManager.isLoaded(Constants.MUSIC))
            return; // Don't do anything if initialization isn't complete
        Music music = assetManager.get(MUSIC, Music.class);
        if (play && !music.isPlaying()) {
            music.setLooping(true);
            music.setVolume(0.85f);
            music.play();
            Gdx.app.log(TAG, "Music is playing");
        } else if (music.isPlaying() && !play) {
            Gdx.app.log(TAG, "Stopping music");
            music.pause();
        }
    }


    /**
     * dir -1 = left, 0 = clear, 1 = right
     *
     * @param dir direction of branch
     * @return A sprite reprenting this branch.
     */
    public Sprite getChunkSprite(Chunk.Type dir) {
        if (dir == Chunk.Type.CLEAR) return clearSprite[rnum.nextInt(SPRITE_COUNT)];
        else if (dir == Chunk.Type.LEFT) return leftBranchSprite[rnum.nextInt(SPRITE_COUNT)];
        else return rightBranchSprite[rnum.nextInt(SPRITE_COUNT)];
    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {

    }

    @Override
    public void dispose() {
        Gdx.app.log(TAG, "Disposing assets");
        assetManager.dispose();
    }

    /**
     * Some happy little clouds.... I feel like channelling Bob ross today..
     * https://www.youtube.com/watch?v=raXanYjTF18
     */
    public class Clouds {
        public final TextureAtlas.AtlasRegion cloud[] = new TextureAtlas.AtlasRegion[6];
        private Random rnum;

        public Clouds(TextureAtlas atlas) {
            cloud[0] = atlas.findRegion("cloud1");
            cloud[1] = atlas.findRegion("cloud2");
            cloud[2] = atlas.findRegion("cloud3");
            // We have 3 different cloud patterns stored as sprites. We'll manufacture 3 more by flipping the sprites.
            cloud[3] = new TextureAtlas.AtlasRegion(cloud[0]);
            cloud[4] = new TextureAtlas.AtlasRegion(cloud[1]);
            cloud[5] = new TextureAtlas.AtlasRegion(cloud[2]);
            cloud[3].flip(true, true);
            cloud[4].flip(true, true);
            cloud[5].flip(true, true);
            rnum = new Random();
        }

        public TextureAtlas.AtlasRegion getRandomCloud() {
            return cloud[rnum.nextInt(cloud.length)];
        }
    }

    public class AssetFonts {
        public final BitmapFont small;
        public final BitmapFont normal;
        public final BitmapFont big;

        public AssetFonts() {
            small = new BitmapFont(Gdx.files.internal("fonts/fontLarge.fnt"), true);
            normal = new BitmapFont(Gdx.files.internal("fonts/fontLarge.fnt"), true);
            big = new BitmapFont(Gdx.files.internal("fonts/fontLarge.fnt"), true);
            // set font sizes
            small.setScale(0.25f);
            normal.setScale(0.5f);
            big.setScale(1.0f);
            // enable linear texture filtering for smooth fonts
            small.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            normal.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            big.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
    }
}

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

/**
 * This class provides constants that are used widely throughout the game in one spot where they can be easily tweaked
 * <p/>
 * Created by james on 19/07/14.
 */
public class Constants {

    public static final String TEXTURE_ATLAS_UI = "images/loggerbill-ui.atlas";
    public static final String MAIN_UI = "gameSkins/loggerbill.json";
    public static final String TEXTURE_ATLAS_STD_UI = "images/uiskin.atlas";
    public static final String MAIN_STD_UI = "gameSkins/uiskin.json";

    public static final String ACHIEVEMENTS = "achievements.json";
    public static final String LEADERBOARDS = "leaderboards.json";

    public static final String VERTEX_SHADER = "shaders/ripple.vsl";
    public static final String FRAG_SHADER = "shaders/ripple.fsl";
    public static final String NIGHT_VERTEX_SHADER = "shaders/night.vsl";
    public static final String NIGHT_FRAG_SHADER = "shaders/night.fsl";

    /*
       "Bama Country" Kevin MacLeod (incompetech.com)
        Licensed under Creative Commons: By Attribution 3.0
        http://creativecommons.org/licenses/by/3.0/
    */
    public static final String MUSIC = "sounds/BamaCountry.ogg";
    public static final String THWACK = "sounds/axe-slash.wav";

    public static final String TEXTURE_ATLAS_GAME = "images/game-sprites.atlas";
    public static final String TEXTURE_ATLAS_SPLASH = "images/splash-sprites.atlas";

    public static final int CHUNK_COUNT = 6;


    public static final float CHUNK_SIZE = 150;


    public static final float BILL_DISTANCE = 130; // Distance bill stands from tree
    public static final float BILL_HEIGHT = 75; // How high from the bottom of the screen
    public static final float BILL_FRAME_TIME = 0.1f;

    public static final int LEFT = -1;
    public static final int RIGHT = 1;

    // GUI Width (Pixels). Height is calculated by function at bottom of this file.
    public static final float VIEWPORT_GUI_WIDTH = 720.0f;

    // For Desktop / HTML5 modes use this resolution.
    public static final float DESKTOP_GUI_WIDTH = 540.0f;
    public static final float DESKTOP_GUI_HEIGHT = 864.0f;

    // OpenGL viewport Width / Height (OpenGL arbitrary units). I'm using centimeters
    public static final float VIEW_WIDTH = 500f;  // 2.5 meters
    public static final float VIEW_HEIGHT = 888f; // 4 meters

    // Difficulty related consts
    public static final float RECHARGE_RATE = 0.015f; // How much time is added to your clock on a chop
    public static final int LEVEL_THRESHOLD = 25; // How many chunks to graduate to the next level
    public static final float START_SPEED = 0.04f; // initial timer countdown rate
    public static final float SPEED_INCREASE = 0.005f; // How much to increase the speed each level.

    // Profile constants
    public static final int MIN_CHUNKS_FOR_CPS = 10;

    // Tweet tweet
    public static final String app_url = "http://maple.scot/index.php/our-games/4-loggerbill";
    public static String app_specific_url = null;
    public static final String tweet_url1 = "https://twitter.com/intent/tweet?text=";
    public static final String tweet_url2 = "&url=";

    // facebook
    public static final String facebook_url = "https://www.facebook.com/LoggerBillGame";

    // email
    public static final String email_url1 = "mailto://";
    public static final String email_url2 = "?subject=";
    public static final String email_url3 = "&body=";
    public static final String maplescot_email = "info@maple.scot";

    // Source code
    public static final String source_url = "https://github.com/MapleScotDevelopment/LoggerBill";
    public static final String music_url = "http://incompetech.com/music/royalty-free/index.html?isrc=USUAN1100359";


    private static float vpHeight = 0.0f;

    /**
     * Unfortunately different android devices have different aspect ratios so we can't use a const to
     * specify the resolution. Instead we are going to specify only the width and then determine the height
     * using this function which multiplies the width by the devices natural aspect ratio. If we hardcoded the
     * height the scaling would be totally wrong on some devices.
     *
     * @return The calculated screen height (pixels) to scale to.
     */
    public static float getViewportHeight() {
        if (vpHeight == 0) {
            vpHeight = ((float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth()) * VIEWPORT_GUI_WIDTH;
        }
        return vpHeight;
    }
}




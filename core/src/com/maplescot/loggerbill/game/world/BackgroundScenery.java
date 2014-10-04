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
package com.maplescot.loggerbill.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.maplescot.loggerbill.misc.Assets;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

import static com.maplescot.loggerbill.misc.Constants.VIEW_HEIGHT;
import static com.maplescot.loggerbill.misc.Constants.VIEW_WIDTH;

/**
 * This class implements an animated background. This adds some 'life' to the game and makes it feel a lot 'nicer'
 * to play and distinguishes us a little bit from other similar games. This is shared in the MainMenu screen
 * and the Game Screen.
 * <p/>
 * Implemented as a singleton because only one background is needed and can be shared between screens.
 * <p/>
 * Created by troy on 18/08/14.
 */
public class BackgroundScenery {

    private static String TAG = BackgroundScenery.class.toString();
    private static BackgroundScenery myInstance = null;
    private static Random rnum = new Random();

    private OrthographicCamera camera;


    // variables to keep track of background activity.
    private BiPlane biPlane = null;
    private float biPlaneCount = 10;
    private Bird bird = null;
    private float birdCount = 1;
    private SpriteBatch batch;
    private boolean isNight;
    private ShaderProgram nightShader;
    private StarryNight stars;
    private int cloud_count = 6;



    private LinkedList<Cloud> clouds = new LinkedList<Cloud>();

    private BackgroundScenery() {
        Gdx.app.debug(TAG, "Instantiating screen background class"); // Should only be called once.

        camera = new OrthographicCamera(VIEW_WIDTH, VIEW_HEIGHT);
        camera.position.set(0, VIEW_HEIGHT / 2, 0f);
        camera.update();

        init();
    }

    public static BackgroundScenery getInstance() {
        if (myInstance == null) myInstance = new BackgroundScenery();
        return myInstance;
    }

    public void init() {

        batch = new SpriteBatch();
        //Generate some random clouds.
        while (clouds.size() < cloud_count)
            clouds.add(new Cloud(true));


    }

    public void setNight(boolean night) {

        // Add some weighted variability to the weather...
        switch (rnum.nextInt(5)) {
            case 0:
                cloud_count = rnum.nextInt(100);
                break;
            case 1:
                cloud_count = rnum.nextInt(50);
                break;
            case 2:
                cloud_count = rnum.nextInt(15);
                break;
            case 3:
                cloud_count = rnum.nextInt(10);
                break;
            case 4:
                cloud_count = rnum.nextInt(5);
                break;
            default:
                cloud_count = 2;
        }
        Gdx.app.log(TAG, "Cloud count: " + cloud_count);
        if (night && !isNight) stars = new StarryNight();
        else if (!night && stars != null ) {
            stars.dispose();
            stars = null;
        }
        isNight = night;

    }

    public void setNightShader(ShaderProgram shader) {
        nightShader = shader;
    }

    /**
     * Draws the background scenery. Because we use a different camera to keep things nice and segregated we have to
     * use a separate sprite batch. This could affect performance in larger applications, but here should not make a
     * difference.
     *
     * @param delta time slize
     */
    public void draw(float delta) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if (isNight)  stars.draw(batch, delta);
        if (isNight) {
            batch.setShader(nightShader);
            nightShader.setUniformf("u_amount", 0.95f);
        } else batch.setShader(null);

        // Draw some clouds for nice background ambiance
        ListIterator<Cloud> cloudIterator = clouds.listIterator();
        while (cloudIterator.hasNext()) {
            Cloud myCloud = cloudIterator.next();
            myCloud.draw(batch, delta);
            if (myCloud.getX() > VIEW_WIDTH / 2) {
                cloudIterator.remove();
            }
            if (clouds.size() < cloud_count) {
                if (rnum.nextInt(100) < 25) cloudIterator.add(new Cloud(false));
            }

        }

        // Draw the background hills
        Assets.getInstance().hillsDrawable.draw(batch, -VIEW_WIDTH / 2, 0f, VIEW_WIDTH, Assets.getInstance().hillsDrawable.getMinHeight());

        if (isNight) { // Fireflies at night
            batch.setShader(null);
        } else { // Birds and planes during the day
            if (biPlane == null) {
                if (biPlaneCount <= 0 && rnum.nextInt(500) <= 1) biPlane = new BiPlane();
                else biPlaneCount -= delta;
            }
            if (biPlane != null) {
                if (!biPlane.draw(batch, delta)) {
                    Gdx.app.log(TAG, "Destroying bi-Plane");
                    biPlane = null;
                    biPlaneCount = 15;
                }
            }


            if (bird == null) {
                if (birdCount <= 0 && rnum.nextInt(100) <= 20) bird = new Bird();
                else birdCount -= delta;
            }
            if (bird != null) {
                if (!bird.draw(batch, delta)) {
                    Gdx.app.log(TAG, "Destroying bird");
                    bird = null;
                    birdCount = 5;
                }
            }
        }

        batch.end();

    }


}

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

package com.maplescot.loggerbill.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.maplescot.loggerbill.misc.Assets;

import java.util.Random;

import static com.maplescot.loggerbill.misc.Constants.VIEW_HEIGHT;
import static com.maplescot.loggerbill.misc.Constants.VIEW_WIDTH;

/**
 * A happy little bird to give our game some life...
 */
public class Bird {
    private static String TAG = Bird.class.toString();
    private static Random rnum = new Random();
    private float x, y;
    private float speed;
    private int dir = 1;
    private Sprite birdSprite;

    private float stateTime = 0f;

    public Bird() {
        x = -(VIEW_WIDTH) - (rnum.nextFloat() * VIEW_WIDTH);
        y = 200 + rnum.nextFloat() * (VIEW_HEIGHT - 250);
        speed = 100f;
        if (rnum.nextInt(10) < 5) {
            speed = -speed;
            x = -x;
            dir = -dir;

        }
        Gdx.app.debug(TAG, "Spawning bird " + dir);
        stateTime = 0f;
        birdSprite = new Sprite();
        birdSprite.setSize(32, 32);
    }

    public boolean draw(SpriteBatch batch, float delta) {
        TextureRegion reg = Assets.getInstance().birdAnimation.getKeyFrame(stateTime, true);

        birdSprite.setRegion(reg);
        birdSprite.setPosition(x, y);
        if (dir == 1) birdSprite.flip(true, false);
        birdSprite.draw(batch);

        x += speed * delta;
        stateTime += delta;
        return !(dir == 1 && x > VIEW_WIDTH) && !(dir == -1 && x < -VIEW_WIDTH);
    }


}

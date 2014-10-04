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
import com.maplescot.loggerbill.misc.Assets;

import java.util.Random;

import static com.maplescot.loggerbill.misc.Constants.VIEW_HEIGHT;
import static com.maplescot.loggerbill.misc.Constants.VIEW_WIDTH;

/**
 * Another little bit of background life...
 */
public class BiPlane {
    private static String TAG = BiPlane.class.toString();
    private static Random rnum = new Random();

    private float x, y;
    private float speed;
    private int dir = 1;
    private Sprite sprite;

    public BiPlane() {

        sprite = new Sprite(Assets.getInstance().biPlaneRegion);
        //sprite.setScale(0.25f);
        x = -(VIEW_WIDTH) - (rnum.nextFloat() * VIEW_WIDTH);
        y = 200 + rnum.nextFloat() * (VIEW_HEIGHT - 250);
        speed = 100f;
        if (rnum.nextInt(10) < 5) {
            speed = -speed;
            x = -x;
            dir = -dir;

        } else {
            sprite.flip(true, false);
        }
        Gdx.app.debug(TAG, "Spawning Bi-Plane " + dir);

    }

    public boolean draw(SpriteBatch batch, float delta) {
        sprite.setPosition(x, y);
        sprite.draw(batch);
        x += speed * delta;
        return !(dir == 1 && x > VIEW_WIDTH) && !(dir == -1 && x < -VIEW_WIDTH);
    }
}

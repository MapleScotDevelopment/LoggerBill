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

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.maplescot.loggerbill.misc.Assets;

import java.util.Random;

import static com.maplescot.loggerbill.misc.Constants.VIEW_HEIGHT;
import static com.maplescot.loggerbill.misc.Constants.VIEW_WIDTH;

/**
 * Happy little clouds...This class represents a floating cloud in the background.
 */
public class Cloud {

    private static Random rnum = new Random();
    private float x, y;
    private float speed;
    private Sprite sprite;

    /**
     * Create a nice fluffy little cloud instance.
     *
     * @param init if init is true then clouds will spawn anywhere (for initialization). Else, clouds only spawn off screen.
     */
    public Cloud(boolean init) {
        if (init) x = VIEW_WIDTH * 2 * (rnum.nextFloat() - 0.5f);
        else x = -(VIEW_WIDTH) - (rnum.nextFloat() * VIEW_WIDTH);
        y = 100 + rnum.nextFloat() * (VIEW_HEIGHT - 100);
        speed = 0.1f + rnum.nextFloat() * 35;
        sprite = new Sprite(Assets.getInstance().clouds.getRandomCloud());
        // For storm effect... possibly.
        // sprite.setColor(0.25f, 0.25f, 0.25f, 1f);
        sprite.setAlpha(rnum.nextFloat() );
        sprite.setScale(1 + (rnum.nextFloat() / 2)); // Scale from 100 - 150%
    }

    public void draw(SpriteBatch batch, float delta) {
        sprite.setPosition(x, y);
        sprite.draw(batch);
        x += speed * delta;
    }

    // So we know when to kill this cloud.
    public float getX() {
        return x;
    }


}

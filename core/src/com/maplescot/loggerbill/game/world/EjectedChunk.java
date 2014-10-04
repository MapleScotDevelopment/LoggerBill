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

import static com.maplescot.loggerbill.misc.Constants.CHUNK_SIZE;
import static com.maplescot.loggerbill.misc.Constants.VIEW_WIDTH;

/**
 * This class is just a simple structure for tracking spewed chunks.
 */
public class EjectedChunk {
    private int direction;
    private Sprite sprite;

    public EjectedChunk(Sprite chunkSprite, int direction) {
        sprite = new Sprite(chunkSprite);
        this.direction = direction;
        sprite.translateX(direction * (CHUNK_SIZE / 2));
    }

    public boolean draw(SpriteBatch batch, float delta) {
        sprite.setY(192 - (CHUNK_SIZE / 2));
        sprite.translateY(delta * 100);
        sprite.translateX(delta * direction * 1000);
        sprite.rotate(direction * (delta * 500) % 360);
        sprite.draw(batch);
        return !(sprite.getX() > VIEW_WIDTH / 2 + CHUNK_SIZE ||
                sprite.getX() < (-VIEW_WIDTH / 2) - (CHUNK_SIZE * 2));
    }

}

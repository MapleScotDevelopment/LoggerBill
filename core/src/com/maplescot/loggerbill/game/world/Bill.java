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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.maplescot.loggerbill.misc.Assets;
import com.maplescot.loggerbill.misc.Constants;

import static com.maplescot.loggerbill.misc.Constants.*;

/**
 * Our hero...
 */
public class Bill {
    private final Sprite billGraveSprite;
    private final Sprite billSprite;

    private int billSide = LEFT;
    private boolean billDead = false;
    private float chopTime = Constants.BILL_FRAME_TIME;
    private boolean chopping = false;

    public Bill() {
        billSprite = new Sprite();
        billSprite.setSize(145, 192);
        billGraveSprite = new Sprite(Assets.getInstance().tombstoneRegion);

        billSprite.setCenterX(BILL_DISTANCE * billSide);
        billGraveSprite.setY(BILL_HEIGHT);
        billGraveSprite.setCenterX(BILL_DISTANCE * billSide);
    }

    private void flip() {
        billGraveSprite.setCenterX(BILL_DISTANCE * billSide);
    }

    public int getSide() {
        return billSide;
    }

    public void setSide(int billSide) {
        this.billSide = billSide;
        flip();
    }

    public void setChopping(boolean chopping) {
        this.chopping = chopping;
    }

    public void draw(SpriteBatch batch, float delta) {
        if (billDead) {
            billGraveSprite.draw(batch);
            return;
        }
        if (chopping) {
            if (chopTime > 4 * Constants.BILL_FRAME_TIME) {
                chopping = false;
                chopTime = Constants.BILL_FRAME_TIME;
            }
            TextureRegion reg = Assets.getInstance().billAnimation.getKeyFrame(chopTime, true);
            billSprite.setRegion(reg);
            billSprite.setCenterX(BILL_DISTANCE * billSide);
            billSprite.setY(BILL_HEIGHT);

            if (billSide == RIGHT) billSprite.flip(true, false);
            billSprite.draw(batch);

            chopTime += delta;
        } else {
            TextureRegion reg = Assets.getInstance().billAnimation.getKeyFrame(0f, true);
            billSprite.setRegion(reg);
            billSprite.setCenterX(BILL_DISTANCE * billSide);
            billSprite.setY(BILL_HEIGHT);
            if (billSide == RIGHT) billSprite.flip(true, false);
            billSprite.draw(batch);
        }
    }

    public void setDead(boolean dead) {
        billDead = dead;
    }

    public void chop() {
        chopTime = Constants.BILL_FRAME_TIME;
        chopping = true;
    }
}

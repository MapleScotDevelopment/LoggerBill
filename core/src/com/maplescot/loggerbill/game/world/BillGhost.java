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
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.maplescot.loggerbill.misc.Assets;

import static com.maplescot.loggerbill.misc.Constants.*;

/**
 * Come on baby, Don't fear the reaper.... Bill's ghost
 */
public class BillGhost {
    private float x, y, alpha;
    private int side;
    private Sprite sprite;
    private float ghostTime;
    private ShaderProgram ghostShader;

    public BillGhost(int side) {
        this.side = side;
        sprite = new Sprite(Assets.getInstance().billDeadRegion);
        if (side == 1) sprite.flip(true, false);
        sprite.setCenterX(BILL_DISTANCE * side);
        sprite.setCenterY(BILL_HEIGHT);
        alpha = 0.75f;
        x = sprite.getX();
        y = sprite.getY();
        ghostTime = 0;
    }

    public boolean draw(SpriteBatch batch, ShaderProgram ghostShader, float delta) {

        // We will use a wibbly-wobbly shader for bill's ghost
        batch.setShader(ghostShader);
        ghostTime += delta;
        ghostShader.setUniformf("time", ghostTime);
        //ghostShader.setUniformf("resolution", 1.0f, 1.0f);

        sprite.setPosition(x, y);
        sprite.setAlpha(alpha);

        sprite.draw(batch);
        y += delta * 250;
        x -= (MathUtils.sin(y / 100 % 360) * 2) * side;
        alpha = (VIEW_HEIGHT - (y / VIEW_HEIGHT / 2));

        batch.setShader(null); // turn off ghost shader.
        return y <= VIEW_HEIGHT + sprite.getHeight();
    }

}

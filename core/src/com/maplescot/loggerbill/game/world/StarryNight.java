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
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.maplescot.loggerbill.misc.Assets;
import com.maplescot.loggerbill.misc.Constants;

import java.util.Random;

/**
 *
 * A little digital Van Goph for our evening sky.
 *
 * Created by troy on 21/09/14.
 */
public class StarryNight implements Disposable {

    private static String TAG = StarryNight.class.toString();
    private static Random rnum = new Random();
    private Pixmap skyImage;
    private Texture skyTex;
    private float moonX;

    public StarryNight() {
        int w = (int) Constants.VIEW_WIDTH;
        int h = (int) Constants.VIEW_HEIGHT;
        Gdx.app.log(TAG, "Generating stars...");
        skyImage = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        skyImage.setColor(0,0,0,1f);
        skyImage.fillRectangle(0, 0, w, h);
        // Draw some stars
        for (int i = 0; i< 10000; i++) {
            float b = rnum.nextFloat(); // Brightness
            skyImage.setColor(b*.9f,b*0.9f,b,1f);
            skyImage.drawPixel(rnum.nextInt(w), rnum.nextInt(h));
        }

        skyTex = new Texture(skyImage, Pixmap.Format.RGBA8888, false);
        skyTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        moonX = (rnum.nextFloat() * Constants.VIEW_WIDTH) - (Constants.VIEW_WIDTH/2);

    }

    void draw(SpriteBatch batch, float delta) {
        batch.draw(skyTex, -Constants.VIEW_WIDTH/2, 0);
        // M-O-O-N, that spells moon.
        batch.draw(Assets.getInstance().moon, moonX, Constants.VIEW_HEIGHT - (Assets.getInstance().moon.getRegionHeight() * 2));
        moonX += (delta/3);
        if (moonX >  (Constants.VIEW_WIDTH/2) + 200) moonX =  -(Constants.VIEW_WIDTH/2) - 100;
    }

    @Override
    public void dispose() {
        skyTex.dispose();
        skyImage.dispose();
    }
}

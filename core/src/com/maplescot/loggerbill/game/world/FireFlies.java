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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.maplescot.loggerbill.misc.Assets;
import com.maplescot.loggerbill.misc.Constants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * During the day time we have some birds and planes in the background... to add a bit of life to the night
 * I'm also creating some nice fireflies.
 *
 * This can be done with a libGDX particle emitter.. it is possible.. I but really, really difficult to get right. I
 * first implemented it as a particle emitter but decided in the end that a direct implementation would be better.
 *
 * Created by troy on 20/09/14.
 */
public class FireFlies {
    private static String TAG = FireFlies.class.toString();
    private static Random rnum = new Random();
    private ArrayList<FireFly> flyList = new ArrayList<FireFly>();
    private int maxCount;
    private float minY;

    public FireFlies(int maxCount, float minY) {
        this.maxCount = maxCount;
        this.minY = minY;
        for (int i =0; i<rnum.nextInt(maxCount); i++) flyList.add(new FireFly());
    }

    public void draw(SpriteBatch batch, float delta) {
        Iterator<FireFly> it = flyList.iterator();
        while (it.hasNext()) {
            FireFly fly = it.next();
            if (!fly.draw(batch, delta)) it.remove();
        }
        if (flyList.size() < maxCount && rnum.nextInt(1000) < 10) flyList.add(new FireFly());
    }


    class FireFly {
        private float x,y;
        private double xdir, ydir;
        private float speed;
        private float life;
        private float alpha;
        private boolean fade;

        public FireFly() {
            Gdx.app.debug(TAG, "Spawning new firefly");
            x = rnum.nextInt((int) (Constants.VIEW_WIDTH)) - (Constants.VIEW_WIDTH /2);
            y = rnum.nextInt((int) (Constants.BILL_HEIGHT * 2)) + minY;
            speed = (rnum.nextFloat() + 0.5f) * 100;
            alpha = 1f; fade = false;
        }

        private void changeDir() {
            life = rnum.nextInt(5);
            float angle = rnum.nextFloat() * 360;
            xdir = Math.sin(Math.toRadians(angle));
            ydir = Math.cos(Math.toRadians(angle));

            if (rnum.nextInt(100) < 25) fade = true;
        }
        public boolean draw(SpriteBatch batch, float delta) {
            batch.setColor(1f,1f,1f,alpha);
            batch.draw(Assets.getInstance().fireFly, x, y);
            batch.setColor(1f,1f,1f,1f);
            x+= xdir * speed * delta;
            y+= ydir * speed * delta;
            if (y> (Constants.BILL_HEIGHT *3) + minY) {
                ydir=-Math.abs(ydir);
                xdir= Math.sin(Math.toRadians(rnum.nextFloat() * 360));
            }
            if (y< minY) {
                ydir= Math.abs(ydir);
                xdir= Math.sin(Math.toRadians(rnum.nextFloat() * 360));
            }
            if (x> Constants.VIEW_WIDTH) xdir=-Math.abs(xdir);
            if (x< -Constants.VIEW_WIDTH) xdir=Math.abs(xdir);

            life -= delta;
            if (life <= 0 ) changeDir();
            if (fade) alpha-=delta/2;
            return alpha > 0;
        }

    }
}

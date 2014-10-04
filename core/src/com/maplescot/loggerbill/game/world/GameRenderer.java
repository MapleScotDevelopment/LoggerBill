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

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * In our Model-View-Controller design this interface represents the View. it is called by GameScreen to update the
 * visual display of the world. For a simple game this could be implemented by the same class as the game engine.
 * <p/>
 * Created by troy on 24/09/14.
 */
public interface GameRenderer {
    /**
     * Called to draw the game-play world
     *
     * @param delta Time slice
     */
    public void drawWorld(SpriteBatch batch, float delta);

    /**
     * Called after the world to draw any sort of HUD or GUI on top of the world.
     *
     * @param delta time slice
     */
    public void drawHUD(SpriteBatch batch, float delta);

}

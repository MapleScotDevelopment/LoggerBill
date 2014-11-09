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

package com.maplescot.loggerbill.game;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Disposable;

/**
 * This interface helps us re-use code quicker for future games. The idea is to follow a general Model-View-Controller
 * approach. All the real game logic goes in a class that implements this interface, It uses actors that are implemented
 * separately. The interface GameRenderer handles drawing of the world. A single class may implement both the
 * GameEngine and GameRenderer interfaces,
 *
 * This approach allows the GameScreen class to be generic so it doesn't need to be changed when we create a new game.
 *
 * Created by troy on 20/09/14.
 */
public interface GameEngine extends Disposable {

    /**
     * Called the first time the game screen is created. Only called once.
     */
    public void init();

    /**
     * A game engine might be it's own InputProcessor for managing user interaction, or it could delegate this to a
     * different class to process input. Let the engine itself decide and tell our game screen which it is.
     *
     * @return the class to use for processing inputs
     */
    public InputProcessor getInputProcessor();

    /**
     * Called to re-set the game engine to it's default starting state - i.e. New Game.
     */
    public void reset();

    /**
     * This is called at the end of each frame to run the actual game-play logic.
     * @param delta Time slice
     * @return true if engine is still active, false to quite and return to main menu.
     */
    public boolean run(float delta);

    /**
     * pause or unpause the game
     */
    public void setPause(boolean paused);

    /**
     * This should generally just return the state of play - are we playing or are we stopped for some reason.
     *
     * @return false if game is playing, true if paused.
     */
    public boolean isPaused();

    /**
     * Is the character alive?
     * @return true if game is playing, false if game is over
     */
    public boolean isAlive();



}

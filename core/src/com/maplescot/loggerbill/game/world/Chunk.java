/*
 * (C) Copyright 2014 MapleScot Development
 * This file licensed under a Creative Commons 3.0 by attribution licence
 * https://creativecommons.org/licenses/by/3.0/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * https://github.com/duriej/LoggerBill
 */
package com.maplescot.loggerbill.game.world;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.maplescot.loggerbill.misc.Assets;

/**
 * This class represents an instance of 'chunk' of the tree.
 * <p/>
 * Created by troy on 23/08/14.
 */
public class Chunk {

    private Sprite sprite;
    private Type type;

    public Chunk(Type type) {
        this.type = type;
        sprite = Assets.getInstance().getChunkSprite(type);
    }

    public Type getType() {
        return type;
    }

    public Sprite getSprite() {
        return sprite;
    }

    // The side that the branch is on.
    public enum Type {
        LEFT, RIGHT, CLEAR
    }

}

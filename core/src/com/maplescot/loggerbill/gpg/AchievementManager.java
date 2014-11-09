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
package com.maplescot.loggerbill.gpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.maplescot.loggerbill.misc.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Manage achievments
 * <p/>
 * Created by james on 27/07/14.
 */
public class AchievementManager {
    private static String TAG = AchievementManager.class.toString();

    private List<Achievement> achievements = new ArrayList<Achievement>();

    public void init() {

        JsonReader jsonReader = new JsonReader();
        JsonValue values = jsonReader.parse(Gdx.files.internal(Constants.ACHIEVEMENTS));
        for (JsonValue value = values.child; value != null; value = value.next) {
            if (value.type() == JsonValue.ValueType.object) {
                String objectName = value.name();
                Achievement achievement = null;

                try {
                    //achievement = (Achievement) ClassReflection.forName(objectName);
                    achievement = (Achievement) ClassReflection.newInstance(ClassReflection.forName(objectName));
                } catch (ReflectionException e) {
                    e.printStackTrace();
                }

                achievement.init(value.get("id").asString(), value.get("name").asString(),
                        value.get("desc").asString(), value.get("type").asString(), value.get("test").asString(),
                        value.get("value").asFloat(), value.get("incremental").asBoolean());
                Gdx.app.debug(TAG, achievement.toString());
                achievements.add(achievement);
            }
        }
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }
}

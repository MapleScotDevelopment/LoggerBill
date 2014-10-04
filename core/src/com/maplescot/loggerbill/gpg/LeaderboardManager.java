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
import com.maplescot.loggerbill.misc.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Manage our leaderboards
 * <p/>
 * Created by james on 27/07/14.
 */
public class LeaderboardManager {
    private static String TAG = LeaderboardManager.class.toString();

    private List<Leaderboard> leaderboards = new ArrayList<Leaderboard>();

    public void init() {
        JsonReader jsonReader = new JsonReader();
        JsonValue values = jsonReader.parse(Gdx.files.internal(Constants.LEADERBOARDS));
        for (JsonValue value : values) {
            Leaderboard l = new Leaderboard(value.get("id").asString(), value.get("name").asString());
            leaderboards.add(l);
        }
    }

    public List<Leaderboard> getLeaderboards() {
        return leaderboards;
    }
}

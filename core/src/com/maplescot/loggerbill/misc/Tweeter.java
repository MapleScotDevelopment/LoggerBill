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
package com.maplescot.loggerbill.misc;

import com.badlogic.gdx.Gdx;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * This class handles twitter postings
 * <p/>
 * Created by james on 16/07/14.
 */
public class Tweeter {
    private static final String TAG = Tweeter.class.toString();
    private static Resolver actual = null;
    private static Tweeter instance = null;

    private Tweeter() {
    }

    public static Tweeter getInstance() {
        if (instance == null)
            instance = new Tweeter();
        return instance;
    }

    public static void init(Resolver actual) {
        Tweeter.actual = actual;
    }

    public void postTweet(String desc, String url) {
        String tweet = new StringBuilder()
                .append(Constants.tweet_url1).append(urlEncode(desc))
                .append(Constants.tweet_url2).append(urlEncode(url))
                .toString();
        if (actual != null)
            actual.postTweet(tweet);
        else
            Gdx.net.openURI(tweet);
    }

    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Gdx.app.error(TAG, "UTF-8 should always be supported", e);
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }

    public interface Resolver {
        public void postTweet(String tweet);
    }
}

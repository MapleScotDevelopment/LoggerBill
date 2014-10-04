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
 * Handle emailing
 * Created by james on 16/07/14.
 */
public class Emailer {
    private static final String TAG = Emailer.class.toString();
    private static Resolver actual=null;
    private static Emailer instance=null;

    private Emailer() {
    }

    public static Emailer getInstance() {
        if (instance == null)
            instance = new Emailer();
        return instance;
    }

    public static void init(Resolver actual) {
        Emailer.actual = actual;
    }

    public void postEmail(String to, String subject, String body) {
        if (actual != null)
            actual.postEmail(to, subject, body);
        else {
            String email = Constants.email_url1 + to + Constants.email_url2 + urlEncode(subject) + Constants.email_url3 + urlEncode(body);
            Gdx.app.debug(TAG, email);
            Gdx.net.openURI(email);
        }
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
        public void postEmail(String to, String subject, String body);
    }
}

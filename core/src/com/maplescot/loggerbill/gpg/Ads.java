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

/**
 * This class handles providing advertising for different platforms.
 * <p/>
 * Created by james on 16/07/14.
 */
public class Ads {
    private static Resolver actual;

    private Ads() {
    }

    public static Resolver getInstance() {
        return actual;
    }

    public static void init(Resolver actual) {
        Ads.actual = actual;
    }

    public interface Resolver {
        public void showBanner(boolean show);

        public void showInterstitial();

        public void endAds();
    }
}

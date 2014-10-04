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
 * Save the user's profile to 'the cloud'
 * Created by james on 07/09/14.
 */
public class CloudSave {
    private static Resolver actual;

    private CloudSave() {
    }

    public static Resolver getInstance() {
        return actual;
    }

    public static void init(Resolver actual) {
        CloudSave.actual = actual;
    }

    public interface Resolver {
        public void register(ConflictResolver conflictResolver);

        public void save(byte[] profile);
    }

    public interface ConflictResolver {
        void resolveConflicts(String currentAccountName, byte[] p1, byte[] p2);
    }
}

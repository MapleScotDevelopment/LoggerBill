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
 * Track achievements
 * Created by james on 26/07/14.
 */
public abstract class Achievement {
    private String id;
    private String name;
    private String desc;
    private String type;
    private TestType test;
    private float value;
    private boolean incremental;

    public Achievement() {}

    public void init(String id, String name, String desc, String type, String test, float value, boolean incremental) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.type = type;
        this.test = TestType.valueOf(test);
        this.value = value;
        this.incremental = incremental;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getType() {
        return type;
    }

    public float getValue() {
        return value;
    }

    public boolean isIncremental() {
        return incremental;
    }

    public abstract boolean test(int chunk_counter, float cps, long totalPlays, long totalChunks, long bestChunks, float bestCPS);

    public boolean doTest(float testValue) {
        boolean result=false;
        switch (test) {
            case LT:
                if (testValue < value)
                    result = true;
                break;
            case LE:
                if (testValue <= value)
                    result = true;
                break;
            case EQ:
                if (testValue == value)
                    result = true;
                break;
            case GE:
                if (testValue >= value)
                    result = true;
                break;
            case GT:
                if (testValue > value)
                    result = true;
                break;
        }

        return result;
    }

    @Override
    public String toString() {
        return "Achievement{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", type='" + type + '\'' +
                ", test=" + test +
                ", value=" + value +
                ", incremental=" + incremental +
                '}';
    }

    public enum TestType {
        LT,
        LE,
        EQ,
        GE,
        GT
    }
}

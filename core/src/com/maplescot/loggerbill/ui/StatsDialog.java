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
package com.maplescot.loggerbill.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.maplescot.loggerbill.gpg.GPG;
import com.maplescot.loggerbill.misc.Assets;
import com.maplescot.loggerbill.misc.ProfileManager;

/**
 * A simple about us dialog with links to rate, like, tweet, and send us abuse.
 * <p/>
 * Created by james on 14/09/14.
 */
public class StatsDialog extends Dialog {

    // button types
    private static final int CLOSE = 200;
    private ImageTextButton login;

    public StatsDialog(String title, Skin skin, String windowStyleName) {
        super(title, skin, windowStyleName);
        key(Input.Keys.ESCAPE, CLOSE);
        key(Input.Keys.BACK, CLOSE);
        Image ach;

        Table content = getContentTable();
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            login = new ImageTextButton("Login", skin);
            login.getImage().setDrawable(Assets.getInstance().skin, "games_controller");
            content.add(login).center().row();
            login.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    GPG.getInstance().login();
                }
            });
        }
        Table list = new Table(skin);
        list.add(new Label("Best Chunks", skin, "medium")).left();
        list.add(new Label(String.valueOf(ProfileManager.getProfile().getBestChunks()), skin, "medium")).right().row();
        list.add(new Label("Best Chunks Per Second:", skin, "medium")).left();
        list.add(new Label(ProfileManager.getProfile().getBestCPSFormatted().toString(), skin, "medium")).right().row();
        list.add(new Label("Total Plays:", skin, "medium")).left();
        list.add(new Label(String.valueOf(ProfileManager.getProfile().getTotalPlays()), skin, "medium")).right().row();
        list.add(new Label("Total Chunks:", skin, "medium")).left();
        list.add(new Label(String.valueOf(ProfileManager.getProfile().getTotalChunks()), skin, "medium")).right().row();
        list.add(new Label("Average Chunks:", skin, "medium")).left();
        list.add(new Label(ProfileManager.getProfile().getAvgChunksFormatted().toString(), skin, "medium")).right().row();

        content.add(list).fill().row();
        button("Close", CLOSE);
    }

    @Override
    protected void result(Object object) {
        switch ((Integer) object) {
            case CLOSE:
                hide();
                break;
        }
    }
}

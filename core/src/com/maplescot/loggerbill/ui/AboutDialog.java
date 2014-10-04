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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.maplescot.loggerbill.misc.Emailer;
import com.maplescot.loggerbill.misc.Tweeter;

import static com.maplescot.loggerbill.misc.Constants.*;

/**
 * A simple about us dialog with links to rate, like, tweet, and send us abuse.
 * <p/>
 * Created by james on 14/09/14.
 */
public class AboutDialog extends Dialog implements EventListener {

    private final TextButton rate;
    private final TextButton like;
    private final TextButton tweet;
    private final TextButton email;
    private final TextButton getSource;

    private final String aboutText="Logger Bill is a clone of another popular wood chopping game.\n" +
            "We built the game to learn about the libGdx framework\n" +
            "with the intention of giving away the code";

    // button types
    private static final int CLOSE = 200;
    private final TextButton music;


    public AboutDialog(String title, Skin skin, String windowStyleName) {
        super(title, skin, windowStyleName);
        key(Input.Keys.ESCAPE, CLOSE);
        key(Input.Keys.BACK, CLOSE);

        Table content = getContentTable();
        content.add(new Label("Source", skin)).center().row();
        content.add(new Label(aboutText, skin)).expand().row();
        getSource = new TextButton("Get Source", skin, "link");
        getSource.addListener(this);
        content.add(getSource).center().row();

        content.add(new Label("Credits", skin)).center().row();
        content.add(new Label("Coding", skin)).left().row();
        content.add(new Label("James Durie", skin)).right().row();
        content.add(new Label("Troy Peterson", skin)).right().row();
        content.add(new Label("Graphics", skin)).left().row();
        content.add(new Label("James Durie", skin)).right().row();
        content.add(new Label("Troy Peterson", skin)).right().row();

        content.add(new Label("Music", skin)).left().row();
        music = new TextButton("\"Bama Country\" Kevin MacLeod (incompetech.com)", skin, "link");
        content.add(music).center().row();
        content.add(new Label("Licensed under Creative Commons: By Attribution 3.0\n" +
                "http://creativecommons.org/licenses/by/3.0/", skin)).right().row();
        music.addListener(this);

        Table aboutTable = getButtonTable();

        Table socialButtons = new Table();
        rate = new TextButton("RATE", skin);
        socialButtons.add(rate).prefWidth(30).pad(5);
        rate.addListener(this);

        like = new TextButton("LIKE", skin);
        socialButtons.add(like).prefWidth(30).pad(5);
        like.addListener(this);
        socialButtons.row();

        tweet = new TextButton("TWEET", skin);
        socialButtons.add(tweet).prefWidth(30).pad(5);
        tweet.addListener(this);

        email = new TextButton("EMAIL", skin);
        socialButtons.add(email).prefWidth(30).pad(5);
        email.addListener(this);

        aboutTable.add(socialButtons).expand();
        aboutTable.row();
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

    public void changed(ChangeEvent event, Actor actor) {
        if (actor == rate)
            Gdx.net.openURI(app_url);
        if (actor == like)
            Gdx.net.openURI(facebook_url);
        if (actor == tweet)
            Tweeter.getInstance().postTweet("Hey @MaplescotDev I am loving #LoggerBill\n", app_url);
        if (actor == email)
            Emailer.getInstance().postEmail(maplescot_email, "LoggerBill", "I am loving Logger Bill!");
        if (actor == getSource)
            Gdx.net.openURI(source_url);
        if (actor == music)
            Gdx.net.openURI(music_url);
    }

    @Override
    public boolean handle(Event event) {
        if (!(event instanceof ChangeEvent)) return false;
        changed((ChangeEvent) event, event.getTarget());
        return false;
    }
}

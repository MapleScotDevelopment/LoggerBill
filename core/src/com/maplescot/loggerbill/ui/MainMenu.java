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

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.maplescot.loggerbill.game.LoggerEngine;
import com.maplescot.loggerbill.game.world.BackgroundScenery;
import com.maplescot.loggerbill.gpg.Achievement;
import com.maplescot.loggerbill.gpg.Ads;
import com.maplescot.loggerbill.gpg.GPG;
import com.maplescot.loggerbill.misc.Assets;
import com.maplescot.loggerbill.misc.Constants;
import com.maplescot.loggerbill.misc.ProfileManager;
import com.maplescot.loggerbill.misc.Tweeter;

import static com.maplescot.loggerbill.misc.Constants.app_url;

/**
 * The main game menu
 * Created by james on 19/07/14.
 */
public class MainMenu extends AbstractScreen {
    private static final String TAG = MainMenu.class.toString();
    private Stage stage;

    private ImageButton musicButton;
    private ImageButton speakerButton;
    private Dialog exitDialog;
    private Dialog aboutDialog;
    private Table menu;
    private boolean play = false; // NB: See hide()

    public MainMenu(Game game) {
        super(game);
    }


    private void rebuildStage() {
        Gdx.app.log(TAG, "Building Menu Screen");

        menu = new Table();
        menu.setFillParent(true);
        menu.top().left();


        Image loggerBill = new Image(skin, "LoggerBill");
        //loggerBill.setScaling(Scaling.fillX);
        //Scaling.fit);
        menu.add(loggerBill).fill();
        menu.row();
        Button logoButton = new Button(skin, "logoButton");
        menu.add(logoButton).center();
        menu.row();

        Button playButton = new Button(skin, "playButton");
        menu.add(playButton).center().expand();
        menu.row();

        Button achButton = new Button(skin, "achButton");
        Button leaderButton = new Button(skin, "leaderButton");
        Button twitButton = new Button(skin, "twitButton");

        twitButton.setDisabled(!ProfileManager.getProfile().hasUntweetedAchievements());

        musicButton = new ImageButton(skin, "musicButton");
        speakerButton = new ImageButton(skin, "speakerButton");
        Button offButton = new Button(skin, "offButton");

        speakerButton.setChecked(ProfileManager.getProfile().isSoundOn());
        speakerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean soundOn = !ProfileManager.getProfile().isSoundOn();
                ProfileManager.getProfile().setSoundOn(soundOn);
                speakerButton.setChecked(soundOn);
            }
        });

        musicButton.setChecked(ProfileManager.getProfile().isMusicOn());
        musicButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean musicOn = !ProfileManager.getProfile().isMusicOn();
                ProfileManager.getProfile().setMusicOn(musicOn);
                musicButton.setChecked(musicOn);
                Assets.getInstance().playMusic(ProfileManager.getProfile().isMusicOn());
            }
        });

        achButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ProfileManager.saveProfile();
                GPG.getInstance().showAchievements(stage);
            }
        });
        leaderButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ProfileManager.saveProfile();
                GPG.getInstance().showLeaderboards(stage);
            }
        });

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Ads.getInstance().showBanner(true);
                play = true;
                game.setScreen(new GameScreen(game, new LoggerEngine()));
            }
        });

        offButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                exit();
            }
        });

        twitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                for (Achievement a : ProfileManager.getAchievementManager().getAchievements()) {
                    if (ProfileManager.getProfile().isAchieved(a)) {
                        if (!ProfileManager.getProfile().isTweeted(a)) {
                            Tweeter.getInstance().postTweet(a.getName() + "\n" + a.getDesc() + "\n#LoggerBill\n", app_url);
                            ProfileManager.getProfile().setTweeted(a);
                        }
                    }
                }
            }
        });

        logoButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                aboutDialog.show(stage);
            }
        });

        Table buttons1 = new Table();
        buttons1.add(achButton).expand();
        buttons1.add(leaderButton).expand();
        buttons1.add(twitButton).expand();
        Table buttons2 = new Table();
        buttons2.add(musicButton).expandX();
        buttons2.add(speakerButton).expandX();
        buttons2.add(offButton).expandX();


        menu.add(buttons1).fill().padBottom(30f);
        menu.row();
        menu.add(buttons2).left().bottom().fillX();

        menu.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
                    MainMenu.this.exit();
                }
                return super.keyDown(event, keycode);
            }
        });

        stage.clear();
        stage.addActor(menu);
        stage.setKeyboardFocus(menu);
        stage.setScrollFocus(menu);

        exitDialog = new Dialog("Exit Game?", uiSkin, "dialog") {
            protected void result(Object object) {
                if ((Boolean) object) {
                    Gdx.app.exit();
                } else Assets.getInstance().playMusic(ProfileManager.getProfile().isMusicOn());
            }
        }.button("Yes", true).button("No", false)
                .key(Input.Keys.ENTER, true).key(Input.Keys.ESCAPE, false).key(Input.Keys.BACK, false);
        exitDialog.setModal(true);

        aboutDialog = new AboutDialog("About", uiSkin, "aboutdialog");
    }

    @Override
    public void render(float delta) {
        // Colour my world blue.
        Gdx.gl.glClearColor(0.36f, 0.58f, 0.988f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        BackgroundScenery.getInstance().draw(delta);
        stage.act(delta);


        // Work around the bug in Dialog which stops it reassigning keyboard focus to menu
        if (stage.getKeyboardFocus() == null)
            stage.setKeyboardFocus(menu);
        stage.draw();
    }

    @Override
    public void show() {
        stage = new Stage(new FillViewport(Constants.VIEWPORT_GUI_WIDTH, Constants.getViewportHeight()));
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);

        Ads.getInstance().showBanner(false);
        BackgroundScenery.getInstance().init();
        BackgroundScenery.getInstance().setNight(false); // Never night on the main menu.
        rebuildStage();
    }

    private void exit() {
        // exit the game
        ProfileManager.saveProfile();
        Ads.getInstance().endAds();
        exitDialog.show(stage);
        Assets.getInstance().playMusic(false);
    }

    @Override
    public void hide() {
        // If this gets hidden then we want to pause the music. However, this also gets called when
        // the play button is pressed. We don't want to stop the music in that case.
        if (!play) Assets.getInstance().playMusic(false);
        play = false;
    }


    @Override
    public void dispose() {
        stage.dispose();
    }
}

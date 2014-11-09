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
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.maplescot.loggerbill.game.LoggerEngine;
import com.maplescot.loggerbill.misc.Assets;
import com.maplescot.loggerbill.misc.Constants;
import com.maplescot.loggerbill.misc.ProfileManager;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * This is the Pause / Game over dialog. It is displayed by pressing back(android), escape(PC), or pause, or by dieing.
 * <p/>
 * Created by troy on 24/08/14.
 */
public class PausedDialog {

    private static String TAG = PausedDialog.class.toString();
    private final LoggerEngine engine;
    // UI Elements
    Window window;
    private Stage stage;
    private Skin billSkin;
    private int chunks;
    private float time;


    public PausedDialog(final LoggerEngine engine) {
        billSkin = Assets.getInstance().skin;
        //stage = new Stage();
        this.engine = engine;
        stage = new Stage(new FillViewport(Constants.VIEWPORT_GUI_WIDTH, Constants.getViewportHeight())) {
            public boolean keyDown(int keyCode) {
                Gdx.app.log(TAG, "BACK");
                if (keyCode == Input.Keys.BACK || keyCode == Input.Keys.ESCAPE) {
                    Gdx.input.setInputProcessor(engine.getInputProcessor());
                    if (engine.isAlive()) engine.setPause(false);
                    else engine.endGame();
                } else if (keyCode ==  Input.Keys.ENTER) {
                    resumeGame();
                }


                return false;
            }
        };
    }

    private void rebuildStage(boolean anim) {
        Table outerTable = new Table();

        stage.clear();
        Stack stack = new Stack();

        if (engine.isAlive()) {
            window = new Window("Game Paused", billSkin, "paused");
        } else window = new Window("Game Over", billSkin, "game_over");

        window.setMovable(true);
        window.setResizeBorder(10);
        window.setResizable(true);
        window.pad(45, 50, 50, 50);
        window.setKeepWithinStage(false);


        int plays = (int) ProfileManager.getProfile().getTotalPlays();
        BigDecimal myCPS = chunks > Constants.MIN_CHUNKS_FOR_CPS ? new BigDecimal(chunks / time).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;


        Table textLayer = new Table();
        textLayer.center().top();
        textLayer.add(new Label("Total Chunks", billSkin, "label")).row();
        textLayer.add(new Label( String.valueOf(chunks), billSkin, "score")).row();

        textLayer.add(new Label("Chunks Per Second", billSkin, "label")).row();
        textLayer.add(new Label( String.valueOf(myCPS), billSkin, "score")).row();

        if (plays > 0) {
            textLayer.add(new Label("__________________________", billSkin, "score")).row();
            textLayer.add(new Label("Best Chunks " + ProfileManager.getProfile().getBestChunks(), billSkin, "score")).row();
            textLayer.add(new Label("Best Chunks Per Second: " + ProfileManager.getProfile().getBestCPSFormatted(), billSkin, "score")).row();
            textLayer.add(new Label("Total Plays: " + plays, billSkin, "score")).row();
            textLayer.add(new Label("Average Chunks " + ProfileManager.getProfile().getAvgChunksFormatted(), billSkin, "score")).row();
        }

        if (chunks > ProfileManager.getProfile().getBestChunks())
            textLayer.add(new Label("New Best Chunks!", billSkin, "highscore")).row();
        if (myCPS.floatValue() > ProfileManager.getProfile().getBestCPS())
            textLayer.add(new Label("New High Speed!", billSkin, "highscore")).row();
        window.add(textLayer).row();


        Table btnLayer = new Table();

        Button menuBtn = new Button(billSkin, "backButton");
        Button playBtn = new Button(billSkin, "replayButton");

        menuBtn.padLeft(10);

        btnLayer.bottom(); //.right();
        btnLayer.add(menuBtn);
        btnLayer.bottom();
        btnLayer.add(playBtn);
        window.add(btnLayer);

        window.pack();
        window.setWidth(Constants.VIEWPORT_GUI_WIDTH / 1.25f);


        stack.add(outerTable);

        stage.addActor(window);
        stack.setSize(Constants.VIEWPORT_GUI_WIDTH, Constants.getViewportHeight());

        playBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                resumeGame();
            }
        });

        menuBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                engine.endGame();
            }
        });

        if (anim) {
            window.setPosition((Constants.VIEWPORT_GUI_WIDTH / 2) - (window.getWidth() / 2), -Constants.getViewportHeight());
            window.addAction(Actions.moveTo((Constants.VIEWPORT_GUI_WIDTH / 2) - (window.getWidth() / 2), (Constants.getViewportHeight() / 2) - (window.getHeight() / 2), 1.0f, Interpolation.swing));
        } else {
            window.setPosition((Constants.VIEWPORT_GUI_WIDTH / 2) - (window.getWidth() / 2), (Constants.getViewportHeight() / 2) - (window.getHeight() / 2));
        }
        window.setKeepWithinStage(false);
    }

    public void show(int chunks, float time, boolean anim) {
        this.chunks = chunks;
        this.time = time;
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);
        rebuildStage(anim);
    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    private void resumeGame() {
        if (!engine.isAlive()) engine.reset();
        engine.setPause(false);
        Gdx.input.setInputProcessor(engine.getInputProcessor());
    }

    public void dispose() {
        billSkin.dispose();
        billSkin.dispose();
        stage.dispose();

    }

}

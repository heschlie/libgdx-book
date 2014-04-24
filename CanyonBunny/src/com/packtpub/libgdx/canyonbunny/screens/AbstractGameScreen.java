package com.packtpub.libgdx.canyonbunny.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.packtpub.libgdx.canyonbunny.game.Assets;

/**
 * Created by heschlie on 4/4/2014.
 */
public abstract class AbstractGameScreen implements Screen {

    protected Game game;

    public AbstractGameScreen(Game game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        Assets.instance.init(new AssetManager());
    }

    @Override
    public void dispose() {
        Assets.instance.dispose();
    }
}

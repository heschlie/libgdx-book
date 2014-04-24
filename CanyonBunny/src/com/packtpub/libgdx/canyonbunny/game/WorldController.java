package com.packtpub.libgdx.canyonbunny.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.packtpub.libgdx.canyonbunny.game.objects.BunnyHead;
import com.packtpub.libgdx.canyonbunny.game.objects.BunnyHead.JUMP_STATE;
import com.packtpub.libgdx.canyonbunny.game.objects.Feather;
import com.packtpub.libgdx.canyonbunny.game.objects.GoldCoin;
import com.packtpub.libgdx.canyonbunny.game.objects.Rock;
import com.packtpub.libgdx.canyonbunny.screens.MenuScreen;
import com.packtpub.libgdx.canyonbunny.util.CameraHelper;
import com.packtpub.libgdx.canyonbunny.util.Constants;

/**
 * Created by heschlie on 3/11/14.
 */
public class WorldController extends InputAdapter{
    private static final String TAG = WorldController.class.getName();

    private Game game;
    public CameraHelper cameraHelper;
    public Level level;
    public int lives;
    public int score;
    private Rectangle r1 = new Rectangle();
    private Rectangle r2 = new Rectangle();
    private float timeLeftGameOverDelay;

    private void initLevel() {
        score = 0;
        level = new Level(Constants.LEVEL_01);
        cameraHelper.setTarget(level.bunnyHead);
    }

    public WorldController(Game game) {
        this.game = game;
        init();
    }

    private void init() {
        Gdx.input.setInputProcessor(this);
        cameraHelper = new CameraHelper();
        lives = Constants.LIVES_START;
        timeLeftGameOverDelay = 0;
        initLevel();
    }

    public boolean isGameOver() {
        return lives < 0;
    }

    public boolean isPlayerInWater() {
        return level.bunnyHead.position.y < -5;
    }

    private void backToMenu() {
        game.setScreen(new MenuScreen(game));
    }

    private void onCollisionBunnyHeadWithRock(Rock rock) {
        BunnyHead bunnyHead = level.bunnyHead;
        float heightDifference = Math.abs(bunnyHead.position.y - (rock.position.y + rock.bounds.height));
        if (heightDifference > 0.25f) {
            boolean hitLeftEdge = bunnyHead.position.x > (rock.position.x + rock.bounds.width / 2.0f);
            if (hitLeftEdge) {
                bunnyHead.position.x = rock.position.x + rock.bounds.width;
            } else {
                bunnyHead.position.x = rock.position.x - bunnyHead.bounds.width;
            }
            return;
        }

        switch (bunnyHead.jumpState) {
            case GROUNDED:
                break;
            case FALLING:
            case JUMP_FALLING:
                bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y;
                bunnyHead.jumpState = JUMP_STATE.GROUNDED;
                break;
            case JUMP_RISING:
                bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y;
                break;
        }
    }

    private void onCollisionBunnyHeadWithCoin(GoldCoin goldCoin) {
        goldCoin.collected = true;
        score += goldCoin.getScore();
        Gdx.app.log(TAG, "Gold coin collected");
    }

    private void onCollisionBunnyHeadWithFeather(Feather feather) {
        feather.collected = true;
        score += feather.getScore();
        level.bunnyHead.setFeatherPowerUp(true);
        Gdx.app.log(TAG, "Feather collected");
    }

    private void testCollisions() {
        r1.set(level.bunnyHead.position.x,
                level.bunnyHead.position.y,
                level.bunnyHead.bounds.width,
                level.bunnyHead.bounds.height);

        for (Rock rock : level.rocks) {
            r2.set(rock.position.x,
                    rock.position.y,
                    rock.bounds.width,
                    rock.bounds.height);
            if (!r1.overlaps(r2)) continue;
            onCollisionBunnyHeadWithRock(rock);
        }

        for (GoldCoin goldCoin : level.goldCoins) {
            if (goldCoin.collected) continue;
            r2.set(goldCoin.position.x,
                    goldCoin.position.y,
                    goldCoin.bounds.width,
                    goldCoin.bounds.height);
            if (!r1.overlaps(r2)) continue;
            onCollisionBunnyHeadWithCoin(goldCoin);
            break;
        }

        for (Feather feather : level.feathers) {
            r2.set(feather.position.x,
                    feather.position.y,
                    feather.bounds.width,
                    feather.bounds.height);
            if (!r1.overlaps(r2)) continue;
            onCollisionBunnyHeadWithFeather(feather);
            break;
        }
    }

    private Pixmap createProceduralPixmap(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 0, 0, .5f);
        pixmap.fill();
        pixmap.setColor(1, 1, 0, 1);
        pixmap.drawLine(0, 0, width, height);
        pixmap.drawLine(width, 0, 0, height);
        pixmap.setColor(0, 1, 1, 1);
        pixmap.drawRectangle(0, 0, width, height);

        return pixmap;
    }

    public void update(float deltaTime) {
        handleDebugInput(deltaTime);
        if (isGameOver()) {
            timeLeftGameOverDelay -= deltaTime;
            if (timeLeftGameOverDelay < 0) backToMenu();
        } else {
            handleInputGame(deltaTime);
        }
        level.update(deltaTime);
        testCollisions();
        cameraHelper.update(deltaTime);
        if (!isGameOver() && isPlayerInWater()) {
            lives--;
            if (isGameOver()) {
                timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
            } else {
                initLevel();
            }
        }
    }

    private void handleDebugInput(float deltaTime) {
        if (Gdx.app.getType() != Application.ApplicationType.Desktop) return;

        if (!cameraHelper.hasTarget(level.bunnyHead)) {
            float camMoveSpeed = 5 * deltaTime;
            float camMoveSpeedAccelerationFactor = 5;

            if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camMoveSpeed *= camMoveSpeedAccelerationFactor;
            if (Gdx.input.isKeyPressed(Keys.LEFT)) movemoveCamera(-camMoveSpeed, 0);
            if (Gdx.input.isKeyPressed(Keys.RIGHT)) movemoveCamera(camMoveSpeed, 0);
            if (Gdx.input.isKeyPressed(Keys.UP)) movemoveCamera(0, camMoveSpeed);
            if (Gdx.input.isKeyPressed(Keys.DOWN)) movemoveCamera(0, -camMoveSpeed);
            if (Gdx.input.isKeyPressed(Keys.BACKSPACE)) cameraHelper.setPosition(0, 0);
        }

        float camZoomSpeed = 1 * deltaTime;
        float camZoomSpeedAccelerationFactor = 5;

        if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camZoomSpeed *= camZoomSpeedAccelerationFactor;
        if (Gdx.input.isKeyPressed(Keys.PERIOD)) cameraHelper.addZoom(-camZoomSpeed);
        if (Gdx.input.isKeyPressed(Keys.COMMA)) cameraHelper.addZoom(camZoomSpeed);
        if (Gdx.input.isKeyPressed(Keys.SLASH)) cameraHelper.setZoom(1);

    }

    private void movemoveCamera(float x, float y) {
        x += cameraHelper.getPosition().x;
        y += cameraHelper.getPosition().y;
        cameraHelper.setPosition(x, y);
    }

    @Override
    public boolean keyUp (int keyCode) {
        if (keyCode == Keys.R) {
            init();
            Gdx.app.debug(TAG, "Game World Reset");
        } else if (keyCode == Keys.ENTER) {
            cameraHelper.setTarget(cameraHelper.hasTarget() ? null: level.bunnyHead);
            Gdx.app.debug(TAG, "Camera follow enabled: " + cameraHelper.hasTarget());
        } else if (keyCode == Keys.ESCAPE || keyCode == Keys.BACK) {
            backToMenu();
        }
        return false;
    }

    private void handleInputGame(float deltaTime) {
        if (cameraHelper.hasTarget(level.bunnyHead)) {
            if (Gdx.input.isKeyPressed(Keys.LEFT)) {
                level.bunnyHead.velocity.x = -level.bunnyHead.terminalVelocity.x;
            } else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
                level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
            } else {
                if (Gdx.app.getType() != Application.ApplicationType.Desktop) {
                    level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
                }
            }

            if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.SPACE)) {
                level.bunnyHead.setJumping(true);
            } else {
                level.bunnyHead.setJumping(false);
            }
        }
    }
}

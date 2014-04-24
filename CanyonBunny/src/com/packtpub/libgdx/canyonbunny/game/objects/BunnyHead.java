package com.packtpub.libgdx.canyonbunny.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.packtpub.libgdx.canyonbunny.game.Assets;
import com.packtpub.libgdx.canyonbunny.util.CharacterSkin;
import com.packtpub.libgdx.canyonbunny.util.Constants;
import com.packtpub.libgdx.canyonbunny.util.GamePreferences;

/**
 * Created by heschlie on 3/21/14.
 */
public class BunnyHead extends AbstractGameObject {
    public static final String TAG = BunnyHead.class.getName();

    private final float JUMP_TIME_MAX = 0.3f;
    private final float JUMP_TIME_MIN = 0.1f;
    private final float JUMP_TIME_OFFSET_FLYING = JUMP_TIME_MAX - 0.018f;
    public ParticleEffect dustParticles = new ParticleEffect();

    public enum VIEW_DIRECTION {LEFT, RIGHT}
    public enum JUMP_STATE {GROUNDED, FALLING, JUMP_RISING, JUMP_FALLING}

    private TextureRegion regHead;
    public VIEW_DIRECTION viewDirection;
    public float timeJumping;
    public JUMP_STATE jumpState;
    public boolean hasFeatherPowerUp;
    public float timeLeftFeatherPowerUp;

    public BunnyHead() {
        init();
    }

    public void init() {
        dimension.set(1, 1);
        regHead = Assets.instance.bunny.head;
        origin.set(dimension.x / 2, dimension.y / 2);
        bounds.set(0, 0, dimension.x, dimension.y);
        terminalVelocity.set(3f, 4f);
        friction.set(12f, 0f);
        acceleration.set(0f, -25f);
        viewDirection = VIEW_DIRECTION.RIGHT;
        jumpState = JUMP_STATE.FALLING;
        timeJumping = 0;
        hasFeatherPowerUp = false;
        timeLeftFeatherPowerUp = 0;

        // particle effects
        dustParticles.load(Gdx.files.internal("data/particles/dust.pfx"), Gdx.files.internal("data/particles"));
    }

    public void setJumping(boolean jumpKeyPressed) {
        switch (jumpState) {
            case GROUNDED:
                if (jumpKeyPressed) {
                    timeJumping = 0;
                    jumpState = JUMP_STATE.JUMP_RISING;
                }
                break;
            case JUMP_RISING:
                if (!jumpKeyPressed) {
                    jumpState = JUMP_STATE.FALLING;
                }
                break;
            case FALLING:
            case JUMP_FALLING:
                if (jumpKeyPressed && hasFeatherPowerUp) {
                    timeJumping = JUMP_TIME_OFFSET_FLYING;
                    jumpState = JUMP_STATE.JUMP_RISING;
                }
                break;
        }
    }

    public void setFeatherPowerUp(boolean pickedUp) {
        hasFeatherPowerUp = pickedUp;
        if (pickedUp) {
            timeLeftFeatherPowerUp = Constants.ITEM_FEATHER_POWERUP_DURATION;
        }
    }

    public boolean hasFeatherPowerUp() {
        return hasFeatherPowerUp && timeLeftFeatherPowerUp > 0;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (velocity.x != 0) {
            viewDirection = velocity.x < 0 ?VIEW_DIRECTION.LEFT : VIEW_DIRECTION.RIGHT;
        }

        if (timeLeftFeatherPowerUp > 0) {
            timeLeftFeatherPowerUp -= deltaTime;
            if (timeLeftFeatherPowerUp < 0) {
                timeLeftFeatherPowerUp = 0;
                setFeatherPowerUp(false);
            }
        }
        dustParticles.update(deltaTime);
    }

    @Override
    protected void updateMotionY(float deltaTime) {
        switch (jumpState) {
            case GROUNDED:
                jumpState = JUMP_STATE.FALLING;
                if (velocity.x != 0) {
                    dustParticles.setPosition(position.x + dimension.x / 2, position.y);
                    dustParticles.start();
                }
                break;
            case JUMP_RISING:
                timeJumping += deltaTime;
                if (timeJumping <= JUMP_TIME_MAX) {
                    velocity.y = terminalVelocity.y;
                }
                break;
            case FALLING:
                break;
            case JUMP_FALLING:
                timeJumping += deltaTime;
                if (timeJumping > 0 && timeJumping <= JUMP_TIME_MIN) {
                    velocity.y = terminalVelocity.y;
                }
        }
        if (jumpState != JUMP_STATE.GROUNDED)
            dustParticles.allowCompletion();
            super.updateMotionY(deltaTime);
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion reg = null;

        //Draw particles
        dustParticles.draw(batch);

        batch.setColor(CharacterSkin.values()[GamePreferences.instance.charSkin].getColor());

        if (hasFeatherPowerUp)
            batch.setColor(1f, .8f, 0f, 1f);
        reg = regHead;
        batch.draw(reg.getTexture(),
                position.x, position.y,
                origin.x, origin.y,
                dimension.x, dimension.y,
                scale.x, scale.y,
                rotation,
                reg.getRegionX(), reg.getRegionY(),
                reg.getRegionWidth(), reg.getRegionHeight(),
                false, false);
        batch.setColor(1, 1, 1, 1);
    }
}

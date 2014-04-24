package com.packtpub.libgdx.canyonbunny.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.packtpub.libgdx.canyonbunny.game.Assets;
import org.omg.CORBA.BAD_TYPECODE;

/**
 * Created by heschlie on 3/21/14.
 */
public class Mountains extends AbstractGameObject{

    private TextureRegion regMountainLeft;
    private TextureRegion regMountainRight;
    private int length;

    public Mountains (int length) {
        this.length = length;
        init();
    }

    private void init() {
        dimension.set(10, 2);

        regMountainLeft = Assets.instance.levelDecoration.mountainLeft;
        regMountainRight = Assets.instance.levelDecoration.mountainRight;

        origin.x = -dimension.x * 2;
        length += dimension.x * 2;
    }

    private void drawMountain (SpriteBatch batch, float offsetX, float offsetY, float tintColor) {
        TextureRegion reg = null;
        batch.setColor(tintColor, tintColor, tintColor, 1);
        float xRel = dimension.x * offsetX;
        float yRel = dimension.y * offsetY;

        int mountainLength = 0;
        mountainLength += MathUtils.ceil(length / (2 * dimension.x));
        mountainLength += MathUtils.ceil(.5f + offsetX);

        for (int i = 0; i < mountainLength; i++) {
            reg = regMountainLeft;
            batch.draw(reg.getTexture(),
                    origin.x + xRel, position.y + origin.y + yRel,
                    origin.x, origin.y,
                    dimension.x, dimension.y,
                    scale.x, scale.y,
                    rotation,
                    reg.getRegionX(), reg.getRegionY(),
                    reg.getRegionWidth(), reg.getRegionHeight(),
                    false, false);
            xRel += dimension.x;

            reg = regMountainRight;
            batch.draw(reg.getTexture(),
                    origin.x + xRel, position.y + origin.y + yRel,
                    origin.x, origin.y,
                    dimension.x, dimension.y,
                    scale.x, scale.y,
                    rotation,
                    reg.getRegionX(), reg.getRegionY(),
                    reg.getRegionWidth(), reg.getRegionHeight(),
                    false, false);

            xRel += dimension.x;
        }
        batch.setColor(1, 1, 1, 1);
    }

    @Override
    public void render(SpriteBatch batch) {
        drawMountain(batch, .5f, .5f, .5f);
        drawMountain(batch, .25f, .25f, .7f);
        drawMountain(batch, 0f, 0f, .9f);
    }
}

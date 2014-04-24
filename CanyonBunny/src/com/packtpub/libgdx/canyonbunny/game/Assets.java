package com.packtpub.libgdx.canyonbunny.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.packtpub.libgdx.canyonbunny.util.Constants;
import com.sun.media.jfxmediaimpl.MediaDisposer;

/**
 * Created by heschlie on 3/14/14.
 */
public class Assets implements MediaDisposer.Disposable, AssetErrorListener {

    public static final String TAG = Assets.class.getName();
    public static final Assets instance = new Assets();
    private AssetManager assetmanager;
    public AssetBunny bunny;
    public AssetRock rock;
    public AssetGoldCoin goldCoin;
    public AssetFeather feather;
    public AssetLevelDecoration levelDecoration;
    public AssetFonts fonts;

    private Assets() {}

    public void init(AssetManager assetmanager) {
        this.assetmanager = assetmanager;
        assetmanager.setErrorListener(this);
        assetmanager.load(Constants.TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);
        assetmanager.finishLoading();
        Gdx.app.debug(TAG, "# of assets loaded: " + assetmanager.getAssetNames().size);
        for (String a : assetmanager.getAssetNames()) {
            Gdx.app.debug(TAG, "asset: " + a);
        }

        TextureAtlas atlas = assetmanager.get(Constants.TEXTURE_ATLAS_OBJECTS);
        for (Texture t : atlas.getTextures()) {
            t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }

        fonts = new AssetFonts();
        bunny = new AssetBunny(atlas);
        rock = new AssetRock(atlas);
        goldCoin = new AssetGoldCoin(atlas);
        feather = new AssetFeather(atlas);
        levelDecoration = new AssetLevelDecoration(atlas);
    }

    public class AssetBunny {
        public final AtlasRegion head;
        public AssetBunny (TextureAtlas atlas) {
            head = atlas.findRegion("bunny_head");
        }
    }

    public class AssetRock {
        public final AtlasRegion edge;
        public final AtlasRegion middle;
        public AssetRock (TextureAtlas atlas) {
            edge = atlas.findRegion("rock_edge");
            middle = atlas.findRegion("rock_middle");
        }
    }

    public class AssetGoldCoin {
        public final AtlasRegion goldCoin;
        public AssetGoldCoin (TextureAtlas atlas) {
            goldCoin = atlas.findRegion("item_gold_coin");
        }
    }

    public class AssetFeather {
        public final AtlasRegion feather;
        public AssetFeather (TextureAtlas atlas) {
            feather = atlas.findRegion("item_feather");
        }
    }

    public class AssetLevelDecoration {
        public final AtlasRegion cloud01;
        public final AtlasRegion cloud02;
        public final AtlasRegion cloud03;
        public final AtlasRegion mountainLeft;
        public final AtlasRegion mountainRight;
        public final AtlasRegion waterOverlay;

        public AssetLevelDecoration (TextureAtlas atlas) {
            cloud01 = atlas.findRegion("cloud01");
            cloud02 = atlas.findRegion("cloud02");
            cloud03 = atlas.findRegion("cloud03");
            mountainLeft = atlas.findRegion("mountain_left");
            mountainRight = atlas.findRegion("mountain_right");
            waterOverlay = atlas.findRegion("water_overlay");
        }
    }

    @Override
    public void dispose() {
        assetmanager.dispose();
        fonts.defaultSmall.dispose();
        fonts.defaultNormal.dispose();
        fonts.defaultBig.dispose();
    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        Gdx.app.error(TAG, "Couldn't load asset 'some file'", (Exception)throwable);
    }

    public class AssetFonts {
        public final BitmapFont defaultSmall;
        public final BitmapFont defaultNormal;
        public final BitmapFont defaultBig;

        public AssetFonts() {
            defaultSmall = new BitmapFont(Gdx.files.internal("data/images/arial-15.fnt"), true);
            defaultNormal = new BitmapFont(Gdx.files.internal("data/images/arial-15.fnt"), true);
            defaultBig = new BitmapFont(Gdx.files.internal("data/images/arial-15.fnt"), true);

            defaultSmall.setScale(.75f);
            defaultNormal.setScale(1f);
            defaultBig.setScale(2f);

            defaultSmall.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            defaultNormal.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            defaultBig.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
    }
}

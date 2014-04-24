package com.packtpub.libgdx.canyonbunny.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by heschlie on 4/9/2014.
 */
public class GamePreferences {
    public static final String TAG = GamePreferences.class.getName();

    public static final GamePreferences instance = new GamePreferences();

    public boolean sound;
    public boolean music;
    public float volSound;
    public float volMusic;
    public int charSkin;
    public boolean showFPSCounter;

    private Preferences prefs;

    private GamePreferences() {
        prefs = Gdx.app.getPreferences(Constants.PREFERENCES);
    }

    public void load() {
        sound = prefs.getBoolean("sound", true);
        music = prefs.getBoolean("music", true);
        volSound = MathUtils.clamp(prefs.getFloat("volSound", 0.5f), 0.0f, 1.0f);
        volMusic = MathUtils.clamp(prefs.getFloat("volMusic", 0.5f), 0.0f, 1.0f);
        charSkin = MathUtils.clamp(prefs.getInteger("charSkin", 0), 0, 2);
        showFPSCounter = prefs.getBoolean("showFpsCounter", false);
    }

    public void save() {
        prefs.putBoolean("sound", sound);
        prefs.putBoolean("music", music);
        prefs.putFloat("volSound", volSound);
        prefs.putFloat("volMusic", volMusic);
        prefs.putInteger("charSkin", charSkin);
        prefs.putBoolean("showFpsCounter", showFPSCounter);
        prefs.flush();
    }
}

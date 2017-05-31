package io.piotrjastrzebski.gdxuitest;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class UIApp extends Game {
	SpriteBatch batch;
	AssetManager assets;

	@Override
	public void create () {
		batch = new SpriteBatch();
		assets = new AssetManager();

		setScreen(new LoadScreen(this));
	}

	
	@Override
	public void dispose () {
		super.dispose();
		batch.dispose();
	}
}

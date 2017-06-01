package io.piotrjastrzebski.gdxuitest;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver.Resolution;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class UIApp extends Game {
	private final static String TAG = UIApp.class.getSimpleName();

	SpriteBatch batch;
	AssetManager assets;
	Resolution[] resolutions;
	Resolution selected;
	float resAlpha;
	@Override
	public void create () {
		batch = new SpriteBatch();
		resolutions = new Resolution[]{
				new Resolution(720, 1280, "x1"), // ie default
				new Resolution(1080, 1920, "x2"),
				new Resolution(1440, 2560, "x4"),
		};
		ResolutionFileResolver resolver = new ResolutionFileResolver(new InternalFileHandleResolver(), resolutions);
		assets = new AssetManager(resolver);
		Texture.setAssetManager(assets);
		select();
	}

	private void select() {
		Resolution next = ResolutionFileResolver.choose(resolutions);
		if (next != selected) {
			selected = next;
			Gdx.app.log(TAG, "Selected " + selected.folder + " (" + Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight() + ")");
			setScreen(new LoadScreen(this));
		}
	}

	float reload;
	@Override
	public void resize(int width, int height) {
		// instead of doing something reasonable, we will just restart everything and hope for the best
		// also, we delay the reload a bit, so we reload only afte we stopped resizing the window
		// this mostly affects macos, as resize events are continuous there
		if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
			reload = .2f;
		} else {
			reload = 1/60f;
		}
		super.resize(width, height);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (reload > 0) {
			reload -= Gdx.graphics.getDeltaTime();
			if (reload <= 0) {
				select();
			}
		}
		super.render();
	}

	@Override
	public void dispose () {
		super.dispose();
		// we need to remove the font we added manually, or it will get disposed twice and cause a crash
		if (assets.isLoaded(LoadScreen.SKIN, Skin.class)) {
			Skin skin = assets.get(LoadScreen.SKIN, Skin.class);
			skin.remove("default-font", BitmapFont.class);
		}
		assets.dispose();
		batch.dispose();
	}
}

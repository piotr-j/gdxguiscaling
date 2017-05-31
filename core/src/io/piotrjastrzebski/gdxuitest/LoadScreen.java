package io.piotrjastrzebski.gdxuitest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by PiotrJ on 31/05/2017.
 */
public class LoadScreen extends BaseScreen {
	public static final String SKIN = "ui/uiskin.json";
	public static final String FONT = "ui/default.ttf";
	boolean fontGenerated = false;
	AssetManager assets;
	public LoadScreen(UIApp app) {
		super(app);
		assets = app.assets;


		{
			// we are using Kumar One font
			FileHandleResolver resolver = assets.getFileHandleResolver();
			assets.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
			assets.setLoader(BitmapFont.class, new FreetypeFontLoader(resolver));
			FreetypeFontLoader.FreeTypeFontLoaderParameter parameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
			parameter.fontFileName = FONT;
			parameter.fontParameters = new FreeTypeFontGenerator.FreeTypeFontParameter();
			// pick a size based on resolution
			float h = Gdx.graphics.getBackBufferWidth() > Gdx.graphics.getBackBufferHeight()?Gdx.graphics.getBackBufferHeight():Gdx.graphics.getBackBufferWidth();
			parameter.fontParameters.size = (int)(h/16);
			parameter.fontParameters.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
			assets.load(FONT, BitmapFont.class, parameter);
		}
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(.3f, .3f, .3f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (assets.update()) {
			if (fontGenerated) {
				// we probably dont need this to hang around
				assets.unload(FONT+".gen");
				app.setScreen(new GameScreen(app));
			} else {
				fontGenerated = true;
				BitmapFont font = assets.get(FONT, BitmapFont.class);
				final ObjectMap<String, Object> resources = new ObjectMap<>();
				resources.put("default-font", font);
				SkinLoader.SkinParameter parameter = new SkinLoader.SkinParameter(resources);
				assets.load(SKIN, Skin.class, parameter);
			}
		}
	}

}

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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;

/**
 * Created by PiotrJ on 31/05/2017.
 */
public class LoadScreen extends BaseScreen {
	public static final String SKIN = "ui/uiskin.json";
	public static final String FONT = "ui/default.ttf";
	public static final String SPINE = "spine/skeleton.json";
	boolean fontGenerated = false;
	AssetManager assets;
	public LoadScreen(UIApp app) {
		super(app);
		assets = app.assets;
		// we need to remove the font we added manually, or it will get disposed twice and cause a crash
		if (assets.isLoaded(SKIN, Skin.class)) {
			Skin skin = assets.get(SKIN, Skin.class);
			skin.remove("default-font", BitmapFont.class);
		}
		assets.clear();
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
			parameter.fontParameters.size = MathUtils.clamp((int)(h/16), 12, 96);
			parameter.fontParameters.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
			assets.load(FONT, BitmapFont.class, parameter);
		}
		{
			assets.setLoader(SkeletonData.class, new SkeletonDataLoader(assets.getFileHandleResolver()));
			// spine exports scaled stuff with a suffix, but we prefer folders...
//			String atlas = "spine/skeleton-" + app.selected.folder + ".atlas";
			String atlas = "spine/skeleton.atlas";
			assets.load(SPINE, SkeletonData.class, new SkeletonDataLoader.SkeletonDataLoaderParameter(atlas));
		}
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(.3f, .3f, .3f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (assets.update()) {
			if (fontGenerated) {
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

package io.piotrjastrzebski.gdxuitest;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.resolvers.ExternalFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.PrefixFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.spine.SkeletonData;

/**
 * Created by PiotrJ on 31/05/2017.
 */
public class LoadScreen extends BaseScreen {
	private static final String TAG = LoadScreen.class.getSimpleName();
	public static final String SKIN = "ui/uiskin.json";
	public static final String FONT = "ui/default.ttf";
	public static final String SPINE = "spine/skeleton.json";
	private String fontFileName;
	private final float size;
	boolean generateFont = true;
	boolean fontGenerated = false;
	AssetManager assets;
	;FreetypeFontLoader.FreeTypeFontLoaderParameter parameter;
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
			// pick a size based on resolution
			float h = Gdx.graphics.getBackBufferWidth() > Gdx.graphics.getBackBufferHeight()?Gdx.graphics.getBackBufferHeight():Gdx.graphics.getBackBufferWidth();
			size = MathUtils.clamp(h/16, 12, 96);

			FileHandle cache = cache("/cache/font-" + size + ".fnt");
			assets.setLoader(BitmapFont.class, ".fnt", new BitmapFontLoader(cacheResolver()));
			assets.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
			if (cache.exists()) {
				Gdx.app.log(TAG, "Loading cached font " + cache);
				// need to use external file
				generateFont = false;
				AssetDescriptor<BitmapFont> ad = new AssetDescriptor<>(cache, BitmapFont.class);
				fontFileName = ad.fileName;
				assets.load(ad);
			} else {
				Gdx.app.log(TAG, "Generating font");
				assets.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
				parameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
				parameter.fontFileName = fontFileName = FONT;
				parameter.fontParameters = new FreeTypeFontGenerator.FreeTypeFontParameter();
				parameter.fontParameters.size = (int) size;
				parameter.fontParameters.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
				parameter.fontParameters.shadowOffsetX = Math.max((int) (size/16), 1);
				parameter.fontParameters.shadowOffsetY = Math.max((int) (size/16), 1);
				// thats a lie, but we will get a reference to packer
				parameter.fontParameters.incremental = true;
				assets.load(FONT, BitmapFont.class, parameter);
			}
		}
		{
			assets.setLoader(SkeletonData.class, new SkeletonDataLoader(assets.getFileHandleResolver()));
			// spine exports scaled stuff with a suffix, but we prefer folders...
//			String atlas = "spine/skeleton-" + app.selected.folder + ".atlas";
			String atlas = "spine/skeleton.atlas";
			assets.load(SPINE, SkeletonData.class, new SkeletonDataLoader.SkeletonDataLoaderParameter(atlas));
		}
	}

	private FileHandle cache (String path) {
		// on desktop, we will use external so we dont pollute assets dir with generated junk
		if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
			return Gdx.files.external("guitest/"+path);
		}
		return Gdx.files.local(path);
	}

	private FileHandleResolver cacheResolver () {
		if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
			return new ExternalFileHandleResolver();
		}
		return new LocalFileHandleResolver();
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
				BitmapFont font = assets.get(fontFileName, BitmapFont.class);
				final ObjectMap<String, Object> resources = new ObjectMap<>();
				resources.put("default-font", font);
				SkinLoader.SkinParameter parameter = new SkinLoader.SkinParameter(resources);
				assets.load(SKIN, Skin.class, parameter);
				if (generateFont) {
					FileHandle cache = cache("/cache/font-" + size + ".fnt");
					Gdx.app.log(TAG, "Caching font data " + cache);
					Array<TextureRegion> regions = font.getRegions();
					Pixmap[] pages = new Pixmap[regions.size];
					for (int i = 0; i < pages.length; i++) {
						TextureData textureData = regions.get(i).getTexture().getTextureData();
						if (!textureData.isPrepared())
							textureData.prepare();
						pages[i] = textureData.consumePixmap();
					}
					BitmapFontWriter.writeFont(font.getData(), pages, cache, null);
				}
			}
		}
	}

}

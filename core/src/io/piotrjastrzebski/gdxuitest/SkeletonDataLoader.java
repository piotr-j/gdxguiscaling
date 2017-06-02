package io.piotrjastrzebski.gdxuitest;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.SkeletonBinary;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.attachments.AtlasAttachmentLoader;
import com.esotericsoftware.spine.attachments.AttachmentLoader;

/**
 * Created by PiotrJ on 02/06/2017.
 */
public class SkeletonDataLoader extends AsynchronousAssetLoader<SkeletonData, SkeletonDataLoader.SkeletonDataLoaderParameter> {
	protected SkeletonData skeletonData;

	public SkeletonDataLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override public void loadAsync (AssetManager manager, String fileName, FileHandle file,
									 SkeletonDataLoaderParameter parameter) {
		if (parameter == null) {
			throw new NullPointerException("SkeletonDataLoaderParameter cannot be null");
		}
		if (parameter.attachmentLoader == null && parameter.atlasName == null) {
			throw new IllegalArgumentException("Loader and atlas name cant be null at the same time!");
		}
		AttachmentLoader loader = parameter.attachmentLoader;
		if (loader == null) {
			TextureAtlas atlas = manager.get(parameter.atlasName, TextureAtlas.class);
			loader = new AtlasAttachmentLoader(atlas);
		}

		// there is no easy way to clone a skeleton data, so we will read same thing multiple times
		String extension = file.extension();
		if (extension.toLowerCase().equals("skel")) {
			SkeletonBinary skeletonBinary = new SkeletonBinary(loader);
			skeletonBinary.setScale(parameter.scale);
			skeletonData = skeletonBinary.readSkeletonData(file);
		} else {
			SkeletonJson skeletonJson = new SkeletonJson(loader);
			skeletonJson.setScale(parameter.scale);
			skeletonData = skeletonJson.readSkeletonData(file);
		}
	}

	@Override public SkeletonData loadSync (AssetManager manager, String fileName, FileHandle file,
											SkeletonDataLoaderParameter parameter) {
		if (parameter == null) {
			throw new NullPointerException("SkeletonDataLoaderParameter cannot be null");
		}
		return skeletonData;
	}

	@Override public Array<AssetDescriptor> getDependencies (String fileName, FileHandle file,
															 SkeletonDataLoaderParameter parameter) {
		if (parameter == null) {
			throw new NullPointerException("SkeletonDataLoaderParameter cannot be null");
		}
		Array<AssetDescriptor> deps = new Array<>();
		if (parameter.atlasName != null) {
			deps.add(new AssetDescriptor(parameter.atlasName, TextureAtlas.class));
		}
		return deps;
	}

	/**
	 * Required parameter to be passed to {@link AssetManager#load(String, Class, AssetLoaderParameters)}.
	 * Atlas name or custom attachment loader must be set
	 */
	static public class SkeletonDataLoaderParameter extends AssetLoaderParameters<SkeletonData> {
		public String atlasName;
		public float scale;
		public AttachmentLoader attachmentLoader;

		public SkeletonDataLoaderParameter () {
			this((String)null, 1);
		}

		public SkeletonDataLoaderParameter (String atlasName) {
			this(atlasName, 1);
		}

		public SkeletonDataLoaderParameter (String atlasName, float scale) {
			this(atlasName, scale, null);
		}

		public SkeletonDataLoaderParameter (AttachmentLoader attachmentLoader) {
			this(null, 1, attachmentLoader);
		}

		public SkeletonDataLoaderParameter (AttachmentLoader attachmentLoader, float scale) {
			this(null, scale, attachmentLoader);
		}

		protected SkeletonDataLoaderParameter (String atlasName, float scale, AttachmentLoader attachmentLoader) {
			this.atlasName = atlasName;
			this.scale = scale;
			this.attachmentLoader = attachmentLoader;
		}
	}
}


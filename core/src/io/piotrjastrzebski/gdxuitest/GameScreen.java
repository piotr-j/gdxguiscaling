package io.piotrjastrzebski.gdxuitest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by PiotrJ on 31/05/2017.
 */
public class GameScreen extends BaseScreen {
	ScreenViewport viewport;
	OrthographicCamera camera;
	Stage stage;
	Table root;
	public GameScreen(UIApp app) {
		super(app);
		camera = new OrthographicCamera();
		viewport = new ScreenViewport(camera);
		stage = new Stage(viewport, app.batch);
		root = new Table();
		root.setFillParent(true);
		stage.addActor(root);

		Skin skin = app.assets.get(LoadScreen.SKIN);

		Label label = new Label("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890", skin);
		label.setWrap(true);
		root.add(label).expand().fillX().pad(100);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(.3f, .5f, .3f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}
}

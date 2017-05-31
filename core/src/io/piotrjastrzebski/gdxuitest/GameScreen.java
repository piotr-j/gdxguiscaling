package io.piotrjastrzebski.gdxuitest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by PiotrJ on 31/05/2017.
 */
public class GameScreen extends BaseScreen {
	ScreenViewport viewport;
	OrthographicCamera camera;
	Stage stage;
	Table root;
	Skin skin;
	public GameScreen(UIApp app) {
		super(app);
		camera = new OrthographicCamera();
		viewport = new ScreenViewport(camera);
		stage = new Stage(viewport, app.batch);
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, this));
		root = new Table();
		root.setFillParent(true);
		stage.addActor(root);

		skin = app.assets.get(LoadScreen.SKIN);

		Label label = new Label("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890", skin);
		label.setWrap(true);
		root.add(label).expand().fillX().pad(100);
		root.row();
		TextButton button = new TextButton("Click me!", skin, "toggle");
		root.add(button);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(.3f, .5f, .3f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Dialog dialog = new Dialog("WELP!", skin);
		dialog.text("Some text?!");
		dialog.button("DO STUFF");
		dialog.button("DONT DO STUFF");
		dialog.show(stage);
		return super.touchUp(screenX, screenY, pointer, button);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}
}

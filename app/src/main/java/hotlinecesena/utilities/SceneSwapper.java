package hotlinecesena.utilities;

import java.io.IOException;

import hotlinecesena.model.dataccesslayer.JSONDataAccessLayer;
import hotlinecesena.view.loader.ImageType;
import hotlinecesena.view.loader.ProxyImage;
import hotlinecesena.view.loader.SceneType;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SceneSwapper {
	
	private static final int WIDTH = 1200;
	private static final int HEIGHT = 800;
	
	private ProxyImage proxyImage = new ProxyImage();
	
	public void swapScene(Initializable controller, String fxml, Stage stage) throws IOException {
		FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource(
				JSONDataAccessLayer.getInstance().getGuiPath().getPath(fxml)));
		loader.setController(controller);
		Pane pane;
		pane = loader.load();
		stage.setWidth(WIDTH);
		stage.setHeight(HEIGHT);
		stage.setResizable(false);
		stage.setTitle("HotLine Cesena");
		stage.centerOnScreen();
		stage.setScene(new Scene(pane));
		stage.getIcons().add(proxyImage.getImage(SceneType.MENU, ImageType.ICON));
	}
}
